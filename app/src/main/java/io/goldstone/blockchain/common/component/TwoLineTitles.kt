package io.goldstone.blockchain.common.component

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.EmptyText
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 11:32 PM
 * @author KaySaith
 */

class TwoLineTitles(context: Context) : LinearLayout(context) {

	val title = textView {
		textSize = fontSize(14)
		textColor = Spectrum.white
	}

	val subtitle = textView {
		textSize = fontSize(12)
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

	fun setDialogStyle() {
		setBlackTitles()
		title.textSize = fontSize(16)
		subtitle.y += 5.uiPX()
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
		title.textSize = fontSize(12)
		subtitle.textSize = fontSize(10)
	}

	fun setWildStyle() {
		title.typeface = GoldStoneFont.heavy(context)
		subtitle.y += 5.uiPX()
	}

	fun setBigWhiteStyle() {
		title.apply {
			textSize = fontSize(24)
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.white
		}
		subtitle.apply {
			textSize = fontSize(12)
			textColor = Spectrum.opacity5White
			typeface = GoldStoneFont.medium(context)
		}
	}

	fun setQuotationStyle() {
		y += 10.uiPX()
		title.apply {
			textSize = fontSize(21)
			typeface = GoldStoneFont.black(context)
			textColor = GrayScale.black
		}
		subtitle.apply {
			textSize = fontSize(12)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.medium(context)
			y -= 5.uiPX()
		}
	}

	fun setOpacityWhiteStyle() {
		title.typeface = GoldStoneFont.heavy(context)
		title.textColor = Spectrum.opacity3White
		title.textSize = fontSize(18)
		subtitle.textColor = Spectrum.opacity3White
	}

	fun getSubtitleValue() = subtitle.text.toString()

}