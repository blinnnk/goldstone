@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.kernel.receiver

import android.annotation.SuppressLint
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
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TinyNumber
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneCode
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject

@Suppress("IMPLICIT_CAST_TO_ANY")
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
					handleTransactionNotification(context, JSONObject(it).safeGet("hash"))
				}
			}
		}
		clearAppIconReDot()
	}

	private fun handleTransactionNotification(
		context: Context?,
		content: String?
	) {
		val intent = Intent(context, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		intent.putExtra(IntentKey.hashFromNotify, content)
		context?.startActivity(intent)
	}

	companion object {
		fun clearAppIconReDot() {
			// 清楚所有 `App Icon` 上的小红点
			val notificationManager =
				GoldStoneAPI.context.applicationContext?.getSystemService(
					Context.NOTIFICATION_SERVICE
				) as NotificationManager
			notificationManager.cancelAll()
		}

		fun registerAddressesForPush(wallet: WalletTable?, isRemove: Boolean = false) {
			wallet?.apply {
				val option = if (isRemove) 0 else 1
				WalletTable.getTargetWalletType(this).let { type ->
					when (type) {
						WalletType.MultiChain -> {
							val ethSeries =
								AddressManagerPresenter.convertToChildAddresses(ethAddresses)
									.map { Pair(it.first, ChainType.ETH.id) }
							val btcSeries =
								AddressManagerPresenter.convertToChildAddresses(btcAddresses)
									.map { Pair(it.first, ChainType.BTC.id) }
							val ltcSeries =
								AddressManagerPresenter.convertToChildAddresses(ltcAddresses)
									.map { Pair(it.first, ChainType.LTC.id) }
							val bchSeries =
								AddressManagerPresenter.convertToChildAddresses(bchAddresses)
									.map { Pair(it.first, ChainType.BCH.id) }
							val btcTestSeries =
								AddressManagerPresenter.convertToChildAddresses(btcSeriesTestAddresses)
									.map { Pair(it.first, ChainType.AllTest.id) }
							val etcSeries =
								AddressManagerPresenter.convertToChildAddresses(etcAddresses)
									.map { Pair(it.first, ChainType.ETC.id) }
							val eosSeries =
								AddressManagerPresenter.convertToChildAddresses(eosAddresses)
									.map { Pair(it.first, ChainType.EOS.id) }
							val all =
								ethSeries
									.plus(btcSeries)
									.plus(btcTestSeries)
									.plus(etcSeries)
									.plus(ltcSeries)
									.plus(bchSeries)
									.plus(eosSeries)
									.map {
										AddressCommissionModel(it.first, it.second, option, id)
									}.map { prepareAddressData(it) }
							GoldStoneAPI.registerWalletAddresses(
								AesCrypto.encrypt("$all").orEmpty(),
								{
									LogUtil.error("registerAddressesAfterGenerateWallet", it)
								}
							) {
								if (!isRemove) updateRegisterAddressesStatus(it)
							}
						}

						WalletType.BTCOnly -> {
							registerSingleAddress(
								AddressCommissionModel(
									currentBTCAddress,
									ChainType.BTC.id,
									option,
									id
								)
							)
						}
						WalletType.BCHOnly ->
							registerSingleAddress(
								AddressCommissionModel(
									currentBCHAddress,
									ChainType.BCH.id,
									option,
									id
								)
							)
						WalletType.EOSOnly ->
							registerSingleAddress(
								AddressCommissionModel(
									currentEOSAddress,
									ChainType.EOS.id,
									option,
									id
								)
							)
						WalletType.LTCOnly ->
							registerSingleAddress(
								AddressCommissionModel(
									currentLTCAddress,
									ChainType.LTC.id,
									option,
									id
								)
							)
						WalletType.BTCTestOnly ->
							registerSingleAddress(
								AddressCommissionModel(
									currentBTCSeriesTestAddress,
									ChainType.AllTest.id,
									option,
									id
								)
							)
						WalletType.ETHERCAndETCOnly ->
							registerSingleAddress(
								AddressCommissionModel(
									currentETHAndERCAddress,
									ChainType.ETH.id,
									option,
									id
								)
							)
					}
				}
			}
		}

		fun registerSingleAddress(model: AddressCommissionModel) {
			listOf(model).map { prepareAddressData(it) }.let { it ->
				GoldStoneAPI.registerWalletAddresses(
					AesCrypto.encrypt("$it").orEmpty(),
					{
						LogUtil.error("registerAddressesAfterGenerateWallet", it)
					}
				) {
					// 如果是注册地址 `option = 1` 的情况下在数据库标记注册成功与否的状态
					if (model.option == 1) GoldStoneCode.isSuccess(it.toJsonObject()["code"]) { isSucceed ->
						isSucceed isTrue {
							AppConfigTable.updateRegisterAddressesStatus(true)
							LogUtil.debug("XinGePushReceiver", "code: $it")
						} otherwise {
							// 服务器返回错误的时候标记注册失败
							AppConfigTable.updateRegisterAddressesStatus(false)
						}
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

		private fun updateRegisterAddressesStatus(code: String) {
			GoldStoneCode.isSuccess(code.toJsonObject()["code"]) { isSucceed ->
				isSucceed isTrue {
					AppConfigTable.updateRegisterAddressesStatus(true)
					LogUtil.debug("XinGePushReceiver", "code: $code")
				} otherwise {
					// 服务器返回错误的时候标记注册失败
					AppConfigTable.updateRegisterAddressesStatus(false)
				}
			}
		}
	}
}

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
					LogUtil.debug(this.javaClass.simpleName, "token: $it")
					if (it == token) return@registerDevice
				}
				// 在本地数据库记录 `Push Token`
				WalletTable.getCurrentWallet {
					XinGePushReceiver.registerAddressesForPush(this)
				}
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
	// 把 `Token` 记录在本地数据库
	AppConfigTable.updatePushToken(token)
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
					Config.getCurrentChain().toInt(),
					CountryCode.currentCountry,
					{
						// Error Callback
						LogUtil.error("registerDevice")
						callback()
					}
				) { it ->
					// 返回的 `Code` 是 `0` 存入 `SharedPreference` `token` 下次检查是否需要重新注册
					GoldStoneCode.isSuccess(it.toJsonObject()["code"]) { isSuccessful ->
						if (isSuccessful) {
							saveDataToSharedPreferences(SharesPreference.registerPush, token)
							uiThread { callback() }
						}
					}
				}
			}
		}
	}
}