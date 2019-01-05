package io.goldstone.blinnnk.common.base.baserecyclerfragment

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.ElementID
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/10/15
 */
class BottomLoadingView(context: Context) : LinearLayout(context) {

	private var description: TextView
	private var loading: ProgressBar

	init {
		orientation = LinearLayout.VERTICAL
		id = ElementID.bottomLoading
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		minimumHeight = 40.uiPX()
		gravity = Gravity.CENTER_HORIZONTAL
		loading = progressBar {
			layoutParams = RelativeLayout.LayoutParams(25.uiPX(), matchParent)
		}
		description = textView {
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.opacity5White
			gravity = Gravity.CENTER
			layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
			text = "there is no new data"
			visibility = View.GONE
		}
		hide()
	}

	fun show() {
		loading.visibility = View.VISIBLE
		description.visibility = View.GONE
	}

	fun hide() {
		loading.visibility = View.GONE
		description.visibility = View.GONE
	}

	fun setGrayDescription() {
		description.textColor = GrayScale.lightGray
	}
}