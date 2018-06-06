package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.os.Bundle
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @date 2018/5/16 3:54 PM
 * @author KaySaith
 */
class GasSelectionPresenter(
	override val fragment: GasSelectionFragment
) : BasePresenter<GasSelectionFragment>() {
	
	private var gasFeeFromCustom: () -> GasFee? = {
		fragment.arguments?.getSerializable(ArgumentKey.gasEditor) as? GasFee
	}
	private val prepareModel by lazy {
		fragment.arguments?.getSerializable(ArgumentKey.gasPrepareModel) as? PaymentPrepareModel
	}
	private var currentMinnerType = MinerFeeType.Recommend.content
	private val defaultGasPrices by lazy {
		arrayListOf(
			BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei()), // cheap
			BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei()), // fast
			BigInteger.valueOf(MinerFeeType.Recommend.value.scaleToGwei()) // recommend
		)
	}
	private var currentGasUsedInEth: Double? = null
	
	fun insertCustomGasData() {
		val gasPrice = BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0).scaleToGwei())
		currentMinnerType = MinerFeeType.Custom.content
		if (defaultGasPrices.size == 4) {
			defaultGasPrices.remove(defaultGasPrices.last())
		}
		defaultGasPrices.add(gasPrice)
		fragment.clearGasLayout()
		generateGasSelections(fragment.getGasLayout())
	}
	
	fun generateGasSelections(parent: LinearLayout) {
		defaultGasPrices.forEachIndexed { index, minner ->
			GasSelectionCell(parent.context).apply {
				id = index
				model = GasSelectionModel(
					minner.toString().toDouble(), prepareGasLimit(minner.toDouble().toGwei()).toDouble(),
					currentMinnerType
				)
				if (model.isSelected) {
					getGasCurrencyPrice(model.count) {
						fragment.setSpendingValue(it)
					}
					/** 更新默认的燃气花销的 `ETH` 用于用户余额判断 */
					currentGasUsedInEth = getGasEthCount(model.count)
				}
			}.click {
				currentMinnerType = it.model.type
				updateGasSettings(parent)
				getGasCurrencyPrice(it.model.count) {
					fragment.setSpendingValue(it)
				}
				/** 更新当前选择的燃气花销的 `ETH` 用于用户余额判断 */
				currentGasUsedInEth = getGasEthCount(it.model.count)
			}.into(parent)
		}
	}
	
	fun goToGasEditorFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.showTargetFragment<GasEditorFragment>(TokenDetailText.customGas,
			                                                TokenDetailText.paymentValue, Bundle().apply {
				putLong(ArgumentKey.gasLimit, prepareModel?.gasLimit?.toLong().orElse(0))
			})
		}
	}
	
	fun confirmTransfer(footer: GasSelectionFooter, callback: () -> Unit) {
		val token = fragment.getParentFragment<TokenDetailOverlayFragment>()?.token
		// 如果输入的 `Decimal` 不合规就提示竞购并返回
		if (!getTransferCount().toString().checkDecimalIsvalid(token)) {
			callback()
			return
		}
		// 检查网络并执行转账操作
		NetworkUtil.hasNetworkWithAlert(fragment.context) isTrue {
			checkBalanceIsValid(token) {
				fragment.context?.runOnUiThread {
					isTrue {
						showConfirmAttentionView(footer, callback)
					} otherwise {
						footer.setCanUseStyle(false)
						fragment.context?.alert(AlertText.balanceNotEnough)
						callback()
					}
				}
			}
		}
	}
	
	private fun checkBalanceIsValid(
		token: WalletDetailCellModel?,
		hold: Boolean.() -> Unit
	) {
		// 如果是 `ETH`转账刚好就是判断站长金额加上燃气费费用
		if (token?.contract.equals(CryptoValue.ethContract, true)) {
			MyTokenTable.getBalanceWithContract(
				token?.contract!!,
				WalletTable.current.address,
				true
			) {
				hold(it >= getTransferCount().toDouble() + currentGasUsedInEth.orElse(0.0))
			}
		} else {
			// 如果当前站长不是 `ETH` 需要额外查询用户的 `ETH` 余额是否够支付当前燃气费用
			MyTokenTable.getBalanceWithContract(
				CryptoValue.ethContract,
				WalletTable.current.address,
				true
			) {
				hold(it >= getTransferCount().toDouble() + currentGasUsedInEth.orElse(0.0))
			}
		}
	}
	
	private fun getTransferCount(): BigDecimal {
		return prepareModel?.count?.toBigDecimal()
		       ?: BigDecimal.ZERO
	}
	
	/**
	 * 交易包括判断选择的交易燃气使用方式，以及生成签名并直接和链上交互.发起转账.
	 * 交易开始后进行当前 `taxHash` 监听判断是否完成交易.
	 */
	private fun transfer(password: String, callback: () -> Unit) {
		doAsync {
			// 获取当前账户的私钥
			fragment.context?.getPrivateKey(
				WalletTable.current.address, password, callback
			) { privateKey ->
				prepareModel?.apply {
					val raw = RawTransaction.createTransaction(
						nonce, getSelectedGasPrice(currentMinnerType),
						BigInteger.valueOf(prepareGasLimit(getSelectedGasPrice(currentMinnerType).toLong())),
						toAddress, countWithDecimal, inputData
					)
					// 准备秘钥格式
					val credentials = Credentials.create(privateKey)
					// 生成签名文件
					val signedMessage = TransactionEncoder.signMessage(raw, credentials)
					// 生成签名哈希值
					val hexValue = Numeric.toHexString(signedMessage)
					// 发起 `sendRawTransaction` 请求
					GoldStoneEthCall
						.sendRawTransaction(hexValue, { error, reason ->
							fragment.context?.alert(reason ?: error.toString())
						}) { taxHash ->
							LogUtil.debug(this.javaClass.simpleName, "taxHash: $taxHash")
							// 如 `nonce` 或 `gas` 导致的失败 `taxHash` 是错误的
							taxHash.isValidTaxHash() isTrue {
								// 把本次交易先插入到数据库, 方便用户从列表也能再次查看到处于 `pending` 状态的交易信息
								insertPendingDataToTransactionTable(toWalletAddress, raw!!, taxHash)
							}
							// 主线程跳转到账目详情界面
							fragment.context?.runOnUiThread {
								goToTransactionDetailFragment(toWalletAddress, raw!!, taxHash)
								callback()
							}
						}
				}
			}
		}
	}
	
	/**
	 * 转账开始后跳转到转账监听界面
	 */
	private fun goToTransactionDetailFragment(
		toWalletAddress: String,
		raw: RawTransaction,
		taxHash: String
	) {
		// 准备跳转到下一个界面
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			// 如果有键盘收起键盘
			activity?.apply { SoftKeyboard.hide(this) }
			removeChildFragment(fragment)
			val model = ReceiptModel(
				toWalletAddress, raw.gasLimit, raw.gasPrice, raw.value, token!!, taxHash,
				System.currentTimeMillis(), prepareModel?.memo
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
	
	private fun insertPendingDataToTransactionTable(
		toWalletAddress: String,
		raw: RawTransaction,
		taxHash: String
	) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			TransactionTable().apply {
				isReceive = false
				symbol = token!!.symbol
				timeStamp =
					(System.currentTimeMillis() / 1000).toString() // 以太坊返回的是 second, 本地的是 mills 在这里转化一下
				fromAddress = WalletTable.current.address
				value = CryptoUtils.toCountByDecimal(raw.value.toDouble(), token!!.decimal).formatCount()
				hash = taxHash
				gasPrice = getSelectedGasPrice(currentMinnerType).toString()
				gasUsed = raw.gasLimit.toString()
				isPending = true
				recordOwnerAddress = WalletTable.current.address
				tokenReceiveAddress = toWalletAddress
				isERC20 = token!!.symbol == CryptoSymbol.eth
				nonce = raw.nonce.toString()
				to = raw.to
				input = raw.data
				contractAddress = token!!.contract
				chainID = GoldStoneApp.getCurrentChain()
			}.let {
				GoldStoneDataBase.database.transactionDao().insert(it)
			}
		}
	}
	
	private fun getSelectedGasPrice(type: String): BigInteger {
		return when (type) {
			MinerFeeType.Fast.content -> BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei())
			MinerFeeType.Cheap.content -> BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei())
			MinerFeeType.Recommend.content -> BigInteger.valueOf(
				MinerFeeType.Recommend.value.scaleToGwei()
			)
			else -> BigInteger.valueOf(MinerFeeType.Custom.value.scaleToGwei())
		}
	}
	
	private fun String.checkDecimalIsvalid(token: WalletDetailCellModel?): Boolean {
		return when {
			getDecimalCount().isNull() -> return true
			
			getDecimalCount().orZero() > token?.decimal.orElse(0.0) -> {
				fragment.context?.alert(AlertText.transferWrongDecimal)
				false
			}
			
			else -> true
		}
	}
	
	private fun showConfirmAttentionView(
		footer: GasSelectionFooter,
		callback: () -> Unit
	) {
		fragment.context?.showAlertView(
			TransactionText.confirmTransaction,
			CommonText.enterPassword.toUpperCase(), true, {
				// 点击 `Alert` 取消按钮
				footer.getConfirmButton { showLoadingStatus(false) }
			}) {
			transfer(it?.text.toString(), callback)
		}
	}
	
	private fun prepareGasLimit(gasPrice: Long): Long {
		return if (gasPrice == MinerFeeType.Custom.value) gasFeeFromCustom()?.gasLimit.orElse(0)
		else prepareModel?.gasLimit?.toLong().orElse(0)
	}
	
	private fun getGasEthCount(info: String): Double {
		return if (info.length > 3) {
			info.replace(" ", "").substring(0, info.lastIndex - 3).toDouble()
		} else {
			0.0
		}
	}
	
	private fun getGasCurrencyPrice(
		info: String,
		hold: (String) -> Unit
	) {
		DefaultTokenTable.getCurrentChainTokenByContract(CryptoValue.ethContract) {
			hold(
				"≈ " + (getGasEthCount(info) * it?.price.orElse(0.0)).formatCurrency() + " " + GoldStoneApp.getCurrencyCode()
			)
		}
	}
	
	private fun updateGasSettings(container: LinearLayout) {
		defaultGasPrices.forEachIndexed { index, minner ->
			container.findViewById<GasSelectionCell>(index)?.let { cell ->
				cell.model = GasSelectionModel(
					minner.toDouble(), prepareGasLimit(minner.toDouble().toGwei()).toDouble(),
					currentMinnerType
				)
			}
		}
	}
	
	override fun onFragmentShowFromHidden() {
		/** 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件 */
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			overlayView.header.showBackButton(true) {
				backEvent(this@apply)
			}
			// 有可能从 `WebViewFragment` 返回 需要重新恢复 `ValueHeader`
			presenter.setValueHeader(token)
		}
	}
	
	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			presenter.setValueHeader(token)
			headerTitle = TokenDetailText.paymentValue
			presenter.popFragmentFrom<GasSelectionFragment>()
		}
	}
}