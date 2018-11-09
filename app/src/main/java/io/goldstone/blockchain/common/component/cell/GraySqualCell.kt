package io.goldstone.blockchain.common.component.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 25/04/2018 8:56 AM
 * @author KaySaith
 */
open class GraySquareCell(context: Context) : GSCard(context) {

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

	private var container: RelativeLayout

	init {
		this.setCardBackgroundColor(GrayScale.whiteGray)
		// 低端机型导致的 SetMargin 不识别, 顾此采用夹层方式实现 `Margin` 的阴影
		container = relativeLayout {
			lparams(matchParent, matchParent)
			addView(
				View(context).apply {
					layoutParams = RelativeLayout.LayoutParams(6.uiPX(), matchParent)
					backgroundColor = GrayScale.midGray
				}
			)
			addView(title)
			addView(subtitle)
		}
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, 52.uiPX())
	}

	fun <T : CharSequence> setTitle(text: T) {
		title.text = text
	}

	fun showArrow() {
		arrow.into(container)
		arrow.setAlignParentRight()
		subtitle.x -= 20.uiPX()
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

	fun setPriceSubtitle(text: String, currency: String) {
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