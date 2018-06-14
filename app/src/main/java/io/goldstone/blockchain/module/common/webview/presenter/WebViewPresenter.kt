package io.goldstone.blockchain.module.common.webview.presenter

import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
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
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.activity?.apply {
			when (this) {
				is SplashActivity -> backEvent = Runnable { setBackEvent() }
				is MainActivity -> backEvent = Runnable { setBackEvent() }
			}
		}
		if (fragment.parentFragment is ProfileOverlayFragment) {
			updateHeight(fragment.context?.getRealScreenHeight().orZero())
		}
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
				
				is TokenDetailOverlayFragment -> {
					headerTitle = TokenDetailText.tokenDetail
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