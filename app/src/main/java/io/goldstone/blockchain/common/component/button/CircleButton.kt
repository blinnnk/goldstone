package io.goldstone.blockchain.common.component.button

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.animation.updateOriginYAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.Size
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.topPadding

/**
 * @date 24/03/2018 12:54 AM
 * @author KaySaith
 */

class CircleButton(context: Context) : LinearLayout(context) {

	var title: String by observing("") {
		buttonTitle.text = title
	}

	var src: Int by observing(0) {
		icon.imageResource = src
	}

	private var iconView: RelativeLayout = RelativeLayout(context)

	private val icon = ImageView(context).apply {
		layoutParams = LinearLayout.LayoutParams(matchParent, iconSize)
		scaleType = ImageView.ScaleType.CENTER_INSIDE
	}

	private val buttonTitle = TextView(context).apply {
		layoutParams = LinearLayout.LayoutParams(matchParent, 25.uiPX())
		typeface = GoldStoneFont.medium(context)
		textColor = Spectrum.opacity5White
		gravity = Gravity.CENTER_HORIZONTAL
		y += 4.uiPX()
	}

	private var viewSize = Size(32.uiPX(), 65.uiPX())
	private var iconSize = 30.uiPX()

	private var redDotView: TextView? = null

	fun setRedDotStyle(count: Int) {
		if (redDotView.isNull()) {
			redDotView = TextView(context).apply {
				textSize = fontSize(15)
				typeface = GoldStoneFont.black(context)
				textColor = Spectrum.white
				gravity = Gravity.CENTER
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			}
			redDotView?.into(iconView)
			icon.visibility = View.GONE
		}
		setIconViewColor(Spectrum.lightRed)
		redDotView?.text = "$count"
	}

	fun removeRedDot() {
		if (!redDotView.isNull()) {
			iconView.removeView(redDotView)
			redDotView = null
			icon.visibility = View.VISIBLE
			setIconViewColor(Spectrum.opacity2White)
		}
	}

	private fun setStyleParameter(
		viewSize: Size = this.viewSize,
		iconSize: Int = this.iconSize,
		backgroundColor: Int = Spectrum.opacity2White,
		iconColor: Int = Spectrum.white
	) {
		this.viewSize = viewSize
		this.iconSize = iconSize
		layoutParams = LinearLayout.LayoutParams(48.uiPX(), viewSize.height)
		iconView.layoutParams = LinearLayout.LayoutParams(viewSize.width, viewSize.width)
		setIconViewColor(backgroundColor)
		icon.setColorFilter(iconColor)
		icon.layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
		icon.centerInParent()
	}

	private fun setTitleStyle(
		titleSize: Float = fontSize(8),
		color: Int = Spectrum.white,
		typeFace: Typeface = GoldStoneFont.medium(context)
	) {
		buttonTitle.textSize = titleSize
		buttonTitle.textColor = color
		buttonTitle.typeface = typeFace
	}

	init {
		setStyleParameter()
		orientation = VERTICAL
		gravity = Gravity.CENTER
		// 背景色的 `Layout`
		iconView.into(this)
		// ICON 图形
		icon.into(iconView)
		buttonTitle.into(this)
		setTitleStyle()
	}

	private fun setIconViewColor(color: Int) {
		iconView.addCorner(viewSize.width / 2, color)
	}

	fun setUnTransparent() {
		buttonTitle.updateAlphaAnimation(0f)
		setIconViewColor(Color.TRANSPARENT)
		if (!redDotView.isNull()) {
			setIconViewColor(Spectrum.lightRed)
		}
	}

	fun setDefaultStyle() {
		buttonTitle.updateAlphaAnimation(1f)
		setIconViewColor(Spectrum.opacity2White)
		if (!redDotView.isNull()) {
			setIconViewColor(Spectrum.lightRed)
		}
	}

}