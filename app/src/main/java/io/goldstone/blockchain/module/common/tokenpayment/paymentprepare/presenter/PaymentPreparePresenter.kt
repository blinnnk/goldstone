package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.web3j.utils.Convert
import java.math.BigInteger

/**
 * @date 2018/5/15 10:19 PM
 * @author KaySaith
 */
class PaymentPreparePresenter(
	override val fragment: PaymentPrepareFragment
) : BasePresenter<PaymentPrepareFragment>() {
	
	private var currentToken: WalletDetailCellModel? = null
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setSymbol()
	}
	
	fun goToGasEditorFragment(callback: () -> Unit) {
		val count = fragment.getTransferCount()
		if (count == 0.0) {
			fragment.context?.alert(TokenDetailText.setTransferCountAlert)
			callback()
		} else {
			fragment.toast("wait a few seconds, It is calculating transaction gas information")
			getPaymentPrepareModel(count, fragment.getMemoContent(), callback) { model ->
				fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
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
		value: Double,
		memo: String,
		callback: () -> Unit,
		hold: (PaymentPrepareModel) -> Unit
	) {
		TransactionTable.getLatestValidNounce(
			{
				fragment.context?.alert(it.toString())
			}) {
			generateTransaction(fragment.address!!, value, memo, it, callback, hold)
		}
	}
	
	private fun generateTransaction(
		toAddress: String,
		value: Double,
		memo: String,
		nounce: BigInteger,
		callback: () -> Unit,
		hold: (PaymentPrepareModel) -> Unit
	) {
		val countWithDecimal: BigInteger
		val data: String
		val to: String
		// `ETH` 转账和 `Token` 转账需要准备不同的 `Transaction`
		if (currentToken?.contract.equals(CryptoValue.ethContract, true)) {
			to = toAddress
			data = if (memo.isEmpty()) "0x" else "0x" + memo.toHexCode() // Memo
			countWithDecimal = Convert.toWei(value.toString(), Convert.Unit.ETHER).toBigInteger()
		} else {
			to = currentToken!!.contract
			countWithDecimal =
				BigInteger.valueOf((value * Math.pow(10.0, currentToken!!.decimal)).toLong())
			data = SolidityCode.contractTransfer + // 方法
				toAddress.toDataStringFromAddress() + // 地址
				countWithDecimal.toDataString() + // 数量
				if (memo.isEmpty()) "" else memo.toHexCode() // Memo
		}
		GoldStoneEthCall.getTransactionExecutedValue(
			to,
			WalletTable.current.address,
			data, { error, reason ->
				fragment.context?.alert(reason ?: error.toString())
				callback()
			}) { limit ->
			hold(
				PaymentPrepareModel(
					nounce,
					limit,
					to,
					countWithDecimal,
					value,
					data,
					fragment.address!!,
					fragment.getMemoContent()
				)
			)
		}
	}
	
	private fun setSymbol() {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			currentToken = token
			fragment.setSymbolAndPrice(
				currentToken?.symbol.orEmpty(),
				currentToken?.price?.formatCurrency().orEmpty() + " " + GoldStoneApp.getCurrencyCode()
			)
		}
	}
	
	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			overlayView.header.backButton.onClick {
				backEvent(this@getParentFragment)
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