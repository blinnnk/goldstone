package io.goldstone.blockchain.module.home.profile.profile.presenter

import android.content.Intent
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.*
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.receiver.currentVersion
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileAdapter
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import kotlinx.coroutines.Dispatchers


/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */

class ProfilePresenter(
	override val fragment: ProfileFragment
) : BaseRecyclerPresenter<ProfileFragment, ProfileModel>() {

	override fun updateData() {
		load {
			ContactTable.dao.getAllContacts().size
		} then {
			val data = arrayListOf(
				ProfileModel(
					R.drawable.wallet_icon,
					ProfileText.walletManager,
					object : FixTextLength() {
						override var text = SharedWallet.getCurrentName()
						override val maxWidth = 40.uiPX().toFloat()
						override val textSize: Float = fragment.view?.fontSize(14).orZero()
					}.getFixString()
				),
				ProfileModel(R.drawable.chain_icon, ProfileText.chain, if (SharedValue.isTestEnvironment()) ChainText.testnet else ChainText.mainnet),
				ProfileModel(R.drawable.pin_code_icon, ProfileText.pinCode, ""),
				ProfileModel(R.drawable.eos_account_register, ProfileText.eosAccountRegister, ""),
				ProfileModel(R.drawable.currency_icon, ProfileText.currency, SharedWallet.getCurrencyCode()),
				ProfileModel(R.drawable.language_icon, ProfileText.language, getCurrentLanguageSymbol()),
				ProfileModel(R.drawable.contacts_icon, ProfileText.contacts, it.toString()),
				ProfileModel(R.drawable.about_us_icon, ProfileText.aboutUs, ""),
				ProfileModel(R.drawable.terms_icon, ProfileText.terms, ""),
				ProfileModel(R.drawable.contact_us_icon, ProfileText.support, ""),
				ProfileModel(R.drawable.help_center_icon, ProfileText.helpCenter, ""),
				ProfileModel(R.drawable.privacy_icon, ProfileText.privacy, ""),
				ProfileModel(R.drawable.share_icon, ProfileText.shareApp, ""),
				if (currentVersion.isEmpty() || currentVersion == SystemUtils.getVersionName(fragment.context!!))
					ProfileModel(R.drawable.version_icon, ProfileText.version, currentVersion)
				else ProfileModel(R.drawable.version_icon, ProfileText.version, currentVersion suffix CommonText.new)
			)
			if (fragment.asyncData.isNull()) fragment.asyncData = data
			else diffAndUpdateAdapterData<ProfileAdapter>(data)
		}
	}

	override fun onFragmentHiddenChanged(isHidden: Boolean) {
		super.onFragmentHiddenChanged(isHidden)
		if (!isHidden) fragment.asyncData?.apply {
			fragment.recyclerView.adapter?.notifyItemChanged(lastIndex)
		}
	}

	fun showTargetFragment(title: String) {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.profileOverlay) isFalse {
				if (title == ProfileText.shareApp) {
					showShareChooser()
				} else {
					addFragmentAndSetArguments<ProfileOverlayFragment>(
						ContainerID.main,
						FragmentTag.profileOverlay
					) {
						putString(ArgumentKey.profileTitle, title)
					}
				}
			}
		}
	}

	private fun showShareChooser() {
		AppConfigTable.getAppConfig(Dispatchers.Main) {
			it?.apply {
				val intent = Intent(Intent.ACTION_SEND)
				intent.putExtra(Intent.EXTRA_TEXT, shareContent)
				intent.type = "text/plain"
				fragment.context?.startActivity(Intent.createChooser(intent, "share"))
			}
		}
	}

	// 这个方法是为了内部使用查看 `GoldStoneID` 的隐藏方法
	private var clickTimes = 10
	private var hasShownGoldStoneID = false
	fun showGoldStoneID() {
		clickTimes -= 1
		if (clickTimes <= 0 && !hasShownGoldStoneID) {
			hasShownGoldStoneID = true
			SharedValue.updateDeveloperModeStatus(true)
			AppConfigTable.getAppConfig(Dispatchers.Main) {
				it?.apply {
					fragment.context.alert(goldStoneID)
					fragment.context?.clickToCopy(goldStoneID)
				}
			}
		}
	}

	private fun getCurrentLanguageSymbol() =
		HoneyLanguage.getLanguageByCode(SharedWallet.getCurrentLanguageCode())
}
