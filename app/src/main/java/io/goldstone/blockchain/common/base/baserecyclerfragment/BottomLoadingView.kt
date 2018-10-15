package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView


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
		layoutParams = RelativeLayout.LayoutParams(matchParent, 40.uiPX())
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
	}

	fun show() {
		loading.visibility = View.VISIBLE
		description.visibility = View.GONE
	}

	fun hide() {
		loading.visibility = View.GONE
		description.visibility = View.VISIBLE
	}
}