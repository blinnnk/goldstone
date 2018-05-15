package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter

import android.os.Bundle
import android.util.Log
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.APIPath
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailCell
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailHeaderView
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.Serializable
import java.math.BigInteger
import kotlin.math.max

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

data class GasFee(
	var gasLimit: Long,
	val gasPrice: Long
) : Serializable

class PaymentValueDetailPresenter(
	override val fragment: PaymentValueDetailFragment
) : BaseRecyclerPresenter<PaymentValueDetailFragment, PaymentValueDetailModel>() {

	private var minerFeeType = MinerFeeType.Recommend.content

	private var currentNonce: BigInteger? = null
	private var gasFeeFromCustom: () -> GasFee? = {
		fragment.arguments?.getSerializable(ArgumentKey.gasEditor) as? GasFee
	}

	private val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))
	private val defaultGasPrices by lazy {
		arrayListOf(
			BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei()), // cheap
			BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei()), // fast
			BigInteger.valueOf(MinerFeeType.Recommend.value.scaleToGwei()) // recommend
		)
	}

	override fun updateData() {
		fragment.apply {
			// 数据在异步计算, 先生成空的数据占位, 避免页面抖动
			generateEmptyData()
			// 计算三种 `Gas` 设置的值, 初始默认的 `count` 设定为 `0`
			prepareRawTransactionByGasPrices(0.00000001) {
				context?.runOnUiThread {
					fragment.asyncData?.clear()
					fragment.asyncData?.addAll(generateModels(it, defaultGasPrices))
					fragment.recyclerView.adapter.notifyItemRangeChanged(1, it.size)
					hasCalculated = true
				}
			}
		}
	}

	override fun updateParentContentLayoutHeight(
		dataCount: Int?,
		cellHeight: Int,
		maxHeight: Int
	) {
		setHeightMatchParent()
	}

	/**
	 * 随着用户更改需要转出的数字的时候动态更新副标题对应的 `currency` 价值.
	 */
	fun updateHeaderValue(header: PaymentValueDetailHeaderView) {
		header.apply {
			inputTextListener { count ->
				count.isNotEmpty() isTrue {
					updateCurrencyValue(count.toDouble() * fragment.token?.price.orElse(0.0))
					// 根据数量更新 `Transaction`
					if (defaultGasPrices.size > 3) {
						minerFeeType = MinerFeeType.Recommend.content
					}
					updateTransactionAndAdapter(count.toDouble()) {
						fragment.recoveryWhenModifyCount()
					}
				} otherwise {
					updateCurrencyValue(0.0)
					updateTransactionAndAdapter(0.0)
				}
			}
		}
	}

	/**
	 * 自定义燃气费的 `gasLimit` 会根据 `inputCode` 的值变化实时变化, 如果用户先修改 `gas custom`
	 * 再修改 `count` 需要抚慰 `custom settings` 不然会转账失败
	 */
	private fun PaymentValueDetailFragment.recoveryWhenModifyCount() {
		if (defaultGasPrices.size > 3) {
			context?.alert(AlertText.modifyCountAfoterCustomGas)
			asyncData?.remove(fragment.asyncData?.last())
			minerFeeType = MinerFeeType.Recommend.content
			recyclerView.adapter.notifyItemRemoved(4)
			defaultGasPrices.remove(defaultGasPrices.last())
		}
	}

	/**
	 * 交易包括判断选择的交易燃气使用方式，以及生成签名并直接和链上交互.发起转账.
	 * 交易开始后进行当前 `taxHash` 监听判断是否完成交易.
	 */
	fun transfer(
		password: String,
		callback: () -> Unit
	) {
		doAsync {
			// 获取当前账户的私钥
			fragment.context?.getPrivateKey(WalletTable.current.address, password) { privateKey ->

				val raw = fragment.asyncData?.getRawTransactionByMinerType(minerFeeType)
				// 准备秘钥格式
				val credentials = Credentials.create(privateKey)
				// 生成签名文件
				val signedMessage = TransactionEncoder.signMessage(raw, credentials)
				// 生成签名哈希值
				val hexValue = Numeric.toHexString(signedMessage)
				// 发起 `sendRawTransaction` 请求
				GoldStoneEthCall.sendRawTransaction(hexValue) { taxHash ->
					Log.d("DEBUG", "taxHash $taxHash")
					// 如 `nonce` 或 `gas` 导致的失败 `taxHash` 是错误的
					taxHash.isValidTaxHash() isTrue {
						// 把本次交易先插入到数据库, 方便用户从列表也能再次查看到处于 `pending` 状态的交易信息
						insertPendingDataToTransactionTable(raw!!, taxHash, fragment.token!!)
					}
					// 主线程跳转到账目详情界面
					fragment.context?.runOnUiThread {
						goToTransactionDetailFragment(fragment.address!!, raw!!, fragment.token!!, taxHash)
						callback()
					}
				}
			}
		}
	}

	private fun ArrayList<PaymentValueDetailModel>.getRawTransactionByMinerType(type: String): RawTransaction? {
		return find { it.type == type }?.rawTransaction
	}

	/**
	 * 业务实现的是随着用户每更新一个 `input` 信息重新测算 `gasLimit` 所以这个函数式用来
	 * 实时更新界面的数值的.
	 */
	var hasCalculated = false
	private var isItemCountChanged = true

	fun updateTransactionAndAdapter(
		value: Double,
		callback: () -> Unit = {}
	) {
		gasFeeFromCustom().isNull() isFalse {
			fragment.getMainActivity()?.showLoadingView()
			minerFeeType = MinerFeeType.Custom.content
		}
		prepareRawTransactionByGasPrices(value) {
			fragment.asyncData?.clear()
			fragment.asyncData?.addAll(generateModels(it, defaultGasPrices))
			// 根据数据的变化决定采用不同的更新 `item` 的方式
			if (it.size == 4 && isItemCountChanged) {
				fragment.recyclerView.adapter.notifyDataSetChanged()
				isItemCountChanged = false
			} else {
				fragment.recyclerView.adapter.notifyItemRangeChanged(1, it.size)
			}

			gasFeeFromCustom().isNull() isFalse {
				fragment.getMainActivity()?.removeLoadingView()
			}
			hasCalculated = true
			callback()
		}
	}

	/**
	 * 查询当前账户的可用 `nonce` 以及 `symbol` 的相关信息后, 生成对应不同速度的 `RawTransaction
	 */
	private fun prepareRawTransactionByGasPrices(
		value: Double,
		hold: (ArrayList<RawTransaction>) -> Unit
	) {
		// 获取当前账户在链上的 `nonce`， 这个行为比较耗时所以把具体业务和获取 `nonce` 分隔开
		currentNonce.isNull() isTrue {
			GoldStoneAPI.getTransactionListByAddress(WalletTable.current.address) {
				TransactionTable.getMyLatestNounce { localNounce ->
					val myLatestNonce = firstOrNull {
						it.fromAddress.equals(WalletTable.current.address, true)
					}?.nonce?.toLong()
					val chainNounce = if (myLatestNonce.isNull()) 0L
					else myLatestNonce!! + 1
					currentNonce =
						BigInteger.valueOf(max(chainNounce, if (localNounce.isNull()) 0 else localNounce!! + 1))
					generateTransaction(fragment.address!!, value, hold)
				}
			}
		} otherwise {
			generateTransaction(fragment.address!!, value, hold)
		}
	}

	/**
	 * 测量 `input` 测量 `gasLimit` 以及生成对应的 `RawTransaction`, [minGasLimit] 是
	 * 在做自定义 `Gas` 的时候传入的最小 `limit` 值
	 */
	private var minGasLimit = BigInteger.valueOf(0)

	private fun generateTransaction(
		toAddress: String,
		value: Double,
		hold: (ArrayList<RawTransaction>) -> Unit
	) {
		val count: BigInteger
		val data: String
		val to: String
		// `ETH` 转账和 `Token` 转账需要准备不同的 `Transaction`
		if (fragment.token?.symbol == CryptoSymbol.eth) {
			to = toAddress
			data = ""
			count = Convert.toWei(value.toString(), Convert.Unit.ETHER).toBigInteger()
		} else {
			to = fragment.token!!.contract
			count = BigInteger.valueOf((value * Math.pow(10.0, fragment.token!!.decimal)).toLong())
			data = SolidityCode.contractTransfer + toAddress.toDataStringFromAddress() +
				count.toDataString()
		}
		// 这个 `Transaction` 是用来测量估算可能要用的 `gasLimit` 不是用来转账用的.
		val transaction = Transaction(
			WalletTable.current.address, currentNonce, BigInteger.valueOf(0), BigInteger.valueOf(0), to,
			count, data
		)

		coroutinesTask({
			// 测量 `Transaction` 得出 `GasLimit`
			if (fragment.token?.symbol == CryptoSymbol.eth) BigInteger.valueOf(21000)
			else web3j.ethEstimateGas(transaction).sendAsync().get().amountUsed
		}) { limit ->
			minGasLimit = limit
			/** 如果有过自定义设置, 那么增加自定义设置的选项 */
			if (minerFeeType == MinerFeeType.Custom.content) {
				if (defaultGasPrices.size <= 3) {
					defaultGasPrices.add(
						BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0L).scaleToGwei())
					)
				} else {
					defaultGasPrices.remove(defaultGasPrices.last())
					defaultGasPrices.add(
						BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0L).scaleToGwei())
					)
				}
			}

			defaultGasPrices.map { price ->
				if (price == BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0).scaleToGwei())) {
					RawTransaction.createTransaction(
						currentNonce, price, BigInteger.valueOf(gasFeeFromCustom()?.gasLimit.orElse(0)), to,
						count, data
					)
				} else {
					RawTransaction.createTransaction(
						currentNonce, price, limit, to, count, data
					)
				}
			}.let {
				hold(it.toArrayList())
			}
		}
	}

	private fun generateModels(
		rawTransactions: ArrayList<RawTransaction>,
		gasPrices: ArrayList<BigInteger>
	) =
		rawTransactions.mapIndexed { index, it ->
			PaymentValueDetailModel(gasPrices[index].toDouble(), it, minerFeeType)
		}.toArrayList()

	private fun generateEmptyData() {
		fragment.asyncData =
			arrayListOf(PaymentValueDetailModel().apply { type = MinerFeeType.Cheap.content },
				PaymentValueDetailModel().apply { type = MinerFeeType.Fast.content },
				PaymentValueDetailModel().apply { type = MinerFeeType.Recommend.content })
	}

	private fun insertPendingDataToTransactionTable(
		raw: RawTransaction,
		taxHash: String,
		token: WalletDetailCellModel
	) {
		TransactionTable().apply {
			isReceive = false
			symbol = token.symbol
			timeStamp =
				(System.currentTimeMillis() / 1000).toString() // 以太坊返回的是 second, 本地的是 mills 在这里转化一下
			fromAddress = WalletTable.current.address
			value = CryptoUtils.toCountByDecimal(raw.value.toDouble(), token.decimal).formatCount()
			hash = taxHash
			gasPrice = raw.gasPrice.toString()
			gasUsed = raw.gasLimit.toString()
			isPending = true
			recordOwnerAddress = WalletTable.current.address
			tokenReceiveAddress = fragment.address
			isERC20 = token.symbol == CryptoSymbol.eth
			nonce = currentNonce.toString()
			to = raw.to
			input = raw.data
		}.let {
			GoldStoneDataBase.database.transactionDao().insert(it)
		}
	}

	/**
	 * 转账开始后跳转到转账监听界面
	 */
	private fun goToTransactionDetailFragment(
		address: String,
		raw: RawTransaction,
		token: WalletDetailCellModel,
		taxHash: String
	) {
		// 准备跳转到下一个界面
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			// 如果有键盘收起键盘
			activity?.apply { SoftKeyboard.hide(this) }
			removeChildFragment(fragment)
			val model = ReceiptModel(
				address, raw.gasLimit, raw.gasPrice, raw.value, token, taxHash, System.currentTimeMillis()
			)
			addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.transactionDetail, model)
			}
			overlayView.header.apply {
				showBackButton(false)
				showCloseButton(true)
			}
			headerTitle = TokenDetailText.transferDetail
		}
	}

	fun goToGasEditorFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.showTargetFragment<GasEditorFragment>(TokenDetailText.customGas,
				TokenDetailText.paymentValue, Bundle().apply {
					putLong(ArgumentKey.gasLimit, minGasLimit.toLong())
				})
		}
	}

	/**
	 * 点选燃气设置后更新界面
	 */
	fun setCellClickEvent(cell: PaymentValueDetailCell) {
		cell.apply {
			onClick {
				event()
				preventDuplicateClicks()
			}
		}
		cell.radioButton.apply {
			onClick {
				cell.event()
				preventDuplicateClicks()
			}
		}
	}

	// 更新 `RadioBox` 选中的状态
	private fun PaymentValueDetailCell.event() {
		minerFeeType = model.type
		fragment.asyncData?.forEachOrEnd { item, isEnd ->
			item.isSelected = item.type == model.type
			if (isEnd) fragment.recyclerView.adapter.notifyDataSetChanged()
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			overlayView.header.backButton.onClick {
				headerTitle = TokenDetailText.address
				presenter.popFragmentFrom<PaymentValueDetailFragment>()
				setHeightMatchParent()
			}
		}
	}
}