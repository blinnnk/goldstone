package io.goldstone.blockchain.module.home.profile.profile.presenter

import android.content.Intent
import android.text.format.DateFormat
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import com.blinnnk.util.SystemUtils
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.eos.base.TitleCellAdapter
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.kernel.receiver.currentVersion
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileAdapter
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.math.roundToInt


/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */

@Suppress("DEPRECATION")
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
				ProfileModel(R.drawable.fingerprint_icon, ProfileText.fingerprintSettings, ""),
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
					addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main, FragmentTag.profileOverlay) {
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
	private var clickTimes = generateUnlockTimes()

	fun showGoldStoneID() {
		clickTimes -= 1
		if (clickTimes <= 0 || SharedValue.getDeveloperModeStatus()) {
			fragment.context?.apply {
				val data = arrayListOf(
					Pair("GoldStone ID", SharedWallet.getGoldStoneID()),
					Pair(ProfileText.apkChannel, currentChannel.value),
					Pair(ProfileText.versionCode, "${SystemUtils.getVersionCode(this)}")
				)
				Dashboard(this) {
					showList(ProfileText.developerOptions, TitleCellAdapter(data))
				}
				SharedValue.updateDeveloperModeStatus(true)
			}
		}
	}

	private fun generateUnlockTimes(): Int {
		return try {
			val day = DateFormat.format("dd", Date()).toString().toBigDecimal().toDouble()
			val hour = Date().hours
			((day * hour / 100) + 10).roundToInt()
		} catch (error: Exception) {
			10
		}
	}

	private fun getCurrentLanguageSymbol() =
		HoneyLanguage.getLanguageByCode(SharedWallet.getCurrentLanguageCode())
}
