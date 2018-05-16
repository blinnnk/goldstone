package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orElse
import com.blinnnk.extension.removeChildFragment
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 * @date 2018/5/16 3:54 PM
 * @author KaySaith
 */

enum class MinerFeeType(
	val content: String,
	var value: Long
) {
	Recommend("recommend", 30),
	Cheap("cheap", 1),
	Fast("fast", 100),
	Custom("custom", 0)
}

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

	fun getTransferCount(): Double {
		return prepareModel?.count.orElse(0.0)
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
				prepareModel?.apply {
					val raw = RawTransaction.createTransaction(
						nounce, getSelectedGasPrice(currentMinnerType),
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
					GoldStoneEthCall.sendRawTransaction(hexValue) { taxHash ->
						Log.d("DEBUG", "taxHash $taxHash")
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
				toWalletAddress,
				raw.gasLimit,
				raw.gasPrice,
				raw.value,
				token!!,
				taxHash,
				System.currentTimeMillis(),
				prepareModel?.memo
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

	fun insertCustomGasData() {
		val gasPrice = BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0).scaleToGwei())
		currentMinnerType = MinerFeeType.Custom.content
		if(defaultGasPrices.size == 4) {
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
				}
			}.click {
				currentMinnerType = it.model.type
				updateGasSettings(parent)
				getGasCurrencyPrice(it.model.count) {
					fragment.setSpendingValue(it)
				}
			}.into(parent)
		}
	}

	private fun prepareGasLimit(gasPrice: Long): Long {
		return if (gasPrice == MinerFeeType.Custom.value) gasFeeFromCustom()?.gasLimit.orElse(0)
		else prepareModel?.gasLimit?.toLong().orElse(0)
	}

	fun goToGasEditorFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.showTargetFragment<GasEditorFragment>(
				TokenDetailText.customGas, TokenDetailText.paymentValue, Bundle().apply {
					putLong(ArgumentKey.gasLimit, prepareModel?.gasLimit?.toLong().orElse(0))
				})
		}
	}

	private fun getGasCurrencyPrice(
		info: String,
		hold: (String) -> Unit
	) {
		DefaultTokenTable.getTokenBySymbol(CryptoSymbol.eth) {
			val count = if (info.length > 3) {
				info.replace(" ", "").substring(0, info.lastIndex - 3).toDouble()
			} else {
				0.0
			}
			hold("≈ " + (count * it.price).formatCurrency() + " " + GoldStoneApp.currencyCode)
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
				presenter.setValueHeader(token)
				presenter.popFragmentFrom<GasSelectionFragment>()
				recoveryFragmentHeight()
			}
		}
	}
}