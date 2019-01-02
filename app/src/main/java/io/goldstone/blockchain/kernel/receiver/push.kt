@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.kernel.receiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.PowerManager
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.blinnnk.extension.safeGet
import com.blinnnk.util.TinyNumber
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.tencent.android.tpush.*
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.value.ClassURI
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.IntentKey
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.crypto.keystore.toJsonObject
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.GoldStoneCode
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.json.JSONObject

@Suppress("IMPLICIT_CAST_TO_ANY")
/**
 * @date 19/04/2018 4:33 PM
 * @author KaySaith
 */
class XinGePushReceiver : XGPushBaseReceiver() {

	@SuppressLint("InvalidWakeLockTag", "WrongConstant")
	private fun showNotificationOnLockScreen(context: Context, content: String) {
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

	override fun onSetTagResult(p0: Context?, p1: Int, p2: String?) {}
	override fun onNotifactionShowedResult(context: Context?, notifiShowedRlt: XGPushShowedResult?) {
		if (context == null || notifiShowedRlt == null) return
	}

	override fun onUnregisterResult(context: Context?, p1: Int) {
		if (context == null) return
	}

	override fun onDeleteTagResult(p0: Context?, p1: Int, p2: String?) {}
	override fun onRegisterResult(p0: Context?, p1: Int, p2: XGPushRegisterResult?) {}
	@SuppressLint("PrivateResource")
	override fun onTextMessage(context: Context?, message: XGPushTextMessage?) {
		if (context == null) return
		showNotificationOnLockScreen(context, message.toString())
	}

	override fun onNotifactionClickedResult(context: Context?, result: XGPushClickedResult?) {
		result?.customContent?.let {
			when (JSONObject(it).safeGet("uri")) {
				ClassURI.transactionDetail -> {
					handleTransactionNotification(context, JSONObject(it).safeGet("hash"))
				}
			}
		}
		clearAppIconReDot()
	}

	private fun handleTransactionNotification(context: Context?, content: String?) {
		val intent = Intent(context, MainActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
		intent.putExtra(IntentKey.hashFromNotify, content)
		context?.startActivity(intent)
	}

	companion object {
		fun clearAppIconReDot() {
			// 清楚所有 `App Icon` 上的小红点
			val notificationManager =
				GoldStoneApp.appContext.applicationContext?.getSystemService(
					Context.NOTIFICATION_SERVICE
				) as NotificationManager
			notificationManager.cancelAll()
		}

		fun registerAddressesForPush(wallet: WalletTable?, isRemove: Boolean = false) {
			wallet?.apply {
				val option = if (isRemove) 0 else 1
				if (getWalletType().isBIP44()) {
					val all = getCurrentAllBip44Address()
					val convertedData = all.asSequence().map {
						prepareAddressData(AddressCommissionModel(it.address, it.getChainType().id, option, id))
					}.toList()
					GoldStoneAPI.registerWalletAddresses(
						AesCrypto.encrypt("$convertedData").orEmpty()
					) { result, error ->
						if (!result.isNullOrEmpty() && error.isNone() && !isRemove) {
							GoldStoneCode.isSuccess(result.toJsonObject()["code"]) { isSucceed ->
								AppConfigTable.dao.updateHasRegisteredAddress(isSucceed)
							}
						}
					}
				} else getCurrentBip44Addresses().forEach {
					registerSingleAddress(AddressCommissionModel(it.address, it.chainType, option, id))
				}
			}
		}

		fun registerSingleAddress(model: AddressCommissionModel) {
			listOf(model).map { prepareAddressData(it) }.let { models ->
				GoldStoneAPI.registerWalletAddresses(
					AesCrypto.encrypt("$models").orEmpty()
				) { result, error ->
					// API 错误
					// 如果是注册地址 `option = 1` 的情况下在数据库标记注册成功与否的状态
					if (!result.isNullOrEmpty() && error.isNone() && model.option == 1)
						GoldStoneCode.isSuccess(result.toJsonObject()["code"]) { isSucceed ->
							val configDao =
								GoldStoneDataBase.database.appConfigDao()
							if (isSucceed) {
								configDao.updateHasRegisteredAddress(true)
								Log.d("XinGePushReceiver", "code: $models")
							} else configDao.updateHasRegisteredAddress(false)
						}
				}
			}
		}

		// 因为 `BCH` 的地址特殊格式, 以及 `Bip44` 复用 `ChainType 1` 作为 `AllTest Path`
		// 所以, `BCH` 客户端单独注册特殊格式给服务器用于服务器监听转账 `Push` 使用
		private fun prepareAddressData(model: AddressCommissionModel): String {
			return if (model.chainType == 1) {
				val bchTestAddress =
					BCHUtil.instance.encodeCashAddressByLegacy(model.address).substringAfter(":")
				generateJSONObject(
					Pair("address", model.address),
					Pair("chain_type", model.chainType),
					Pair("wallet", model.walletID),
					Pair("option", model.option),
					Pair("extra", "bchtest:$bchTestAddress")
				)
			} else {
				generateJSONObject(
					Pair("address", model.address),
					Pair("chain_type", model.chainType),
					Pair("wallet", model.walletID),
					Pair("option", model.option)
				)
			}
		}

		private fun <T> generateJSONObject(vararg pairs: Pair<String, T>): String {
			var content = ""
			pairs.forEach {
				val value = if (it.second is String) {
					"\"${it.second}\""
				} else it.second
				content += "\"${it.first}\":$value,"
			}
			return "{${content.substringBeforeLast(",")}}"
		}
	}
}

@WorkerThread
@SuppressLint("HardwareIds")
fun Context.registerDeviceForPush() {
	// 为测试方便设置，发布上线时设置为 `false`
	XGPushConfig.enableDebug(this, false)
	XGPushManager.registerPush(this, object : XGIOperateCallback {
		override fun onSuccess(token: Any?, p1: Int) {
			// 准备信息注册设备的信息到服务器, 为了 `Push` 做的工作
			registerDevice(token.toString()) {
				// 如果本地有注册成功的标记则不再注册
				getStringFromSharedPreferences(SharesPreference.registerPush).let {
					Log.d(this.javaClass.simpleName, "token: $it")
					if (it == token) return@registerDevice
				}
				// 在本地数据库记录 `Push Token`
				val currentWallet =
					WalletTable.dao.findWhichIsUsing()
				XinGePushReceiver.registerAddressesForPush(currentWallet)
			}
		}

		override fun onFail(data: Any?, errCode: Int, message: String?) {
			Log.d("registerDeviceForPush", message.orEmpty())
		}
	})
}

@WorkerThread
@SuppressLint("HardwareIds")
fun Context.registerDevice(token: String, @UiThread callback: () -> Unit) {
	// 把 `Token` 记录在本地数据库
	AppConfigTable.dao.updatePushToken(token)
	// 没有注册过就开始注册
	val isChina = if (CountryCode.currentCountry == CountryCode.china.country) TinyNumber.True.value
	else TinyNumber.False.value
	val languageCode = SharedWallet.getCurrentLanguageCode()
	GoldStoneAPI.registerDevice(
		HoneyLanguage.getLanguageSymbol(languageCode).toLowerCase(),
		token,
		SharedWallet.getGoldStoneID(),
		isChina,
		0,
		CountryCode.currentCountry
	) { result, error ->
		// 返回的 `Code` 是 `0` 存入 `SharedPreference` `token` 下次检查是否需要重新注册
		if (!result.isNullOrEmpty() && error.isNone()) {
			val code = JSONObject(result).safeGet("code")
			GoldStoneCode.isSuccess(code) { isSuccessful ->
				if (isSuccessful) {
					saveDataToSharedPreferences(SharesPreference.registerPush, token)
					launchUI(callback)
				}
			}
		}
	}
}