package io.goldstone.blinnnk.common.component.button

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.textColor

/**
 * @date 2018/6/7 1:03 AM
 * @author KaySaith
 */
class StoneButton(context: Context) : TextView(context) {
	
	private val halfScreen = (ScreenSize.Width - 15.uiPX() * 2 - 10.uiPX()) / 2
	private val stoneColor = Color.parseColor("#FF12324D")
	
	init {
		layoutParams = LinearLayout.LayoutParams(halfScreen, 36.uiPX())
		addTouchRippleAnimation(stoneColor, Spectrum.green, RippleMode.Square, 5.uiPX().toFloat())
		elevation = 8.uiPX().toFloat()
		textColor = Spectrum.white
		textSize = fontSize(14)
		gravity = Gravity.CENTER
		typeface = GoldStoneFont.heavy(context)
	}
}