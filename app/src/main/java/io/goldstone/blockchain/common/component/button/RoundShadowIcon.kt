package io.goldstone.blockchain.common.component.button

import android.R
import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.centerInParent
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.value.ShadowSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.imageResource


/**
 * @author KaySaith
 * @date  2018/11/07
 */
class RoundShadowIcon(context: Context) : RelativeLayout(context) {

	var iconColor: Int by observing(Color.BLACK) {
		addCorner(iconSize, iconColor, false)
	}
	var src: Int by observing(0) {
		icon.imageResource = src
		icon.setColorFilter(Spectrum.white)
	}

	private var iconSize = 60.uiPX()

	private var pendingIcon: ProgressBar? = null
	private val icon = ImageView(context)

	init {
		layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
		icon.scaleType = ImageView.ScaleType.CENTER_INSIDE
		icon.layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
		icon.z = 1f
		addView(icon)
		elevation = ShadowSize.default

	}

	fun setColorFilter(color: Int) {
		icon.setColorFilter(color)
	}

	fun showPendingIcon(status: Boolean = true) {
		if (pendingIcon != null) {
			if (!status) removeView(pendingIcon)
		} else {
			if (status) {
				pendingIcon =
					ProgressBar(this.context, null, R.attr.progressBarStyleInverse).apply {
						indeterminateDrawable.setColorFilter(
							HoneyColor.HoneyWhite,
							android.graphics.PorterDuff.Mode.MULTIPLY
						)
						RelativeLayout.LayoutParams(32.uiPX(), 32.uiPX())
					}
				addView(pendingIcon)
				pendingIcon?.centerInParent()
				pendingIcon?.z = 2f
			}
		}
	}
}