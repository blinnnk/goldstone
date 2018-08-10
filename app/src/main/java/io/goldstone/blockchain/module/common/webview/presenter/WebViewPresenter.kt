package io.goldstone.blockchain.module.common.webview.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

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