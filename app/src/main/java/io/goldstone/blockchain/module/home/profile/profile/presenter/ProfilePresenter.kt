package io.goldstone.blockchain.module.home.profile.profile.presenter

import android.widget.LinearLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.orZero
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.common.value.ProfileText
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileCell
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */

class ProfilePresenter(
  override val fragment: ProfileFragment
  ) : BaseRecyclerPresenter<ProfileFragment, ProfileModel>() {

  override fun updateData() {
    ContactTable.getAllContacts { contactCount ->
      fragment.asyncData = arrayListOf(
        ProfileModel(R.drawable.contacts_icon, ProfileText.contacts, contactCount.size.toString()),
        ProfileModel(R.drawable.currency_icon, ProfileText.currency, GoldStoneApp.currencyCode),
        ProfileModel(R.drawable.language_icon, ProfileText.language, getCurrentLanguageSymbol()),
        ProfileModel(R.drawable.pin_code_icon, ProfileText.pinCode, ""),
        ProfileModel(R.drawable.language_icon, ProfileText.language, "EN"),
        ProfileModel(R.drawable.contacts_icon, ProfileText.aboutUs, "8"),
        ProfileModel(R.drawable.currency_icon, ProfileText.currency, "USD"),
        ProfileModel(R.drawable.language_icon, ProfileText.language, "EN")
      )
    }
  }

  fun showContactsFragment(title: String) {
    fragment.activity?.addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main) {
      putString(ArgumentKey.profileTitle, title)
    }
  }

  fun setCustomIntervalSize(item: ProfileCell, position: Int) {
    item.apply {
      when(position) {
        3 -> setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
        0 -> {
          setMargins<LinearLayout.LayoutParams> { topMargin = 100.uiPX() }
          fragment.recyclerView.scrollToPosition(0)
        }
      }
    }
  }

  private fun getCurrentLanguageSymbol() =
    HoneyLanguage.getLanguageSymbol(GoldStoneApp.currentLanguage.orZero())

}