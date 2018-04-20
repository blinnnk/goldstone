@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.kernel.receiver

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.media.RingtoneManager
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNotNull
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.google.gson.JsonArray
import com.tencent.android.tpush.*
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.jetbrains.anko.configuration

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
      val wake
        = powerManager.newWakeLock(
        PowerManager.FULL_WAKE_LOCK
          or PowerManager.ACQUIRE_CAUSES_WAKEUP
          or PowerManager.ON_AFTER_RELEASE,
        "weakScreen")
      wake.acquire(10000)
      val wakeLock
        = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "weakScreen")
      wakeLock.acquire(10000)
    }

    val builder = NotificationCompat.Builder(context.applicationContext)
    builder
      .setContentTitle("GoldStone")
      .setContentText(content)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
  }

  override fun onSetTagResult(p0: Context?, p1: Int, p2: String?) {
    //
  }

  override fun onNotifactionShowedResult(context: Context?, notifiShowedRlt: XGPushShowedResult?) {
    if (context == null || notifiShowedRlt == null) return
    showNotificationOnLockScreen(context, notifiShowedRlt.toString())
  }

  override fun onUnregisterResult(context: Context?, p1: Int) {
    if (context == null) {
      return
    }
  }

  override fun onDeleteTagResult(p0: Context?, p1: Int, p2: String?) {
    //
  }

  override fun onRegisterResult(p0: Context?, p1: Int, p2: XGPushRegisterResult?) {
    //
  }

  @SuppressLint("PrivateResource")
  override fun onTextMessage(context: Context?, message: XGPushTextMessage?) {
    context.isNotNull {
      showNotificationOnLockScreen(context!!, message.toString())
    }
  }

  override fun onNotifactionClickedResult(p0: Context?, p1: XGPushClickedResult?) {
    //
  }

  companion object {
    fun registerWalletAddressForPush() {
      val stringArray = JsonArray()
      // 获取本地的所有钱包
      WalletTable.getAll {
        forEachOrEnd { item, isEnd ->
          stringArray.add(item.address)
          if (isEnd) {
            // 注册钱包地址
            GoldStoneAPI.registerWalletAddress(stringArray, GoldStoneApp.deviceID.orEmpty()) {
              if (it.toJsonObject()["code"] == 0) {
                println(it)
                println("Register Address Worked")
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
      // 如果本地有注册成功的标记则不再注册
      getStringFromSharedPreferences(SharesPreference.registerPush).let {
        Log.d("DEBUG", it)
        if (it == token) return
      }
      // 准备信息注册设备的信息到服务器, 为了 `Push` 做的工作
      registerDevice(token.toString())
      XinGePushReceiver.registerWalletAddressForPush()
    }
    override fun onFail(p0: Any?, p1: Int, p2: String?) {}
  })
}

@SuppressLint("HardwareIds")
private fun Application.registerDevice(token: String) {
  // 没有注册过就开始注册
  val isChina
    = if (CountryCode.currentCountry == CountryCode.china.country) TinyNumber.True.value
  else TinyNumber.False.value
  GoldStoneAPI.registerDevice(
    configuration.locale.language,
    token,
    GoldStoneApp.deviceID.orEmpty(),
    isChina,
    TinyNumber.True.value
  ) {
    // 返回的 `Code` 是 `0` 存入 `SharedPreference` `token` 下次检查是否需要重新注册
    if (it.toJsonObject()["code"] == 0) {
      saveDataToSharedPreferences(SharesPreference.registerPush, token)
    }
  }
}