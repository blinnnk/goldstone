package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAfterColonContent
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.utils.CryptoUtils.toValueWithDecimal
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.crypto.utils.toDataString
import io.goldstone.blockchain.crypto.utils.toDataStringFromAddress
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import java.math.BigInteger

/**
 * @date 2018/5/15 10:19 PM
 * @author KaySaith
 */
class PaymentPreparePresenter(
	override val fragment: PaymentPrepareFragment
) : BasePresenter<PaymentPrepareFragment>() {
	
	private val rootFragment by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setSymbol()
	}
	
	fun goToGasEditorFragment(callback: () -> Unit) {
		val count = fragment.getTransferCount()
		if (count == 0.0) {
			fragment.context?.alert(AlertText.emptyTransferValue)
			callback()
		} else {
			fragment.toast(LoadingText.calculateGas)
			getPaymentPrepareModel(
				count,
				fragment.getMemoContent(),
				ChainURL.getChainTypeBySymbol(fragment.rootFragment?.token?.symbol.orEmpty()),
				callback
			) { model ->
				fragment.rootFragment?.apply {
					presenter.showTargetFragment<GasSelectionFragment>(
						TokenDetailText.customGas,
						TokenDetailText.paymentValue,
						Bundle().apply {
							putSerializable(ArgumentKey.gasPrepareModel, model)
						})
					callback()
				}
			}
		}
	}
	
	/**
	 * 查询当前账户的可用 `nonce` 以及 `symbol` 的相关信息后, 生成 `Recommond` 的 `RawTransaction`
	 */
	private fun getPaymentPrepareModel(
		count: Double,
		memo: String,
		chainType: ChainType,
		callback: () -> Unit,
		hold: (PaymentPrepareModel) -> Unit
	) {
		GoldStoneEthCall.getUsableNonce(
			{ error, reason ->
				fragment.context?.alert(reason ?: error.toString().showAfterColonContent())
			},
			chainType
		) {
			generateTransaction(fragment.address!!, count, memo, it, callback, hold)
		}
	}
	
	private fun generateTransaction(
		toAddress: String,
		count: Double,
		memo: String,
		nonce: BigInteger,
		callback: () -> Unit,
		hold: (PaymentPrepareModel) -> Unit
	) {
		val countWithDecimal: BigInteger
		val data: String
		val to: String
		// `ETH`, `ETC` 和 `Token` 转账需要准备不同的 `Transaction`
		when {
			rootFragment?.token?.contract.equals(CryptoValue.ethContract, true)
				or rootFragment?.token?.contract.equals(CryptoValue.etcContract, true) -> {
				to = toAddress
				data = if (memo.isEmpty()) "0x" else "0x" + memo.toCryptHexString() // Memo
				countWithDecimal = toValueWithDecimal(count)
			}
			
			else -> {
				to = rootFragment?.token!!.contract
				countWithDecimal = toValueWithDecimal(count, rootFragment?.token?.decimal!!)
				data = SolidityCode.contractTransfer + // 方法
					toAddress.toDataStringFromAddress() + // 地址
					countWithDecimal.toDataString() + // 数量
					if (memo.isEmpty()) "" else memo.toCryptHexString() // Memo
			}
		}
		GoldStoneEthCall.getTransactionExecutedValue(
			to,
			Config.getCurrentAddress(),
			data,
			{ error, reason ->
				fragment.context?.alert(reason ?: error.toString())
				callback()
			},
			ChainURL.getChainNameBySymbol(rootFragment?.token?.symbol.orEmpty())
		) { limit ->
			hold(
				PaymentPrepareModel(
					nonce,
					limit,
					to,
					countWithDecimal,
					count,
					data,
					fragment.address!!,
					fragment.getMemoContent()
				)
			)
		}
	}
	
	private fun setSymbol() {
		fragment.setSymbolAndPrice(
			rootFragment?.token?.symbol.orEmpty(),
			rootFragment?.token?.price?.formatCurrency().orEmpty() + " " + Config.getCurrencyCode()
		)
	}
	
	override fun onFragmentShowFromHidden() {
		rootFragment?.apply {
			overlayView.header.backButton.onClick {
				backEvent(this@apply)
			}
		}
	}
	
	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			headerTitle = TokenDetailText.address
			presenter.popFragmentFrom<PaymentPrepareFragment>()
		}
	}
}