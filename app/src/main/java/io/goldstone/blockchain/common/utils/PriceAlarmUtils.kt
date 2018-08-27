package io.goldstone.blockchain.common.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter.PriceAlarmReceiver
import kotlinx.coroutines.experimental.async

/**
 * @data 07/23/2018 20/26
 * @author wcx
 * @description 唤醒闹钟工具类
 */
object PriceAlarmUtils {

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
			PriceAlarmReceiver.stopAlarmClock()
			val intent = Intent(
				context,
				PriceAlarmReceiver::class.java
			)
			val bundle = Bundle()
			bundle.putBoolean(
				ArgumentKey.turnOffPriceAlarmReceiverMessageFlag,
				true
			)
			intent.putExtras(bundle)
			intent.action = "Alarm_clock"
			val pendingIntent = PendingIntent.getBroadcast(
				context,
				alarmId,
				intent,
				PendingIntent.FLAG_CANCEL_CURRENT
			)

			val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
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
		val intent = Intent(
			context,
			PriceAlarmReceiver::class.java
		)
		val bundle = Bundle()
		bundle.putBoolean(
			"msg",
			false
		)
		intent.putExtras(bundle)
		intent.action = "Alarm_clock"
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			alarmId,
			intent,
			0
		)
		val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
		alarmManager.cancel(pendingIntent)
		pendingIntent.cancel()
	}
}