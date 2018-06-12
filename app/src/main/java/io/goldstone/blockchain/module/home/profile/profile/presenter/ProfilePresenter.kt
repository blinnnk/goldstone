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
					ChainID.getChainNameByID(Config.getCurrentChain())
				),
				ProfileModel(R.drawable.pin_code_icon, ProfileText.pinCode, ""),
				ProfileModel(R.drawable.about_us_icon, ProfileText.aboutUs, ""),
				ProfileModel(R.drawable.terms_icon, ProfileText.terms, ""),
				ProfileModel(R.drawable.support_icon, ProfileText.support, ""),
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
		GoldStoneDialog.show(fragment.context!!) {
			showButtons(CommonText.upgrade) {
				downloadNewVersion {
					GoldStoneDialog.remove(fragment.context!!)
					fragment.context?.alert("Application is downloading now")
				}
			}
			setContent(newVersionName, newVersionDescription)
			setImage(R.drawable.version_banner)
		}
	}
	
	private fun checkVersion() {
		GoldStoneAPI.getNewVersionOrElse {
			version = if (it.isNull()) {
				SystemUtils.getVersionName(fragment.context!!)
			} else {
				newVersionDescription = it?.description.orEmpty()
				newVersionName = it?.versionName.orEmpty()
				newVersionUrl = it?.url.orEmpty()
				it?.versionName + " " + CommonText.new
			}
			fragment.asyncData?.last()?.info = version
			fragment.recyclerView.adapter.notifyItemChanged(fragment.asyncData!!.lastIndex)
		}
	}
	
	private fun downloadNewVersion(callback: () -> Unit) {
		object : CheckPermission(fragment.activity) {
			override var permissionType = PermissionCategory.Write
		}.start {
			doAsync {
				download(newVersionUrl, newVersionName, newVersionDescription)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
	}
	
	private fun download(url: String, title: String, description: String) {
		val request = DownloadManager.Request(Uri.parse(url))
		request.setDescription(description)
		request.setTitle(title)
		request.allowScanningByMediaScanner()
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
		request
			.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.apk")
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
	
	private fun getCurrentLanguageSymbol() =
		HoneyLanguage.getLanguageByCode(Config.getCurrentLanguageCode())
}