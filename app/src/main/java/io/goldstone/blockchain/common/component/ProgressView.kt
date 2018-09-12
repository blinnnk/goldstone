package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateWidthAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class ProgressView(context: Context) : RelativeLayout(context) {
	private val marginSize = 15.uiPX()
	private val title = textView {
		textSize = fontSize(12)
		textColor = GrayScale.black
		typeface = GoldStoneFont.black(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		setPadding(5.uiPX(), marginSize, 0, 10.uiPX())
	}
	private val subtitle = textView {
		textSize = fontSize(12)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		setPadding(0, marginSize, 5.uiPX(), 10.uiPX())
	}
	private var progressTotalValueView: RelativeLayout
	private lateinit var progressValueView: RelativeLayout

	private val leftValueView = TextView(context).apply {
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		textSize = fontSize(11)
		typeface = GoldStoneFont.black(context)
		textColor = Spectrum.white
		leftPadding = marginSize
	}
	private val rightValueView = TextView(context).apply {
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		textSize = fontSize(11)
		typeface = GoldStoneFont.black(context)
		textColor = Spectrum.white
		rightPadding = marginSize
	}
	private val progressViewHeight = 26.uiPX()

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, progressViewHeight + 45.uiPX())
		leftPadding = marginSize
		rightPadding = marginSize
		subtitle.setAlignParentRight()
		progressTotalValueView = relativeLayout {
			lparams {
				width = matchParent
				height = progressViewHeight
				alignParentBottom()
			}
			addCorner(progressViewHeight, Spectrum.grayBlue)
			progressValueView = relativeLayout {
				lparams(0, matchParent)
				addCorner(progressViewHeight, Spectrum.blue)
			}
			leftValueView.into(this)
			leftValueView.setCenterInVertical()
			rightValueView.into(this)
			rightValueView.setCenterInVertical()
			rightValueView.setAlignParentRight()
		}
	}

	fun setTitle(title: String) {
		this.title.text = title
	}

	fun setSubtitle(subtitle: String) {
		this.subtitle.text = subtitle
	}

	private var leftValue = 0
	@SuppressLint("SetTextI18n")
	fun setLeftValue(value: Int, description: String) {
		leftValue = value
		leftValueView.text = "$value $description"
		setProgressValue()
	}

	private var rightValue = 0
	@SuppressLint("SetTextI18n")
	fun setRightValue(value: Int, description: String) {
		rightValue = value
		rightValueView.text = "$value $description"
		setProgressValue()
	}

	private fun setProgressValue() {
		val percent: Float = if (rightValue == 0) 0f else leftValue / rightValue.toFloat()
		measure(0, 0)
		val width = measuredWidth * 1.uiPX() - marginSize * 4
		progressValueView.updateWidthAnimation((width * percent).toInt())
	}
}