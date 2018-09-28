package io.goldstone.blockchain.common.component.cell

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.into
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
class RoundCell(context: Context) : LinearLayout(context) {
	
	private val cellHeight = 40.uiPX()
	private val titleView = TextView(context).apply {
		textSize = fontSize(14)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		gravity = Gravity.CENTER_VERTICAL
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
		leftPadding = 25.uiPX()
	}
	private val arrowIcon = ImageView(context).apply {
		scaleX = 0.75f
		scaleY = 0.75f
		setColorFilter(GrayScale.lightGray)
		imageResource = R.drawable.arrow_icon
		layoutParams = RelativeLayout.LayoutParams(cellHeight, cellHeight)
	}
	private val subtitleView = TextView(context).apply {
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		gravity = Gravity.CENTER_VERTICAL or Gravity.END
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
	}
	var container: RelativeLayout
	
	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		addTouchRippleAnimation(
			GrayScale.whiteGray,
			Spectrum.green,
			RippleMode.Square,
			CornerSize.normal
		)
		container = relativeLayout {
			lparams(matchParent, matchParent)
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, cellHeight)
			subtitleView.into(this)
			titleView.into(this)
			arrowIcon.into(this)
			arrowIcon.setAlignParentRight()
			subtitleView.setAlignParentRight()
			subtitleView.x -= 40.uiPX()
		}
	}
	
	fun setTitles(title: String, subTitle: String) {
		titleView.text = title
		subtitleView.text = subTitle
	}
}