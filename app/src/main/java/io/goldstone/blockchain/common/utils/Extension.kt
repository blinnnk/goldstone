@file:Suppress("UNCHECKED_CAST")

package io.goldstone.blockchain.common.utils

import android.graphics.Paint
import android.view.View
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
