package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.R
import android.content.Context
import android.view.Gravity
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.measureTextWidth
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 2018/5/13 11:37 AM
 * @author KaySaith
 */
class RecyclerLoadingView(context: Context) : RelativeLayout(context) {
	
	private val progressBar = ProgressBar(
		this.context, null, R.attr.progressBarStyleInverse
	).apply {
		indeterminateDrawable.setColorFilter(
			Spectrum.white, android.graphics.PorterDuff.Mode.MULTIPLY
		)
		layoutParams = RelativeLayout.LayoutParams(16.uiPX(), 16.uiPX())
		setCenterInVertical()
	}
	private var loadingText: TextView
	
	init {
		progressBar.into(this)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 45.uiPX())
		backgroundColor = Spectrum.green
		
		loadingText = textView {
			x += 16.uiPX()
			textSize = fontSize(12)
			textColor = Spectrum.white
			layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
			gravity = Gravity.CENTER
		}
		loadingText.setCenterInParent()
	}
	
	fun setTextContent(content: String) {
		loadingText.text = content
		progressBar.x = (ScreenSize.Width - loadingText.text.measureTextWidth(14.uiPX().toFloat())) / 2f
	}
}