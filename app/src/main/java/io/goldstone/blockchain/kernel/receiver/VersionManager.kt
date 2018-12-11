package io.goldstone.blockchain.kernel.receiver

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
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AlertDialog
import com.blinnnk.extension.*
import com.blinnnk.util.CheckPermission
import com.blinnnk.util.PermissionCategory
import com.blinnnk.util.SystemUtils
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ApkUtil
import io.goldstone.blockchain.common.value.ApkChannel
import io.goldstone.blockchain.common.value.currentChannel
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.profile.profile.view.ProgressLoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors


/**
 * @author KaySaith
 * @date  2018/11/09
 */
var currentVersion = ""

class VersionManager(val fragment: Fragment) {

	private var newVersionDescription = ""
	private var newVersionName = ""
	private var newVersionUrl = ""
	private val installPermissionRequestCode = 0x3
	private val progressLoadingDialog: ProgressLoadingDialog by lazy {
		ProgressLoadingDialog(fragment.context!!)
	}
	private var downloadId = 0L
	private var filepath = ""
	private val notifyManager =
		GoldStoneApp.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	private val builder by lazy {
		NotificationCompat.Builder(GoldStoneApp.appContext, "channel_1")
			.setLargeIcon(BitmapFactory.decodeResource(GoldStoneApp.appContext.resources, R.mipmap.ic_launcher))
			.setSmallIcon(R.drawable.version_icon)
			.setContentTitle("GoldStone")
	}

	private val downloadHandler: Handler by lazy {
		Handler()
	}
	private val progressRunnable: Runnable by lazy {
		Runnable {
			getBytesAndStatus()
		}
	}

	// 需要在集成的 Fragment 销毁的时候执行
	fun removeHandler() {
		downloadHandler.removeCallbacks(progressRunnable)
	}

	fun onActivityResult(requestCode: Int, resultCode: Int) {
		if (requestCode == installPermissionRequestCode && resultCode == Activity.RESULT_OK) {
			ApkUtil.installApk(File(filepath))
			resetDownloadState()
		}
	}

	fun checkVersion(@UiThread hold: (error: RequestError) -> Unit) {
		GoldStoneAPI.getNewVersionOrElse { versionModel, error ->
			if (versionModel.isNotNull() && error.isNone()) {
				newVersionDescription = versionModel.description.orEmpty()
				newVersionName = versionModel.versionName.orEmpty()
				newVersionUrl = versionModel.url.orEmpty()
				currentVersion = newVersionName
				GlobalScope.launch(Dispatchers.Main) { hold(error) }
			} else GlobalScope.launch(Dispatchers.Main) {
				fragment.context?.let {
					currentVersion = SystemUtils.getVersionName(it)
					hold(error)
				}
			}
		}
	}

	private fun resetDownloadState() {
		downloadId = 0L
		filepath = ""
	}

	private fun Context.showGooglePlayStore() {
		try {
			startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
		} catch (error: android.content.ActivityNotFoundException) {
			startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
		}
	}

	fun showUpgradeDialog() {
		fragment.context?.apply {
			GoldStoneDialog(this).showUpgradeStatus(newVersionName, newVersionDescription) {
				downloadNewVersion {
					if (currentChannel == ApkChannel.Google) {
						showGooglePlayStore()
					} else showDownloadProgress()
				}
			}
		}
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
			fragment.context?.packageManager?.canRequestPackageInstalls() ?: false
		} else {
			true
		}
	}

	private fun showInstallPermissionAlertDialog() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
		fragment.context?.apply {
			AlertDialog.Builder(this)
				.setTitle("需要打开您的安装未知来源的权限")
				.setNegativeButton(CommonText.cancel) { dialogInterface, _ ->
					dialogInterface.dismiss()
				}.setPositiveButton(CommonText.confirm) { dialogInterface, _ ->
					dialogInterface.dismiss()
					startInstallPermissionSettingActivity()
				}.show()
		}
	}

	private fun startInstallPermissionSettingActivity() {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

		val packageURI = Uri.parse("package:" + GoldStoneApp.appContext.packageName)
		val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
		fragment.startActivityForResult(intent, installPermissionRequestCode)
	}


	private fun showDownloadProgress() {
		if (downloadId != 0L) {
			progressLoadingDialog.show()
			getBytesAndStatus()
		}
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
			GlobalScope.launch(Dispatchers.Main) {
				onProgressUpdate(bytesAndStatus[2], progress.toInt())
			}
		}
	}

	private fun downloadNewVersion(callback: () -> Unit) {
		object : CheckPermission(fragment.activity) {
			override var permissionType = PermissionCategory.Write
		}.start {
			GlobalScope.launch(Dispatchers.Default) {
				downloadId = download(newVersionUrl, newVersionName, newVersionDescription)
				launchUI(callback)
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
		val manager = GoldStoneApp.appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
}