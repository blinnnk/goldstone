package io.goldstone.blockchain.common.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.receiver.PriceAlarmClockReceiver
import kotlinx.coroutines.experimental.async

/**
 * @data 07/23/2018 20/26
 * @author wcx
 * @description 唤醒闹钟工具类
 */
object PriceAlarmClockUtils {

  private lateinit var alarmManager: AlarmManager
  private lateinit var pendingIntent: PendingIntent
  private lateinit var intent: Intent
  private var intervalTime = false

  /**
   * @data 07/24/2018 10/02
   * @author wcx
   * @description 发送闹钟震动广播
   */
  fun sendAlarmReceiver(
    time: Long,
    context: Context,
    alarmId: Int
  ) {
    if (!intervalTime) {
      intervalTime = true
      async {
        Thread.sleep(1000)
        intervalTime = false
      }
      PriceAlarmClockReceiver.stopAlarmClock()
      alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
      intent = Intent(
        context,
        PriceAlarmClockReceiver::class.java
      )
      val bundle = Bundle()
      bundle.putBoolean(
        "msg",
        true
      )
      intent.putExtras(bundle)
      intent.action = "Alarm_clock"
      pendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT
      )
      alarmManager.set(
        AlarmManager.RTC_WAKEUP,
        time,
        pendingIntent
      )
    }
  }

  /**
   * @data 07/24/2018 10/02
   * @author wcx
   * @description 停止闹钟震动广播
   */
  fun stopAlarmReceiver(
    context: Context,
    alarmId: Int
  ) {
    alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    intent = Intent(
      context,
      PriceAlarmClockReceiver::class.java
    )
    val bundle = Bundle()
    bundle.putBoolean(
      "msg",
      false
    )
    intent.putExtras(bundle)
    intent.action = "Alarm_clock"
    pendingIntent = PendingIntent.getBroadcast(
      context,
      alarmId,
      intent,
      0
    )
    alarmManager.cancel(pendingIntent)
    pendingIntent.cancel()
  }
}