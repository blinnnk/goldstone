@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.kernel.receiver

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.safeGet
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.tencent.android.tpush.*
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TinyNumber
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneCode
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

/**
 * @date 19/04/2018 4:33 PM
 * @author KaySaith
 */
class XinGePushReceiver : XGPushBaseReceiver() {
	
	@SuppressLint("InvalidWakeLockTag", "WrongConstant")
	private fun showNotificationOnLockScreen(
		context: Context,
		content: String
	) {
		// 播放提醒音
		val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val ringTone = RingtoneManager.getRingtone(context, notification)
		ringTone.play()
		// 激活屏幕让屏幕亮起来, 在锁屏的时候
		val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
		if (!powerManager.isInteractive) {
			// if screen is not already on, turn it on (get wake_lock for 10 seconds)
			val wake = powerManager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
				"weakScreen"
			)
			wake.acquire(10000)
			val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "weakScreen")
			wakeLock.acquire(10000)
		}
		val builder = NotificationCompat.Builder(context)
		builder.setContentTitle("GoldStone").setContentText(content).setSmallIcon(R.mipmap.ic_launcher)
			.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
	}
	
	override fun onSetTagResult(
		p0: Context?,
		p1: Int,
		p2: String?
	) {
	}
	
	override fun onNotifactionShowedResult(
		context: Context?,
		notifiShowedRlt: XGPushShowedResult?
	) {
		if (context == null || notifiShowedRlt == null) return
		// Normal Notification
	}
	
	override fun onUnregisterResult(
		context: Context?,
		p1: Int
	) {
		if (context == null) {
			return
		}
	}
	
	override fun onDeleteTagResult(
		p0: Context?,
		p1: Int,
		p2: String?
	) {
	}
	
	override fun onRegisterResult(
		p0: Context?,
		p1: Int,
		p2: XGPushRegisterResult?
	) {
	}
	
	@SuppressLint("PrivateResource")
	override fun onTextMessage(
		context: Context?,
		message: XGPushTextMessage?
	) {
		if (context == null) return
		showNotificationOnLockScreen(context, message.toString())
	}
	
	override fun onNotifactionClickedResult(
		context: Context?,
		result: XGPushClickedResult?
	) {
		result?.customContent?.let {
			when (JSONObject(it).safeGet("uri")) {
				ClassURI.transactionDetail -> {
					handlTransactionNotification(context, JSONObject(it).safeGet("hash"))
				}
			}
		}
		clearAppIconRedot()
	}
	
	private fun handlTransactionNotification(
		context: Context?,
		content: String?
	) {
		val intent = Intent(context, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		intent.putExtra(IntentKey.hashFromNotify, content)
		context?.startActivity(intent)
	}
	
	companion object {
		fun clearAppIconRedot() {
			// 清楚所有 `App Icon` 上的小红点
			val notificationManager =
				GoldStoneAPI.context.applicationContext?.getSystemService(
					Context.NOTIFICATION_SERVICE
				) as NotificationManager
			notificationManager.cancelAll()
		}
		
		fun registerWalletAddressForPush() {
			WalletTable.getAllAddresses {
				AppConfigTable.getAppConfig { config ->
					// 把地址转换成 `JsonArray` 格式
					toJsonArray {
						GoldStoneAPI.registerWalletAddress(
							it,
							config?.goldStoneID.orEmpty(),
							{
								// 网络有问题的时候或其他错误的时候标记注册失败
								AppConfigTable.updateRegisterAddressesStatus(false)
							}
						) {
							GoldStoneCode.isSuccess(it.toJsonObject()["code"]) { isSucceed ->
								isSucceed isTrue {
									AppConfigTable.updateRegisterAddressesStatus(true)
									LogUtil.debug(this.javaClass.simpleName, "code: $it")
								} otherwise {
									// 服务器返回错误的时候标记注册失败
									AppConfigTable.updateRegisterAddressesStatus(false)
								}
							}
						}
					}
				}
			}
		}
	}
}

@SuppressLint("HardwareIds")
fun Application.registerDeviceForPush() {
	// 为测试方便设置，发布上线时设置为 `false`
	XGPushConfig.enableDebug(this, false)
	XGPushManager.registerPush(this, object : XGIOperateCallback {
		override fun onSuccess(token: Any?, p1: Int) {
			// 准备信息注册设备的信息到服务器, 为了 `Push` 做的工作
			registerDevice(token.toString()) {
				// 如果本地有注册成功的标记则不再注册
				getStringFromSharedPreferences(SharesPreference.registerPush).let {
					LogUtil.debug(this.javaClass.simpleName, "token: $it")
					if (it == token) return@registerDevice
				}
				// 在本地数据库记录 `Push Token`
				AppConfigTable.updatePushToken(token.toString())
				XinGePushReceiver.registerWalletAddressForPush()
			}
		}
		
		override fun onFail(
			data: Any?, errCode: Int, message: String?
		) {
			LogUtil.debug("registerDeviceForPush", message.orEmpty())
		}
	})
}

@SuppressLint("HardwareIds")
fun Context.registerDevice(
	token: String,
	callback: () -> Unit
) {
	// 没有注册过就开始注册
	val isChina = if (CountryCode.currentCountry == CountryCode.china.country) TinyNumber.True.value
	else TinyNumber.False.value
	doAsync {
		AppConfigTable.getAppConfig { config ->
			config?.apply {
				GoldStoneAPI.registerDevice(
					HoneyLanguage.getLanguageSymbol(language).toLowerCase(),
					token,
					goldStoneID,
					isChina,
					0,
					config.chainID.toInt(),
					CountryCode.currentCountry,
					{
						// Error Callback
						LogUtil.error("registerDevice")
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				) {
					// 返回的 `Code` 是 `0` 存入 `SharedPreference` `token` 下次检查是否需要重新注册
					GoldStoneCode.isSuccess(it.toJsonObject()["code"]) {
						saveDataToSharedPreferences(SharesPreference.registerPush, token)
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}
	}
}