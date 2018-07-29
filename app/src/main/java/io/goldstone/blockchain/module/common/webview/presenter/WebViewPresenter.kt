package io.goldstone.blockchain.module.common.webview.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.common.value.NotificationText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment

/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */
class WebViewPresenter(
	override val fragment: WebViewFragment
) : BasePresenter<WebViewFragment>() {
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		setBackEvent()
	}
	
	fun setBackEvent() {
		fragment.parentFragment?.apply {
			when (this) {
				is TransactionFragment -> {
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
				
				is WalletImportFragment -> {
					headerTitle = ImportWalletText.importWallet
					presenter.popFragmentFrom<WebViewFragment>()
				}
				
				is ProfileOverlayFragment -> {
					presenter.removeSelfFromActivity()
				}
			}
		}
	}
}