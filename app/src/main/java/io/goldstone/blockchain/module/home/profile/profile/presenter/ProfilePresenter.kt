package io.goldstone.blockchain.module.home.profile.profile.presenter

import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AlertDialog
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.*
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.ApkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileAdapter
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.profile.profile.view.ProgressLoadingDialog
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.File
import java.util.concurrent.Executors


/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */
class ProfilePresenter(
	override val fragment: ProfileFragment
) : BaseRecyclerPresenter<ProfileFragment, ProfileModel>() {

	private val installPermissionRequestCode = 0x3
	private var version = ""
	private val progressLoadingDialog: ProgressLoadingDialog by lazy {
		ProgressLoadingDialog(fragment.context!!)
	}

	private val downloadHandler: Handler by lazy {
		Handler()
	}
	private val progressRunnable: Runnable by lazy {
		Runnable {
			getBytesAndStatus()
		}
	}

	private var downloadId = 0L
	private var filepath = ""
	private val notifyManager =
		GoldStoneAPI.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	private val builder by lazy {
		NotificationCompat.Builder(GoldStoneAPI.context, "channel_1")
			.setLargeIcon(BitmapFactory.decodeResource(GoldStoneAPI.context.resources, R.mipmap.ic_launcher))
			.setSmallIcon(R.drawable.version_icon)
			.setContentTitle("GoldStone")
	}

	fun onActivityResult(
		requestCode: Int,
		resultCode: Int
	) {
		if (
			requestCode == installPermissionRequestCode
			&& resultCode == Activity.RESULT_OK
		) {
			ApkUtil.installApk(File(filepath))
			resetDownloadState()
		}
	}

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
					object : FixTextLength() {
						override var text = Config.getCurrentName()
						override val maxWidth = 40.uiPX().toFloat()
						override val textSize: Float = fragment.view?.fontSize(14).orZero()
					}.getFixString()
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
		if (downloadId != 0L) {
			progressLoadingDialog.show()
			return
		}
		fragment.context?.let {
			GoldStoneDialog.show(it) {
				showButtons(CommonText.upgrade) {
					downloadNewVersion {
						GoldStoneDialog.remove(it)
						showDownloadProgress()
					}
				}
				setContent(newVersionName, newVersionDescription)
				setImage(R.drawable.version_banner)
			}
		}
	}

	private fun resetDownloadState() {
		downloadId = 0L
		filepath = ""
	}

	private fun onProgressUpdate(status: Int, progress: Int) {
		when (status) {
			DownloadManager.STATUS_SUCCESSFUL -> {
				progressLoadingDialog.isShowing.isTrue {
					progressLoadingDialog.setProgress(0, 100)
					progressLoadingDialog.dismiss()
				}
				filepath.isEmpty() isFalse {
					if (checkInstallPermission()) {
						ApkUtil.installApk(File(filepath))
						resetDownloadState()
					} else {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							showInstallPermissionAlertDialog()
						}
					}
				}
				cancelNotification()

			}

			DownloadManager.STATUS_FAILED -> {
				resetDownloadState()
				cancelNotification()
				progressLoadingDialog.isShowing.isTrue {
					progressLoadingDialog.setProgress(0, 100)
					progressLoadingDialog.dismiss()
				}
			}

			else -> {
				updateNotificationProgress(progress)
				progressLoadingDialog.isShowing.isTrue {
					progressLoadingDialog.setProgress(progress, 100)
				}
				downloadHandler.postDelayed(progressRunnable, 500)
			}
		}
	}

	private fun checkInstallPermission(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			fragment.context?.packageManager!!.canRequestPackageInstalls()
		} else {
			true
		}
	}

	private fun showInstallPermissionAlertDialog() {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

		fragment.context?.let {
			AlertDialog.Builder(it)
				.setTitle("需要打开您的安装未知来源的权限")
				.setNegativeButton(CommonText.cancel) { dialogInterface, _ ->
					dialogInterface.dismiss()
				}
				.setPositiveButton(CommonText.confirm) { dialogInterface, _ ->
					dialogInterface.dismiss()
					startInstallPermissionSettingActivity()
				}
				.show()

		}
	}

	private fun startInstallPermissionSettingActivity() {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

		val packageURI = Uri.parse("package:" + GoldStoneAPI.context.packageName)
		val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
		fragment.startActivityForResult(intent, installPermissionRequestCode)
	}


	private fun showDownloadProgress() {
		fragment.context?.apply {
			if (downloadId != 0L) {
				progressLoadingDialog.show()
				getBytesAndStatus()
			}
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		downloadHandler.removeCallbacks(progressRunnable)
	}

	private fun getBytesAndStatus() {
		Executors.newSingleThreadExecutor().execute {
			val bytesAndStatus = arrayOf(-1, -1, 0)
			val query = DownloadManager.Query().setFilterById(downloadId)
			var cursor: Cursor? = null
			try {
				cursor = (fragment.context?.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager)?.query(query)
				cursor?.apply {
					if (!cursor.isNull() && moveToFirst()) {
						// 已经下载文件大小
						bytesAndStatus[0] = getInt(getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
						// 下载文件的总大小
						bytesAndStatus[1] = getInt(getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
						// 下载状态
						bytesAndStatus[2] = getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))
					}
				}
			} finally {
				cursor?.close()
			}

			val progress = (bytesAndStatus[0].toFloat()) / (bytesAndStatus[1].toFloat()) * 100
			fragment.context?.apply {
				runOnUiThread {
					onProgressUpdate(bytesAndStatus[2], progress.toInt())
				}
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
					fragment.recyclerView.adapter?.notifyItemChanged(lastIndex)
				}
			}
		}
	}

	private fun downloadNewVersion(callback: () -> Unit) {
		object : CheckPermission(fragment.activity) {
			override var permissionType = PermissionCategory.Write
		}.start {
			doAsync {
				downloadId = download(newVersionUrl, newVersionName, newVersionDescription)
				fragment.context?.runOnUiThread { callback() }
			}
		}
	}

	private fun download(url: String, title: String, description: String): Long {
		val request = DownloadManager.Request(Uri.parse(url)).apply {
			setDescription(description)
			setTitle(title)
			setVisibleInDownloadsUi(true)
			allowScanningByMediaScanner()
			setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
			setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "GoldStone$title.apk")
			filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + "GoldStone$title.apk"
		}
		// get download service and enqueue file
		val manager = GoldStoneAPI.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
		showNotification(description)
		return manager.enqueue(request)

	}

	private fun showNotification(description: String?) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notifyManager.createNotificationChannel(NotificationChannel("channel_1", "download", NotificationManager.IMPORTANCE_LOW))
		}
		builder.setProgress(100, 0, false)
		builder.setContentText(description)
		notifyManager.notify(1, builder.build())
	}

	private fun updateNotificationProgress(progress: Int) {
		builder.setProgress(100, progress, false)
		notifyManager.notify(1, builder.build())
	}

	private fun cancelNotification() {
		notifyManager.cancel(1)
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

	// 这个方法是为了内部使用查看 `GoldStoneID` 的隐藏方法
	private var clickTimes = 10
	private var hasShownGoldStoneID = false
	fun showGoldStoneID() {
		clickTimes -= 1
		if (clickTimes <= 0 && !hasShownGoldStoneID) {
			hasShownGoldStoneID = true
			AppConfigTable.getAppConfig {
				it?.apply {
					fragment.context.alert(goldStoneID)
					fragment.context?.clickToCopy(goldStoneID)
				}
			}
		}
	}

	private fun getCurrentLanguageSymbol() =
		HoneyLanguage.getLanguageByCode(Config.getCurrentLanguageCode())
}