package io.goldstone.blockchain.module.home.profile.profile.presenter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.util.CheckPermission
import com.blinnnk.util.PermissionCategory
import com.blinnnk.util.SystemUtils
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileAdapter
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */
class ProfilePresenter(
	override val fragment: ProfileFragment
) : BaseRecyclerPresenter<ProfileFragment, ProfileModel>() {
	
	private var version = ""
	
	override fun updateData() {
		ContactTable.getAllContacts { contactCount ->
			val data = arrayListOf(
				ProfileModel(
					R.drawable.contacts_icon,
					ProfileText.contacts,
					contactCount.size.toString()
				),
				ProfileModel(
					R.drawable.currency_icon,
					ProfileText.currency,
					Config.getCurrencyCode()
				),
				ProfileModel(
					R.drawable.language_icon,
					ProfileText.language,
					getCurrentLanguageSymbol()
				),
				ProfileModel(
					R.drawable.chain_icon,
					ProfileText.chain,
					if (Config.isTestEnvironment()) ChainText.testnet else ChainText.mainnet
				),
				ProfileModel(
					R.drawable.wallet_icon,
					ProfileText.walletManager,
					Config.getCurrentName()
				),
				ProfileModel(R.drawable.pin_code_icon, ProfileText.pinCode, ""),
				ProfileModel(R.drawable.about_us_icon, ProfileText.aboutUs, ""),
				ProfileModel(R.drawable.terms_icon, ProfileText.terms, ""),
				ProfileModel(R.drawable.contact_us_icon, ProfileText.support, ""),
				ProfileModel(R.drawable.help_center_icon, ProfileText.helpCenter, ""),
				ProfileModel(R.drawable.privacy_icon, ProfileText.privacy, ""),
				ProfileModel(R.drawable.share_icon, ProfileText.shareApp, ""),
				ProfileModel(
					R.drawable.version_icon,
					ProfileText.version,
					version
				)
			)
			if (fragment.asyncData.isNull()) {
				fragment.asyncData = data
				checkVersion()
			} else {
				diffAndUpdateAdapterData<ProfileAdapter>(data)
			}
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
	
	private var newVersionDescription = ""
	private var newVersionName = ""
	private var newVersionUrl = ""
	
	fun showUpgradeDialog() {
		fragment.context?.let {
			GoldStoneDialog.show(it) {
				showButtons(CommonText.upgrade) {
					downloadNewVersion {
						GoldStoneDialog.remove(it)
						fragment.context?.alert("Application is downloading now")
					}
				}
				setContent(newVersionName, newVersionDescription)
				setImage(R.drawable.version_banner)
			}
		}
	}
	
	private fun checkVersion() {
		fragment.context?.let { context ->
			GoldStoneAPI.getNewVersionOrElse { versionModel ->
				version = if (versionModel.isNull()) {
					SystemUtils.getVersionName(context)
				} else {
					newVersionDescription = versionModel?.description.orEmpty()
					newVersionName = versionModel?.versionName.orEmpty()
					newVersionUrl = versionModel?.url.orEmpty()
					versionModel?.versionName + " " + CommonText.new
				}
				fragment.asyncData?.apply {
					last().info = version
					fragment.recyclerView.adapter.notifyItemChanged(lastIndex)
				}
			}
		}
	}
	
	private fun downloadNewVersion(callback: () -> Unit) {
		object : CheckPermission(fragment.activity) {
			override var permissionType = PermissionCategory.Write
		}.start {
			doAsync {
				download(newVersionUrl, newVersionName, newVersionDescription)
				fragment.context?.runOnUiThread { callback() }
			}
		}
	}
	
	private fun download(url: String, title: String, description: String) {
		val request = DownloadManager.Request(Uri.parse(url)).apply {
			setDescription(description)
			setTitle(title)
			allowScanningByMediaScanner()
			setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
			setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.apk")
		}
		// get download service and enqueue file
		val manager = GoldStoneAPI.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
		manager.enqueue(request)
	}
	
	private fun showShareChooser() {
		val intent = Intent(Intent.ACTION_SEND)
		fun getShareContentThenShowView(content: String) {
			intent.putExtra(
				Intent.EXTRA_TEXT,
				content
			)
			intent.type = "text/plain"
			fragment.context?.startActivity(Intent.createChooser(intent, "share"))
		}
		
		AppConfigTable.getAppConfig {
			it?.apply {
				getShareContentThenShowView(shareContent)
			}
		}
	}
	
	// 这个方法是为了内部使用的隐藏方法
	private var clickTimes = 10
	private var hasShownGoldStoneID = false
	fun showGoldStoneID() {
		clickTimes -= 1
		if (clickTimes <= 0 && !hasShownGoldStoneID) {
			hasShownGoldStoneID = true
			AppConfigTable.getAppConfig {
				it?.apply {
					fragment.context.alert(it.goldStoneID)
				}
			}
		}
	}
	
	private fun getCurrentLanguageSymbol() =
		HoneyLanguage.getLanguageByCode(Config.getCurrentLanguageCode())
}