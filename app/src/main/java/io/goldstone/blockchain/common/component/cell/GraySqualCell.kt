package io.goldstone.blockchain.common.component.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/04/2018 8:56 AM
 * @author KaySaith
 */
open class GraySquareCell(context: Context) : RelativeLayout(context) {

	protected val title = TextView(context).apply {
		textSize = fontSize(12)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.gray
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		x += 20.uiPX()
		gravity = Gravity.CENTER_VERTICAL
	}

	protected val subtitle = TextView(context).apply {
		visibility = View.GONE
		textSize = fontSize(12)
		typeface = GoldStoneFont.black(context)
		textColor = GrayScale.black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		x -= 20.uiPX()
		gravity = Gravity.END or Gravity.CENTER_VERTICAL
	}
	private val arrow by lazy {
		ImageView(context).apply {
			imageResource = R.drawable.arrow_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.gray)
			layoutParams = RelativeLayout.LayoutParams(45.uiPX(), matchParent)
		}
	}

	private val radio by lazy {
		HoneyRadioButton(context)
	}

	private var container: RelativeLayout
	private val shadowSize = 3

	init {
		// 低端机型导致的 SetMargin 不识别, 顾此采用夹层方式实现 `Margin` 的阴影
		container = relativeLayout {
			lparams(ScreenSize.widthWithPadding - shadowSize * 2, 45.uiPX())
			setCenterInParent()
			addCorner(CornerSize.cell, GrayScale.whiteGray)
			elevation = 3f
			addView(View(context).apply {
				layoutParams = RelativeLayout.LayoutParams(6.uiPX(), matchParent)
				backgroundColor = GrayScale.midGray
			})
			addView(title)
			addView(subtitle)
		}
		layoutParams = RelativeLayout.LayoutParams(matchParent, 51.uiPX())
	}

	fun <T : CharSequence> setTitle(text: T) {
		title.text = text
	}

	fun showArrow() {
		arrow.into(container)
		arrow.setAlignParentRight()
		subtitle.x -= 20.uiPX()
	}

	fun showRadio() {
		radio.setColorStyle(GrayScale.midGray, Spectrum.blue)
		radio.scaleX = 0.8f
		radio.scaleY = 0.8f
		radio.into(container)
		radio.setAlignParentRight()
		radio.setCenterInVertical()
		radio.x -= 10.uiPX()
	}

	fun setRadioStatus(
		status: Boolean,
		action: () -> Unit = {}
	) {
		radio.isChecked = status
		radio.onClick {
			radio.preventDuplicateClicks()
			action()
		}
	}

	fun setPriceTitle(text: String) {
		title.text =
			CustomTargetTextStyle(
				QuotationText.highAndLow,
				"$text ${QuotationText.highAndLow}",
				GrayScale.black,
				9.uiPX(),
				true,
				false
			)
	}

	fun setPriceSubtitle(
		text: String,
		currency: String
	) {
		subtitle.visibility = View.VISIBLE
		subtitle.text =
			CustomTargetTextStyle(currency, "$text  $currency", GrayScale.black, 8.uiPX(), true, false)
	}

	fun setTitle(text: String) {
		title.text = text
	}

	@SuppressLint("SetTextI18n")
	fun setSubtitle(content: String) {
		subtitle.visibility = View.VISIBLE
		subtitle.text = object : FixTextLength() {
			override var text = content
			override val maxWidth = ScreenSize.widthWithPadding * 0.55f
			override val textSize: Float = fontSize(12.uiPX())
		}.getFixString()
	}
}