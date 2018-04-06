@file:Suppress("UNCHECKED_CAST")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.graphics.Paint
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.goldstone.blockchain.module.home.home.view.MainActivity
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

/** Context Utils */

private const val sharedPreferencesName = "share_date"

fun <T> Context.saveDataToSharedPreferences(key: String, data: T) {
  val sharedPreferencesEdit = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE).edit()
  when (data) {
    is String -> {
      sharedPreferencesEdit.putString(key, data)
      sharedPreferencesEdit.apply()
    }
    is Int -> {
      sharedPreferencesEdit.putInt(key, data)
      sharedPreferencesEdit.apply()
    }
    is Boolean -> {
      sharedPreferencesEdit.putBoolean(key, data)
      sharedPreferencesEdit.apply()
    }
    is Float -> {
      sharedPreferencesEdit.putFloat(key, data)
      sharedPreferencesEdit.apply()
    }
    is Long -> {
      sharedPreferencesEdit.putLong(key, data)
      sharedPreferencesEdit.apply()
    }
  }
}

fun Context.getIntFromSharedPreferences(key: String): Int =
  getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE).getInt(key, 100)

inline fun<reified T: AppCompatActivity> Fragment.reboot() {
  (activity as? T)?.apply {
    finish()
    startActivity(intent)
  }
}

fun Fragment.getMainActivity() = activity as? MainActivity
