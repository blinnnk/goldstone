package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 2018/7/11 1:20 AM
 * @author KaySaith
 */
open class GraySqualCellWithButtons(context: Context) : RelativeLayout(context) {
	
	private val cellHeight = 45.uiPX()
	protected val title = TextView(context).apply {
		textSize = fontSize(12)
		typeface = GoldStoneFont.black(context)
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
		gravity = Gravity.CENTER_VERTICAL
	}
	val copyButton by lazy {
		ImageView(context).apply {
			imageResource = R.drawable.copy_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.midGray)
			layoutParams = RelativeLayout.LayoutParams(cellHeight, matchParent)
			addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
		}
	}
	val moreButton by lazy {
		ImageView(context).apply {
			imageResource = R.drawable.more_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.midGray)
			layoutParams = RelativeLayout.LayoutParams(cellHeight, matchParent)
			addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
		}
	}
	
	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, cellHeight)
		setMargins<RelativeLayout.LayoutParams> {
			bottomMargin = 5.uiPX()
		}
		backgroundColor = GrayScale.whiteGray
		this.addView(title)
		this.addView(subtitle)
		subtitle.leftPadding = 40.uiPX()
		this.addView(copyButton)
		copyButton.setAlignParentRight()
		copyButton.x -= 30.uiPX()
		this.addView(moreButton)
		moreButton.setAlignParentRight()
	}
	
	fun <T : CharSequence> setTitle(text: T) {
		title.text = text
	}
	
	fun setTitle(text: String) {
		title.text = text
	}
	
	fun setSubtitle(text: String) {
		subtitle.visibility = View.VISIBLE
		subtitle.text = if (text.length > 36) text.substring(0, 36) + "..." else text
	}
}