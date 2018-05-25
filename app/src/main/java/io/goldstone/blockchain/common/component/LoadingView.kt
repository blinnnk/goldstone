package io.goldstone.blockchain.common.component

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date 07/04/2018 12:29 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class LoadingView(context: Context) : RelativeLayout(context) {

	private val introView by lazy { TextView(context) }

	init {
		id = ElementID.loadingView
		isClickable = true

		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)

		updateColorAnimation(GrayScale.Opacity1Black, GrayScale.Opacity5Black)

		val size = (ScreenSize.Width * 0.7).toInt()

		relativeLayout {

			addCorner(CornerSize.default.toInt(), Spectrum.white)

			addLoadingCircle(this) {
				setCenterInParent()
				y -= 30.uiPX()
			}

			introView.apply {
				textSize = fontSize(13)
				textColor = GrayScale.gray
				gravity = Gravity.CENTER_HORIZONTAL
				leftPadding = 30.uiPX()
				rightPadding = 30.uiPX()
				lparams {
					width = matchParent
					height = 50.uiPX()
					centerInParent()
					y += 50.uiPX()
				}
			}.into(this)

			lparams {
				centerInParent()
				width = size
				height = size
			}
		}

		setIntroText("obtaining token information from ethereum now just wait a moment")
	}

	private fun setIntroText(intro: String) {
		introView.text = intro
	}

	companion object {
		fun addLoadingCircle(parent: ViewGroup, size: Int = 80.uiPX(), color: Int = HoneyColor.Red,getCircle: ProgressBar.() -> Unit) {
			val loading = ProgressBar(
				parent.context,
				null,
				R.attr.progressBarStyleInverse
			).apply {
				indeterminateDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
				layoutParams = RelativeLayout.LayoutParams(size, size)
				getCircle(this)
			}
			parent.addView(loading)
		}
	}

}