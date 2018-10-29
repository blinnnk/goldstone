package io.goldstone.blockchain.module.common.webview.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */
class WebViewPresenter(
	override val fragment: WebViewFragment
) : BasePresenter<WebViewFragment>() {

	fun setBackEvent() {
		fragment.parentFragment?.apply {
			when (this) {
				is WalletImportFragment ->
					presenter.popFragmentFrom<WebViewFragment>()
				is BaseOverlayFragment<*> -> presenter.removeSelfFromActivity()
			}
		}
	}

	fun prepareBackEvent(callback: () -> Unit) {
		fragment.parentFragment?.apply {
			when (this) {
				is TokenDetailOverlayFragment ->
					presenter.popFragmentFrom<WebViewFragment>()
				is NotificationFragment ->
					presenter.popFragmentFrom<WebViewFragment>()
				is WalletGenerationFragment ->
					presenter.popFragmentFrom<WebViewFragment>()
				is QuotationOverlayFragment ->
					presenter.popFragmentFrom<WebViewFragment>()
				is WalletSettingsFragment ->
					presenter.popFragmentFrom<WebViewFragment>()
				is ProfileOverlayFragment -> {
					if (childFragmentManager.fragments.size > 1)
						presenter.popFragmentFrom<WebViewFragment>()
					else callback()
				}
				else -> callback()
			}
		}
	}
}