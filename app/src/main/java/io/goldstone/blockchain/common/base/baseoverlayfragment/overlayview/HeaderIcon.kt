package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.alignParentLeft
import org.jetbrains.anko.alignParentRight


/**
 * @author KaySaith
 * @date  2018/11/09
 */
class HeaderIcon(context: Context) : ImageView(context) {

	private val iconSize = 30.uiPX()

	init {
		setColorFilter(Spectrum.white)
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.blue, RippleMode.Round)
	}

	fun setLeftPosition() {
		layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
			topMargin = 18.uiPX()
			leftMargin = 15.uiPX()
			alignParentLeft()
		}
	}

	fun setRightPosition() {
		layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
			topMargin = 18.uiPX()
			rightMargin = 15.uiPX()
			alignParentRight()
		}
	}
}