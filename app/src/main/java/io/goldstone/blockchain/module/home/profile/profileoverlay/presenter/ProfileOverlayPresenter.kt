package io.goldstone.blockchain.module.home.profile.profileoverlay.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ProfileText
import io.goldstone.blockchain.module.home.profile.aboutus.view.AboutUsFragment
import io.goldstone.blockchain.module.home.profile.contacts.view.ContactsFragment
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment
import io.goldstone.blockchain.module.home.profile.lanaguage.view.LanguageFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

/**
 * @date 26/03/2018 12:56 AM
 * @author KaySaith
 */

class ProfileOverlayPresenter(
  override val fragment: ProfileOverlayFragment
) : BaseOverlayPresenter<ProfileOverlayFragment>() {

  private fun showContactsFragment() {
    fragment.addFragmentAndSetArgument<ContactsFragment>(ContainerID.content) {
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
      // Send Arguments
    }
  }

  fun showTargetFragmentByTitle(title: String) {
    when(title) {
      ProfileText.contacts -> showContactsFragment()
      ProfileText.currency -> showCurrencyFragment()
      ProfileText.language -> showLanguageFragment()
      ProfileText.aboutUs -> showAboutUsFragment()
    }
  }

}