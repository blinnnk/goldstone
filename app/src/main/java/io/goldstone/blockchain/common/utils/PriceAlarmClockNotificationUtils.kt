package io.goldstone.blockchain.common.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.util.Log
import io.goldstone.blockchain.R
import android.content.Intent
import io.goldstone.blockchain.module.home.home.view.MainActivity


/**
 * @data 07/23/2018 20/26
 * @author wcx
 * @description 唤醒闹钟通知工具类
 */
object PriceAlarmClockNotificationUtils {

  @SuppressLint("WrongConstant")
  fun sendPriceAlarmClockNotification(
    context: Context,
    title: String,
    content: String,
    id: String?) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val sendID: String? = id ?: "price_alarm_clock_01"
    Log.e("sendID", "++" + sendID)
    val channel: NotificationChannel? // 创建Notification Channel对象
    // 如果版本号为8.0以上,定义Notification Channel
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      channel = NotificationChannel(
        sendID,
        "price_alarm_clock",
        NotificationManager.IMPORTANCE_MAX) // 设置唯一的渠道通知Id
      channel.apply {
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true) // 开启震动
        vibrationPattern = longArrayOf(1000, 1000, 1000, 1000) // 8.0以下版本的效果一样,都是震动
      }
      manager.createNotificationChannel(channel) // 在NotificationManager中注册渠道通知对象
    }
    // 定义通知,都可适配
    val notification = NotificationCompat.Builder(context, "1")
    notification.apply {
      setContentTitle("" + title)
      setContentText("" + content)
//      mContentTitle = "" + title
//      mContentText = "" + content
      setWhen(System.currentTimeMillis())
      setSmallIcon(R.mipmap.ic_launcher)
      setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(context.resources,
        R.mipmap.ic_launcher)))
      setVibrate(longArrayOf(1000, 1000, 1000, 1000))
      setLights(Color.RED, 1000, 1000) // 震动和灯光一样都需要Notification Channel开启灯光和震动,才能有效果
      priority = NotificationCompat.PRIORITY_MAX // 悬浮通知
      setChannelId(sendID!!) // 设置通知Id
      setAutoCancel(true)
      setDefaults(Notification.DEFAULT_ALL)

      val notificationIntent = Intent(Intent.ACTION_MAIN)
      notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
      notificationIntent.setClass(context, MainActivity::class.java)
      notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
      val intent = PendingIntent.getActivity(context, 0,
        notificationIntent, 0)
      setContentIntent(intent)
    }
    manager.notify(1, notification.build())
  }
}