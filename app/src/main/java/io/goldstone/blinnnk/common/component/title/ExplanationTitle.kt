package io.goldstone.blinnnk.common.component.title

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.textColor

/**
 * @date 2018/5/28 6:06 PM
 * @author KaySaith
 */
class ExplanationTitle(context: Context) : TextView(context) {
	
	init {
		textSize = fontSize(15)
		typeface = GoldStoneFont.heavy(context)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.Width, 30.uiPX()).apply {
			topMargin = 20.uiPX()
			bottomMargin = 20.uiPX()
		}
		textColor = Spectrum.blue
		gravity = Gravity.CENTER
	}
	
}