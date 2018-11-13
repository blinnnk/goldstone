package io.goldstone.blockchain.common.component.button

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

/**
 * @date 21/03/2018 11:00 PM
 * @author KaySaith
 */
class RoundButton(context: Context) : GSCard(context) {

	var text: String by observing("") {
		title.text = text
	}
	var marginTop = 0
	private val buttonHeight = 50.uiPX()

	private lateinit var title: TextView
	private lateinit var loadingView: ProgressBar

	private var container: RelativeLayout = relativeLayout {
		layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
		title = textView {
			gravity = Gravity.CENTER
			textColor = Spectrum.white
			layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
			textSize = fontSize(12)
			typeface = GoldStoneFont.black(context)
		}
		loadingView = ProgressBar(context, null, android.R.attr.progressBarStyleInverse).apply {
			visibility = View.GONE
			id = ElementID.buttonLoading
			indeterminateDrawable.setColorFilter(
				Spectrum.white, android.graphics.PorterDuff.Mode.MULTIPLY
			)
			layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())
		}
		loadingView.into(this)
		loadingView.setCenterInParent()
	}

	fun showLoadingStatus(
		needToShow: Boolean = true,
		color: Int = Spectrum.white,
		recoveryText: String = CommonText.confirm
	) {
		if (needToShow) {
			isEnabled = false
			text = ""
			loadingView.indeterminateDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
			loadingView.visibility = View.VISIBLE
		} else {
			loadingView.visibility = View.GONE
			text = recoveryText
			isEnabled = true
		}
	}

	fun setGrayStyle(top: Int? = null) {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.card, buttonHeight).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}
		title.textColor = GrayScale.midGray
		setCardBackgroundColor(GrayScale.whiteGray)
	}

	fun setBlueStyle(top: Int? = null, width: Int = ScreenSize.card, height: Int = buttonHeight) {
		layoutParams = LinearLayout.LayoutParams(width, height).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}
		setCardBackgroundColor(Spectrum.blue)
	}
	
	fun setRedStyle(top: Int? = null, width: Int = ScreenSize.card, height: Int = buttonHeight) {
		layoutParams = LinearLayout.LayoutParams(width, height).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}
		setCardBackgroundColor(Spectrum.red)
	}
	
	fun setDarkStyle(top: Int? = null) {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.card, buttonHeight).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}
		setCardBackgroundColor(Spectrum.blackBlue)
	}

	fun setGreenStyle(top: Int? = null) {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.card, buttonHeight).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		setCardBackgroundColor(Spectrum.green)
	}
}