package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
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
	private val shadowSize = 1.uiPX().toFloat()
	private val buttonHeight = 45.uiPX()

	private val textPaint = Paint()
	private var textSize: Float by observing(0f) {
		textPaint.textSize = textSize
		invalidate()
	}

	init {
		setWillNotDraw(false)
		textPaint.isAntiAlias = true
		textPaint.style = Paint.Style.FILL
		textPaint.typeface = GoldStoneFont.heavy(context)
		elevation = shadowSize
	}

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val textX = (width - textPaint.measureText(text)) / 2
		val textY = (height + textSize) / 2 - 2.uiPX()
		canvas?.drawText(text, textX, textY, textPaint)
		canvas?.save()
	}

	fun showLoadingStatus(
		needToShow: Boolean = true,
		color: Int = Spectrum.white,
		recoveryText: String = CommonText.confirm
	) {
		if (needToShow) {
			isEnabled = false
			text = ""
			try {
				if (findViewById<ProgressBar>(ElementID.buttonLoading).isNull()) {
					ProgressBar(
						context, null, android.R.attr.progressBarStyleInverse
					).apply {
						id = ElementID.buttonLoading
						indeterminateDrawable.setColorFilter(
							color, android.graphics.PorterDuff.Mode.MULTIPLY
						)
						layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
						setCenterInParent()
					}.into(this)
				}
			} catch (error: Exception) {
				LogUtil.error("position: RoundButton error: $error")
			}
		}
		if (!needToShow) {
			try {
				findViewById<ProgressBar>(ElementID.buttonLoading)?.let {
					removeView(it)
				}
			} catch (error: Exception) {
				LogUtil.error("position: RoundButton error: $error")
			}
			text = recoveryText
			isEnabled = true
		}
	}

	fun setWhiteStyle() {
		textSize = 14.uiPX().toFloat()
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, buttonHeight).apply {
			topMargin = marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			Spectrum.white, Spectrum.yellow, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = Spectrum.blue
		invalidate()
	}

	fun setGrayStyle(top: Int? = null) {
		textSize = 14.uiPX().toFloat()
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, buttonHeight).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			GrayScale.whiteGray, Spectrum.green, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = GrayScale.midGray
		invalidate()
	}

	fun setBlueStyle(top: Int? = null) {
		textSize = 14.uiPX().toFloat()
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, buttonHeight).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			Spectrum.blue, Spectrum.white, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = Spectrum.white
		invalidate()
	}

}