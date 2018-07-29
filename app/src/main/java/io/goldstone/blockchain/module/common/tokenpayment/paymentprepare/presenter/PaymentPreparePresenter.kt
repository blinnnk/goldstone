package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

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
	
	fun getToken(): WalletDetailCellModel? {
		return rootFragment?.token
	}
	
	fun goToGasEditorFragment(callback: () -> Unit) {
		val count = fragment.getTransferCount()
		if (count == 0.0) {
			fragment.context?.alert(AlertText.emptyTransferValue)
			callback()
		} else {
			fragment.toast(LoadingText.calculateGas)
			if (getToken()?.symbol.equals(CryptoSymbol.btc, true)) {
				prepareBTCPaymentModel(count, fragment.getChangeAddress(), callback)
			} else {
				prepareETHERC20ETCPaymentModel(count, callback)
			}
		}
	}
	
	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			headerTitle = TokenDetailText.address
			presenter.popFragmentFrom<PaymentPrepareFragment>()
		}
	}
	
	override fun onFragmentShowFromHidden() {
		rootFragment?.apply {
			overlayView.header.backButton.onClick {
				backEvent(this@apply)
			}
		}
	}
	
	private fun setSymbol() {
		fragment.setSymbolAndPrice(
			rootFragment?.token?.symbol.orEmpty(),
			rootFragment?.token?.price?.formatCurrency().orEmpty() + " " + Config.getCurrencyCode()
		)
	}
}