package io.goldstone.blinnnk.common.component.title

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.blinnnk.extension.into
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class RadioWithTitle(context: Context) : LinearLayout(context) {
	private lateinit var radio: RadioButton
	private val titleView = TextView(context).apply {
		textSize = fontSize(12)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		layoutParams = LinearLayout.LayoutParams(wrapContent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
	}

	init {
		gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
		layoutParams = LinearLayout.LayoutParams(wrapContent, matchParent)
		linearLayout {
			lparams(wrapContent, matchParent)
			radio = radioButton {
				isClickable = false
				scaleX = 0.8f
				scaleY = 0.8f
			}
			titleView.into(this)
		}
	}

	fun setTitle(text: String) {
		titleView.text = text
	}

	fun getRadioStatus(): Boolean = radio.isChecked

	fun setRadioStatus(status: Boolean) {
		radio.isChecked = status
	}
}