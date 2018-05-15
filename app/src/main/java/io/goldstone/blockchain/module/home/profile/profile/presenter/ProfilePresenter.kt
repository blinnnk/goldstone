package io.goldstone.blockchain.module.home.profile.profile.presenter

import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileAdapter
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
			val data = arrayListOf(
				ProfileModel(R.drawable.contacts_icon, ProfileText.contacts, contactCount.size.toString()),
				ProfileModel(R.drawable.currency_icon, ProfileText.currency, GoldStoneApp.currencyCode),
				ProfileModel(R.drawable.language_icon, ProfileText.language, getCurrentLanguageSymbol()),
				ProfileModel(R.drawable.chain_icon, ProfileText.chain, "Ropstan"),
				ProfileModel(R.drawable.pin_code_icon, ProfileText.pinCode, ""),
				ProfileModel(R.drawable.about_us_icon, ProfileText.aboutUs, ""),
				ProfileModel(R.drawable.terms_icon, ProfileText.terms, ""),
				ProfileModel(R.drawable.support_icon, ProfileText.support, ""),
				ProfileModel(R.drawable.privacy_icon, ProfileText.privacy, ""),
				ProfileModel(R.drawable.version_icon, ProfileText.version, "BETA 1.0.0")
			)
			if (fragment.asyncData.isNull()) fragment.asyncData = data
			else {
				diffAndUpdateAdapterData<ProfileAdapter>(data)
			}
		}
	}

	fun showTargetFragment(title: String) {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.profileOverlay) isFalse {
				addFragmentAndSetArguments<ProfileOverlayFragment>(
					ContainerID.main, FragmentTag.profileOverlay
				) {
					putString(ArgumentKey.profileTitle, title)
				}
			}
		}

	}

	private fun getCurrentLanguageSymbol() =
		HoneyLanguage.getLanguageSymbol(GoldStoneApp.currentLanguage.orZero())

}