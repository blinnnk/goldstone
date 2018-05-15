package io.goldstone.blockchain.module.home.profile.profileoverlay.presenter

import com.blinnnk.extension.findChildFragmentByTag
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.profile.aboutus.view.AboutUsFragment
import io.goldstone.blockchain.module.home.profile.chainselection.view.ChainSelectionFragment
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.view.ContractInputFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment
import io.goldstone.blockchain.module.home.profile.lanaguage.view.LanguageFragment
import io.goldstone.blockchain.module.home.profile.pincode.view.PinCodeEditorFragment
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

/**
 * @date 26/03/2018 12:56 AM
 * @author KaySaith
 */

class ProfileOverlayPresenter(
	override val fragment: ProfileOverlayFragment
) : BaseOverlayPresenter<ProfileOverlayFragment>() {

	override fun removeSelfFromActivity() {
		super.removeSelfFromActivity()
		fragment.getMainActivity()?.apply {
			supportFragmentManager.findFragmentByTag(FragmentTag.home)?.apply {
				findChildFragmentByTag<ProfileFragment>(FragmentTag.profile)?.presenter?.updateData()
			}
		}
	}

	fun showContactInputFragment() {
		showTargetFragment<ContractInputFragment>(ProfileText.contactsInput, ProfileText.contacts)
	}

	fun showTargetFragmentByTitle(title: String) {
		when (title) {
			ProfileText.contacts -> showContactsFragment()
			ProfileText.currency -> showCurrencyFragment()
			ProfileText.language -> showLanguageFragment()
			ProfileText.aboutUs -> showAboutUsFragment()
			ProfileText.pinCode -> showPinCodeEditorFragment()
			ProfileText.chain -> showChainSelectionFragment()
			ProfileText.privacy -> showPrivacyFragment()
			ProfileText.terms -> showTermsFragment()
		}
	}

	private fun showPrivacyFragment() {
		fragment.addFragmentAndSetArgument<WebViewFragment>(ContainerID.content) {
			putString(ArgumentKey.webViewUrl, WebUrl.privacy)
		}
	}

	private fun showTermsFragment() {
		fragment.addFragmentAndSetArgument<WebViewFragment>(ContainerID.content) {
			putString(ArgumentKey.webViewUrl, WebUrl.terms)
		}
	}

	private fun showChainSelectionFragment() {
		fragment.addFragmentAndSetArgument<ChainSelectionFragment>(ContainerID.content) {
			// Send Arguments
		}
	}

	private fun showPinCodeEditorFragment() {
		fragment.addFragmentAndSetArgument<PinCodeEditorFragment>(ContainerID.content) {
			// Send Arguments
		}
	}

	private fun showContactsFragment() {
		fragment.addFragmentAndSetArgument<ContactFragment>(ContainerID.content) {
			// Send Arguments
		}
	}

	private fun showCurrencyFragment() {
		fragment.addFragmentAndSetArgument<CurrencyFragment>(ContainerID.content) {
			// Send Arguments
		}
	}

	private fun showLanguageFragment() {
		fragment.addFragmentAndSetArgument<LanguageFragment>(ContainerID.content) {
			// Send Arguments
		}
	}

	private fun showAboutUsFragment() {
		fragment.addFragmentAndSetArgument<AboutUsFragment>(ContainerID.content) {
			//
		}
	}

}