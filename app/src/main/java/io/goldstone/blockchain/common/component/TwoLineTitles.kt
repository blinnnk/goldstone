package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

/**
 * @date 23/03/2018 11:32 PM
 * @author KaySaith
 * @rewriteDate 13/08/2018 15:37 PM
 * @rewriter wcx
 * @description 添加设置title和subtitle方法
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
	
	fun setBlackTitles(titleSize: Float = fontSize(14)) {
		title.apply {
			typeface = GoldStoneFont.heavy(context)
			textColor = GrayScale.black
			textSize = titleSize
		}
		subtitle.textColor = GrayScale.midGray
	}
	
	fun setBoldTiltes(
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

	fun setTitle(title: String) {
		this.title.text = title
	}

	fun setSubtitle(subtitle: String) {
		this.subtitle.text = subtitle
	}
}