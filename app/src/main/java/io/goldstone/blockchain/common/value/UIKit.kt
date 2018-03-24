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

object GrayScale {
  @JvmField val black = Color.parseColor("#FF000000")
  @JvmField val lightGray = Color.parseColor("#FFE5E5E5")
  @JvmField val whiteGray = Color.parseColor("#FFF1F1F1")
  @JvmField val gray = Color.parseColor("#FFAAAAAA")
  @JvmField val midGray = Color.parseColor("#FFCCCCCC")
  @JvmField val Opacity5Black = Color.parseColor("#80000000")
  @JvmField val Opacity7Black = Color.parseColor("#B3000000")
  @JvmField val Opacity8Black = Color.parseColor("#CC000000")
}

object Spectrum {
  @JvmField val white = Color.parseColor("#FFFFFFFF")
  @JvmField val blue = Color.parseColor("#FF00B1FF")
  @JvmField val green = Color.parseColor("#FF1CC881")
  @JvmField val darkBlue = Color.parseColor("#FF0863B8")
  @JvmField val red = Color.parseColor("#FFFA0D0D")
  @JvmField val lightRed = Color.parseColor("#FFFF6464")
  @JvmField val yellow = Color.parseColor("#FFFFF53C")
  @JvmField val opacity1White = Color.parseColor("#1AFFFFFF")
  @JvmField val opacity3White = Color.parseColor("#4DFFFFFF")
  @JvmField val opacity2White = Color.parseColor("#33FFFFFF")
  @JvmField val opacity5White = Color.parseColor("#80FFFFFF")
}

object FontSize {
  @JvmField val header = 6.uiPX().toFloat()
  @JvmField val cellTitle = 16.uiPX().toFloat()
  @JvmField val cellSubtitle = 11.uiPX().toFloat()
  @JvmField val cellDate = 10.uiPX().toFloat()
  @JvmField val cellNumber = 18.uiPX().toFloat()
}

object ShadowSize {
  @JvmField val Button = 10.uiPX().toFloat()
  @JvmField val Overlay = 15.uiPX().toFloat()
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
  val widthWithPadding = Resources.getSystem().displayMetrics.widthPixels - PaddingSize.device * 2
}

