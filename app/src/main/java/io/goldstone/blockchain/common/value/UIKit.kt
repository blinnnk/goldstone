package io.goldstone.blockchain.common.value

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import com.blinnnk.uikit.uiPX
import org.jetbrains.anko.px2sp

/**
 * @date 21/03/2018 7:30 PM
 * @author KaySaith
 * @description
 * Common interface parameters, including color, original size, etc.
 */
object GrayScale {
	
	@JvmField
	val black = Color.parseColor("#FF000000")
	@JvmField
	val lightGray = Color.parseColor("#FFE5E5E5")
	@JvmField
	val whiteGray = Color.parseColor("#FFF1F1F1")
	@JvmField
	val gray = Color.parseColor("#FFAAAAAA")
	@JvmField
	val midGray = Color.parseColor("#FFCCCCCC")
	@JvmField
	val Opacity1Black = Color.parseColor("#1A000000")
	@JvmField
	val Opacity2Black = Color.parseColor("#33000000")
	@JvmField
	val Opacity3Black = Color.parseColor("#4D000000")
	@JvmField
	val Opacity5Black = Color.parseColor("#80000000")
	@JvmField
	val Opacity8Black = Color.parseColor("#CC000000")
}

object Spectrum {
	@JvmField
	val white = Color.parseColor("#FFFFFFFF")
	@JvmField
	val blue = Color.parseColor("#FF235682")
	@JvmField
	val deepBlue = Color.parseColor("#FF17446B")
	@JvmField
	val lightBlue = Color.parseColor("#FF2882D2")
	@JvmField
	val green = Color.parseColor("#FF1CC881")
	@JvmField
	val opacity8Green = Color.parseColor("#CC1CC881")
	@JvmField
	val lightGreen = Color.parseColor("#801CC881")
	@JvmField
	val darkBlue = Color.parseColor("#FF0863B8")
	@JvmField
	val red = Color.parseColor("#FFFA0D0D")
	@JvmField
	val lightRed = Color.parseColor("#FFFF6464")
	@JvmField
	val yellow = Color.parseColor("#FFFFF988")
	@JvmField
	val DarkYellow = Color.parseColor("#FFF3EA3C")
	@JvmField
	val opacity1White = Color.parseColor("#1AFFFFFF")
	@JvmField
	val opacity3White = Color.parseColor("#4DFFFFFF")
	@JvmField
	val opacity2White = Color.parseColor("#33FFFFFF")
	@JvmField
	val opacity5White = Color.parseColor("#80FFFFFF")
}

object WalletColor {
	private val purple = Color.parseColor("#FF3F4E92")
	private val blue = Color.parseColor("#FF2A7EDA")
	private val cyan = Color.parseColor("#FF1BA2A9")
	private val darkPuple = Color.parseColor("#FF603361")
	private val grayYellow = Color.parseColor("#FF717335")
	private val blueGray = Color.parseColor("#FF4B5C6E")
	fun getALl(): List<Int> = listOf(purple, blue, cyan, darkPuple, grayYellow, blueGray)
}

object ShadowSize {
	@JvmField
	val Header = 3.uiPX().toFloat()
	@JvmField
	val Overlay = 15.uiPX().toFloat()
	@JvmField
	val default = 10.uiPX().toFloat()
}

object PaddingSize {
	@JvmField
	val device = 20.uiPX()
	@JvmField
	val content = 10.uiPX()
}

object CornerSize {
	@JvmField
	val cell = 3.uiPX()
	@JvmField
	val small = 5.uiPX()
	@JvmField
	val default = 10.uiPX().toFloat()
	@JvmField
	val middle = 15.uiPX().toFloat()
}

object BorderSize {
	@JvmField
	val default = 1.uiPX().toFloat()
	@JvmField
	val bold = 2.uiPX().toFloat()
	@JvmField
	val crude = 3.uiPX().toFloat()
}

object ScreenSize {
	val widthWithPadding = Resources.getSystem().displayMetrics.widthPixels - PaddingSize.device * 2
	val fullHeight = Resources.getSystem().displayMetrics.heightPixels
	var heightWithOutHeader = fullHeight - HomeSize.headerHeight
}

object CommonCellSize {
	@JvmField
	val rightPadding = 30.uiPX()
	@JvmField
	val iconPadding = 40.uiPX()
}

object TransactionSize {
	@JvmField
	val headerView = 220.uiPX()
}

object HomeSize {
	@JvmField
	val tabBarHeight = 50.uiPX()
	@JvmField
	val sliderHeaderHeight = 65.uiPX()
	@JvmField
	val headerHeight = 65.uiPX()
}

object AvatarSize {
	@JvmField
	val big = 75.uiPX()
	@JvmField
	val middle = 60.uiPX()
}

object TokenDetailSize {
	@JvmField
	val headerHeight = 300.uiPX()
}

object Count {
	const val pinCode = 4
	const val retry = 5
}

object WalletDetailSize {
	@JvmField
	val headerHeight = 365.uiPX()
}

fun View.fontSize(defaultSize: Int): Float {
	return px2sp((Resources.getSystem().displayMetrics.density * defaultSize).toInt())
}

object Duration {
	const val wave = 1500L
}