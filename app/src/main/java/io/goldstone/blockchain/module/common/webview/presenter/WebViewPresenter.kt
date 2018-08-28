package io.goldstone.blockchain.module.common.webview.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.NotificationText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
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
				is WalletImportFragment -> {
					headerTitle = ImportWalletText.importWallet
					presenter.popFragmentFrom<WebViewFragment>()
				}
			}
		}
	}

	fun prepareBackEvent() {
		fragment.parentFragment?.apply {
			when (this) {
				is TokenDetailOverlayFragment -> {
					headerTitle = TransactionText.detail
					presenter.popFragmentFrom<WebViewFragment>()
				}

				is NotificationFragment -> {
					headerTitle = NotificationText.notification
					presenter.popFragmentFrom<WebViewFragment>()
				}

				is WalletGenerationFragment -> {
					headerTitle = CreateWalletText.create
					presenter.popFragmentFrom<WebViewFragment>()
				}

				is QuotationOverlayFragment -> {
					presenter.popFragmentFrom<WebViewFragment>()
				}

				is WalletSettingsFragment -> {
					presenter.popFragmentFrom<WebViewFragment>()
				}
			}
		}
	}
}