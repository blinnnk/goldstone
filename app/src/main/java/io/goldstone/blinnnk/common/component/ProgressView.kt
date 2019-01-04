package io.goldstone.blinnnk.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateWidthAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.convertToDiskUnit
import io.goldstone.blinnnk.common.utils.musTimeStampConverter
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*
import java.math.BigInteger


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
		typeface = GoldStoneFont.heavy(context)
		textColor = Spectrum.white
		leftPadding = marginSize
	}
	private val rightValueView = TextView(context).apply {
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		textSize = fontSize(11)
		typeface = GoldStoneFont.heavy(context)
		textColor = Spectrum.white
		rightPadding = marginSize
	}
	private val progressViewHeight = 26.uiPX()

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, progressViewHeight + 45.uiPX())
		subtitle.alignParentRight()
		progressTotalValueView = relativeLayout {
			lparams {
				width = matchParent
				height = progressViewHeight
				alignParentBottom()
			}
			addCorner(CornerSize.small.toInt(), Spectrum.grayBlue)
			progressValueView = relativeLayout {
				lparams(0, matchParent)
				addCorner(CornerSize.small.toInt(), Spectrum.blue)
			}
			leftValueView.into(this)
			leftValueView.centerInVertical()
			rightValueView.into(this)
			rightValueView.centerInVertical()
			rightValueView.alignParentRight()
		}
	}

	fun setTitle(title: String) {
		this.title.text = title
	}

	fun setSubtitle(subtitle: String) {
		this.subtitle.text = subtitle
	}

	fun removeTitles() {
		removeView(title)
		removeView(subtitle)
	}

	private var leftValue = BigInteger.ZERO
	@SuppressLint("SetTextI18n")
	fun setLeftValue(value: BigInteger, description: String, type: ProcessType) {
		leftValue = value
		val convertedValue = when (type) {
			ProcessType.Time -> value.musTimeStampConverter()
			ProcessType.Disk -> value.convertToDiskUnit()
			else -> value.toString()
		}
		leftValueView.text = "$convertedValue $description"
		setProgressValue()
	}

	private var rightValue = BigInteger.ZERO
	@SuppressLint("SetTextI18n")
	fun setRightValue(value: BigInteger, description: String, type: ProcessType) {
		rightValue = value
		val convertedValue = when (type) {
			ProcessType.Time -> value.musTimeStampConverter()
			ProcessType.Disk -> value.convertToDiskUnit()
			else -> value.toString()
		}
		rightValueView.text = "$convertedValue $description"
		setProgressValue()
	}


	private fun setProgressValue() {
		val percent: Double = if (rightValue == BigInteger.ZERO) 0.0 else leftValue.toDouble() / rightValue.toDouble()
		progressValueView.measure(0, 0)
		val width = measuredWidth
		progressValueView.updateWidthAnimation((width * percent).toInt())
	}
}

enum class ProcessType {
	Time, Disk, Value
}