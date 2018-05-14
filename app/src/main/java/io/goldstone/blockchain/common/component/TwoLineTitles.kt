package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.EmptyText
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.sp
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 23/03/2018 11:32 PM
 * @author KaySaith
 */

class TwoLineTitles(context: Context) : LinearLayout(context) {

	val title = textView {
		textSize = 5.uiPX().toFloat()
		textColor = Spectrum.white
	}

	val subtitle = textView {
		textSize = sp(4).toFloat()
		typeface = GoldStoneFont.medium(context)
		textColor = Spectrum.opacity5White
		y -= 3.uiPX()
	}

	var isFloatRight by observing(false) {
		gravity = Gravity.END
	}

	var isCenter by observing(false) {
		gravity = Gravity.CENTER_HORIZONTAL
		subtitle.gravity = Gravity.CENTER_HORIZONTAL
		title.gravity = Gravity.CENTER_HORIZONTAL
	}

	init {
		orientation = VERTICAL
	}

	fun setBlackTitles() {
		title.apply {
			typeface = GoldStoneFont.heavy(context)
			textColor = GrayScale.black
		}
		subtitle.textColor = GrayScale.gray
	}

	fun setColorStyle(color: Int) {
		title.textColor = color
		subtitle.textColor = color
	}

	fun setGrayTitles() {
		title.apply {
			typeface = GoldStoneFont.book(context)
			textColor = GrayScale.black
		}
		title.textColor = GrayScale.gray
		subtitle.y += 3.uiPX()
		subtitle.textColor = GrayScale.midGray
	}

	fun setSmallStyle() {
		title.textSize = 4.uiPX() + 1f
		subtitle.textSize = 3.uiPX() + 1f
	}

	fun setWildStyle() {
		title.typeface = GoldStoneFont.heavy(context)
		subtitle.y += 5.uiPX()
	}

	fun setBigWhiteStyle() {
		title.apply {
			textSize = 7.uiPX().toFloat()
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.white
		}
		subtitle.apply {
			textSize = 4.uiPX().toFloat()
			textColor = Spectrum.opacity5White
			typeface = GoldStoneFont.medium(context)
		}
	}

	fun setQuotationStyle() {
		y += 10.uiPX()
		title.apply {
			textSize = 7.uiPX().toFloat()
			typeface = GoldStoneFont.black(context)
			textColor = GrayScale.black
		}
		subtitle.apply {
			textSize = 4.uiPX().toFloat()
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.medium(context)
			y -= 5.uiPX()
		}
	}

	fun setOpacityWhiteStyle() {
		title.typeface = GoldStoneFont.heavy(context)
		title.textColor = Spectrum.opacity3White
		title.textSize = 6.uiPX().toFloat()
		subtitle.textColor = Spectrum.opacity3White
	}

	fun getSubtitleValue() = subtitle.text.toString()

}