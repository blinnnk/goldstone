@file:Suppress("NOTHING_TO_INLINE")

package io.goldstone.blockchain.common.component.title

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewManager
import android.widget.LinearLayout
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView


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
		setLineSpacing(2f, 0.8f)
		y -= 3.uiPX()
	}
	var isFloatRight by observing(false) {
		gravity = Gravity.END
		title.gravity = Gravity.END
		subtitle.gravity = Gravity.END
	}
	var isCenter: Boolean by observing(false) {
		if (isCenter) {
			gravity = Gravity.CENTER_HORIZONTAL
			subtitle.gravity = Gravity.CENTER_HORIZONTAL
			title.gravity = Gravity.CENTER_HORIZONTAL
		} else {
			gravity = Gravity.START
			subtitle.gravity = Gravity.START
			title.gravity = Gravity.START
		}
	}

	init {
		orientation = VERTICAL
	}

	fun setBlackTitles(
		titleSize: Float = fontSize(14),
		lineSpace: Int = 0,
		subtitleSize: Float = fontSize(12)
	) {
		title.apply {
			typeface = GoldStoneFont.heavy(context)
			textColor = GrayScale.black
			textSize = titleSize
		}
		subtitle.textSize = subtitleSize
		subtitle.y += lineSpace
		subtitle.textColor = GrayScale.midGray
	}

	fun setBoldTitles(
		color: Int = Spectrum.white,
		subtitleColor: Int = Spectrum.opacity5White
	) {
		title.apply {
			typeface = GoldStoneFont.black(context)
			textColor = color
			textSize = fontSize(16)
		}
		subtitle.textColor = subtitleColor
	}

	fun setDescriptionTitles() {
		title.apply {
			typeface = GoldStoneFont.black(context)
			textColor = GrayScale.midGray
			textSize = fontSize(12)
		}
		subtitle.textColor = GrayScale.black
		subtitle.textSize = fontSize(14)
		val typeface = GoldStoneFont.black(context)
		subtitle.setTypeface(typeface, Typeface.ITALIC)
	}

	fun setColorStyle(color: Int) {
		val colorAnim =
			ObjectAnimator.ofInt(title, "textColor", GrayScale.whiteGray, color)
		colorAnim.setEvaluator(ArgbEvaluator())
		colorAnim.start()
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

	fun setBigWhiteStyle(titleSize: Int = 24, subtitleSize: Int = 12, lineSpace: Int = 0) {
		title.apply {
			textSize = fontSize(titleSize)
			typeface = GoldStoneFont.black(context)
			textColor = Spectrum.white
		}
		subtitle.apply {
			setMargins<LinearLayout.LayoutParams> { topMargin = lineSpace }
			textSize = fontSize(subtitleSize)
			textColor = Spectrum.opacity5White
			typeface = GoldStoneFont.medium(context)
		}
	}

	fun setQuotationStyle() {
		y += 10.uiPX()
		title.apply {
			textSize = fontSize(20)
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

	fun setSubtitleLineCount(lineCount: Int) {
		subtitle.maxLines = lineCount
	}
}

inline fun ViewManager.twoLineTitles() = twoLineTitles {}
inline fun ViewManager.twoLineTitles(init: TwoLineTitles.() -> Unit) = ankoView({ TwoLineTitles(it) }, 0, init)