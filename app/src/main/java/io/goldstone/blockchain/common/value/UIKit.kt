package io.goldstone.blockchain.common.value

import android.content.res.Resources
import android.graphics.Color
import com.blinnnk.uikit.uiPX

/**
 * @date 21/03/2018 7:30 PM
 * @author KaySaith
 * @description
 * Common interface parameters, including color, original size, etc.
 */

object Grayscale {
  @JvmField val black = Color.parseColor("#FF000000")
  @JvmField val lightGray = Color.parseColor("#FFE5E5E5")
  @JvmField val whiteGray = Color.parseColor("#FFF1F1F1")
  @JvmField val gray = Color.parseColor("#FFAAAAAA")
  @JvmField val midGray = Color.parseColor("#FFCCCCCC")
}

object Spectrum {
  @JvmField val white = Color.parseColor("#FFFFFFFF")
  @JvmField val blue = Color.parseColor("#FF00B1FF")
  @JvmField val green = Color.parseColor("#FF1CC881")
  @JvmField val darkBlue = Color.parseColor("#FF0863B8")
  @JvmField val red = Color.parseColor("#FFFA0D0D")
  @JvmField val lightRed = Color.parseColor("#FFFF6464")
  @JvmField val yellow = Color.parseColor("#FFFFF53C")
}

object FontSize {
  @JvmField val header = 6.uiPX().toFloat()
  @JvmField val cellTitle = 16.uiPX().toFloat()
  @JvmField val cellSubtitle = 11.uiPX().toFloat()
  @JvmField val cellDate = 10.uiPX().toFloat()
  @JvmField val cellNumber = 18.uiPX().toFloat()
}

object ShadowSize {
  @JvmField val Button = 15.uiPX()
  @JvmField val Overlay = 15.uiPX()
}

object PaddingSize {
  @JvmField val device = 20.uiPX()
  @JvmField val content = 10.uiPX()
  @JvmField val lineSpace = 2.uiPX()
}

object CornerSize {
  @JvmField val default = 10.uiPX().toFloat()
  @JvmField val middle = 15.uiPX().toFloat()
  @JvmField val big = 25.uiPX().toFloat()
}

object BorderSize {
  @JvmField val default = 1.uiPX().toFloat()
  @JvmField val bold = 2.uiPX().toFloat()
  @JvmField val crude = 3.uiPX().toFloat()
}

object ScreenSize {
  @JvmField
  val Width = Resources.getSystem().displayMetrics.widthPixels
  // This height will lose control bar height when there is a soft control bar
  @JvmField
  val Height = Resources.getSystem().displayMetrics.heightPixels
  @JvmField
  val centerX = Width / 2f
  @JvmField
  val centerY = Height / 2f
  @JvmField val statusBarHeight = getStatusBarHeight()
  val widthWithPadding = Resources.getSystem().displayMetrics.widthPixels - PaddingSize.device * 2
}

// 获取状态栏高度的方法
private fun getStatusBarHeight(): Int {
  var result = 0
  val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = Resources.getSystem().getDimensionPixelSize(resourceId)
  }
  return result
}

