package io.goldstone.blockchain.common.component.title

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.into
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class RadioWithTitle(context: Context) : LinearLayout(context) {
	private val radio = HoneyRadioButton(context).apply {
		isClickable = false
		scaleX = 0.8f
		scaleY = 0.8f
		setColorStyle(GrayScale.midGray, Spectrum.blue)
	}
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
			radio.into(this)
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