package io.goldstone.blinnnk.common.component.title

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.ElementID
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.textColor
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.wrapContent

/**
 * @date 22/03/2018 11:43 PM
 * @author KaySaith
 */
class AttentionTextView(context: Context) : TextView(context) {

	init {
		topPadding = 20.uiPX()
		bottomPadding = 10.uiPX()
		id = ElementID.attentionText
		textSize = fontSize(14)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		gravity = Gravity.START
	}

	fun isCenter() {
		gravity = Gravity.CENTER_HORIZONTAL
	}
}