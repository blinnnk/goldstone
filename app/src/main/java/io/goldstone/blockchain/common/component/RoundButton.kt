package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.isNull
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*

/**
 * @date 21/03/2018 11:00 PM
 * @author KaySaith
 */

class RoundButton(context: Context) : RelativeLayout(context) {

	var text by observing("") {
		invalidate()
	}

	var marginTop = 0

	private val buttonHeight = 45.uiPX()

	private val textPaint = Paint()
	private var textSize: Float by observing(0f) {
		textPaint.textSize = textSize
		invalidate()
	}

	private var loadingView: ProgressBar? = null

	init {
		setWillNotDraw(false)
		textPaint.isAntiAlias = true
		textPaint.style = Paint.Style.FILL
		textPaint.typeface = GoldStoneFont.heavy(context)

		// 视觉垂直居中的微调
		y += 2.uiPX()

	}

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		val textX = (width - textPaint.measureText(text)) / 2
		val textY = (height + textSize) / 2 - 2.uiPX()
		canvas?.drawText(
			text, textX, textY, textPaint
		)

		canvas?.save()
	}

	fun showLoadingStatus(
		needToShow: Boolean = true,
		color: Int = Spectrum.white,
		recoveryText: String = CommonText.confirm
	) {
		if (needToShow && loadingView.isNull()) {
			isEnabled = false
			text = ""
			loadingView = ProgressBar(
				this.context, null, android.R.attr.progressBarStyleInverse
			).apply {
				indeterminateDrawable.setColorFilter(
					color, android.graphics.PorterDuff.Mode.MULTIPLY
				)
				layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
				setCenterInParent()
			}
			addView(loadingView)
		}
		if (!needToShow && !loadingView.isNull()) {
			removeView(loadingView)
			loadingView = null
			text = recoveryText
			isEnabled = true
		}
	}

	fun setWhiteStyle() {
		textSize = fontSize(42)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, buttonHeight).apply {
			topMargin = marginTop
		}

		addTouchRippleAnimation(
			Spectrum.white, Spectrum.yellow, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = Spectrum.blue
		invalidate()
	}

	fun setGrayStyle(top: Int? = null) {
		textSize = fontSize(42)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, buttonHeight).apply {
			topMargin = top ?: marginTop
		}

		addTouchRippleAnimation(
			GrayScale.lightGray, Spectrum.yellow, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = GrayScale.midGray
		invalidate()
	}

	fun setBlueStyle(top: Int? = null) {
		textSize = fontSize(42)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, buttonHeight).apply {
			topMargin = top ?: marginTop
		}

		addTouchRippleAnimation(
			Spectrum.blue, Spectrum.white, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = Spectrum.white
		invalidate()
	}

	fun setSmallButton(
		color: Int,
		textColor: Int = Spectrum.white
	) {
		textSize = fontSize(33)
		layoutParams = RelativeLayout.LayoutParams(75.uiPX(), 30.uiPX()).apply {
			topMargin = marginTop
		}
		addTouchRippleAnimation(color, Spectrum.white, RippleMode.Square, layoutParams.height / 2f)
		textPaint.color = textColor
		invalidate()
	}

	fun updateColor(
		color: Int,
		textColor: Int = Spectrum.white
	) {
		addTouchRippleAnimation(
			color, Spectrum.white, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = textColor
		invalidate()
	}

}