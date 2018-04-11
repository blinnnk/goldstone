@file:Suppress("UNCHECKED_CAST")

package io.goldstone.blockchain.common.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.support.v4.app.Fragment
import android.view.View
import com.blinnnk.util.PermissionCategory
import com.blinnnk.util.requestPermissionListener
import com.blinnnk.util.verifyMultiplePermissions
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 21/03/2018 11:12 PM
 * @author KaySaith
 */

/**
 * `View` 的便捷链式调用的方法
 * */

fun <T: View> T.assignWidth(width: Int): T {
  layoutParams.width = width
  return this
}

fun <T: View> T.assignHeight(height: Int): T {
  layoutParams.height = height
  return this
}

fun <T: View> T.click(callback: (T) -> Unit): T {
  onClick { callback(this@click) }
  return this
}

fun CharSequence.measureTextWidth(fontSize: Float): Float {
  val textPaint = Paint().apply {
    textSize = fontSize
  }
  return textPaint.measureText(this.toString())
}

fun<T> List<T>.toArrayList(): ArrayList<T> {
  return this.mapTo(arrayListOf()) { it }
}


fun Fragment.getMainActivity() = activity as? MainActivity
fun Context.getMainActivity() = this as? MainActivity

fun Context.alert(message: String) {
  alert(Appcompat, message).show()
}

fun Context.reload() {
  val startActivity = Intent(this, SplashActivity::class.java)
  val pendingIntentId = 123456
  val pendingIntent = PendingIntent.getActivity(this, pendingIntentId, startActivity,
    PendingIntent.FLAG_CANCEL_CURRENT
  )
  val service = getSystemService(Context.ALARM_SERVICE) as AlarmManager
  service.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
  System.exit(0)
}

fun Activity.checkPermissionThen(type: PermissionCategory, callback: () -> Unit) {
  if (verifyMultiplePermissions(type)) {
    callback()
  } else {
    requestPermissionListener(type) { hasPermission ->
      if (hasPermission) {
        callback()
      } else {
        checkPermissionThen(type, callback)
      }
    }
  }
}