package io.goldstone.blinnnk.common.component

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/3 1:55 PM
 * @author KaySaith
 */
class ArrowIconView(context: Context) : ImageView(context) {
	
	init {
		layoutParams = LinearLayout.LayoutParams(36.uiPX(), wrapContent)
		imageResource = R.drawable.arrow_icon
		setColorFilter(Spectrum.white)
		scaleType = ScaleType.FIT_CENTER
		setOpacityStyle()
	}
	
	fun setGrayStyle() {
		alpha = 1f
		setColorFilter(GrayScale.lightGray)
	}
	
	fun setWhiteSytle() {
		alpha = 1f
		setColorFilter(Spectrum.white)
	}
	
	fun setOpacityStyle() {
		alpha = 0.5f
	}
}