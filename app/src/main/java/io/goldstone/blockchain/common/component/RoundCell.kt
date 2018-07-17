package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

/**
 * @date 2018/7/12 2:29 PM
 * @author KaySaith
 */
class RoundCell(context: Context) : RelativeLayout(context) {
	
	private val cellHeight = 40.uiPX()
	private val titleView = textView {
		textSize = fontSize(14)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		gravity = Gravity.CENTER_VERTICAL
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
		leftPadding = 25.uiPX()
	}
	private val arrowIcon = imageView {
		scaleX = 0.75f
		scaleY = 0.75f
		setColorFilter(GrayScale.lightGray)
		imageResource = R.drawable.arrow_icon
		layoutParams = RelativeLayout.LayoutParams(cellHeight, cellHeight)
	}
	private val subtitleView = textView {
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		gravity = Gravity.CENTER_VERTICAL or Gravity.END
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
	}
	
	init {
		addTouchRippleAnimation(
			GrayScale.whiteGray,
			Spectrum.green,
			RippleMode.Square,
			40.uiPX().toFloat()
		)
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, cellHeight)
		arrowIcon.setAlignParentRight()
		subtitleView.setAlignParentRight()
		subtitleView.x -= 40.uiPX()
	}
	
	fun setTitles(title: String, subTitle: String) {
		titleView.text = title
		subtitleView.text = subTitle
	}
}