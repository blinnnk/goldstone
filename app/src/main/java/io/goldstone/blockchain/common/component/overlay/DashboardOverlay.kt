package io.goldstone.blockchain.common.component.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.centerInParent
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

@SuppressLint("ViewConstructor")
/**
 * @date 2018/7/12 3:57 PM
 * @author KaySaith
 */
open class DashboardOverlay(
	context: Context,
	hold: LinearLayout.() -> Unit
) : RelativeLayout(context) {

	var confirmEvent: Runnable? = null
	private lateinit var titleView: TextView
	private val confirmButton = RoundButton(context)
	private val container = scrollView {
		addCorner(CornerSize.small.toInt(), Spectrum.white)
		elevation = ShadowSize.Overlay
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		minimumHeight = 200.uiPX()
		verticalLayout {
			titleView = textView {
				visibility = View.GONE
				textSize = fontSize(20)
				textColor = GrayScale.black
				typeface = GoldStoneFont.black(context)
				gravity = Gravity.CENTER_HORIZONTAL
				bottomPadding = 20.uiPX()
				topPadding = 16.uiPX()
			}
			lparams(matchParent, matchParent)
			gravity = Gravity.CENTER_HORIZONTAL
			topPadding = 20.uiPX()
			bottomPadding = 20.uiPX()
			hold(this)
			confirmButton
				.click {
					confirmEvent?.run()
					removeSelf()
				}
				.into(this)
			confirmButton.text = CommonText.confirm
			confirmButton.setBlueStyle(20.uiPX(), ScreenSize.widthWithPadding - 40.uiPX())
		}
	}

	init {
		id = ElementID.dashboardOverlay
		backgroundColor = GrayScale.Opacity5Black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		container.centerInParent()
		isClickable = true
		onClick {
			removeSelf()
		}
	}

	fun showTitle(text: String): DashboardOverlay {
		titleView.visibility = View.VISIBLE
		titleView.text = text
		return this
	}

	private fun removeSelf() {
		(parent as? ViewGroup)?.removeView(this)
	}
}