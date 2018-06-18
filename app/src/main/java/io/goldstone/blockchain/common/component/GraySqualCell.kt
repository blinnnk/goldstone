package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 25/04/2018 8:56 AM
 * @author KaySaith
 */

open class GraySqualCell(context: Context) : RelativeLayout(context) {

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
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		x -= 20.uiPX()
		gravity = Gravity.END or Gravity.CENTER_VERTICAL
	}

	private val arrow by lazy {
		ImageView(context).apply {
			imageResource = R.drawable.arrow_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.midGray)
			layoutParams = RelativeLayout.LayoutParams(45.uiPX(), matchParent)
		}
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 45.uiPX())
		setMargins<RelativeLayout.LayoutParams> {
			bottomMargin = 5.uiPX()
		}
		backgroundColor = GrayScale.whiteGray
		this.addView(title)
		this.addView(subtitle)
	}

	fun <T : CharSequence> setTitle(text: T) {
		title.text = text
	}

	fun showArrow() {
		arrow.into(this)
		arrow.setAlignParentRight()
		subtitle.x -= 20.uiPX()
	}

	fun setPriceTitle(text: String) {
		title.text =
			CustomTargetTextStyle(
				QuotationText.highAndLow, "$text ${QuotationText.highAndLow}", GrayScale.black, 9.uiPX(), true, false
			)
	}

	fun setPricesubtitle(
		text: String,
		currency: String
	) {
		subtitle.visibility = View.VISIBLE
		subtitle.text =
			CustomTargetTextStyle(currency, "$text  $currency", GrayScale.black, 8.uiPX(), true, false)
	}

	fun setSubtitle(text: String) {
		subtitle.visibility = View.VISIBLE
		subtitle.text = if (text.length > 36) text.substring(0, 36) + "..." else text
	}
}