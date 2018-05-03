package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum

/**
 * @date 21/03/2018 11:00 PM
 * @author KaySaith
 */

class RoundButton(context: Context) : View(context) {

	var text by observing("") {
		invalidate()
	}

	var marginTop = 0

	private val textPaint = Paint()
	private var textSize: Float by observing(0f) {
		textPaint.textSize = textSize
		invalidate()
	}

	init {
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
		canvas?.drawText(text, textX, textY, textPaint)

		canvas?.save()
	}

	fun setWhiteStyle() {
		textSize = 13.uiPX().toFloat()
		layoutParams =
			LinearLayout.LayoutParams(ScreenSize.Width - PaddingSize.device * 2, 45.uiPX()).apply {
				topMargin = marginTop
				leftMargin = PaddingSize.device
			}

		addTouchRippleAnimation(
			Spectrum.white, Spectrum.yellow, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = Spectrum.blue
		invalidate()
	}

	fun setGrayStyle(top: Int? = null) {
		textSize = 14.uiPX().toFloat()
		layoutParams =
			LinearLayout.LayoutParams(ScreenSize.Width - PaddingSize.device * 2, 45.uiPX()).apply {
				topMargin = top ?: marginTop
				leftMargin = PaddingSize.device
			}

		addTouchRippleAnimation(
			GrayScale.lightGray, Spectrum.yellow, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = GrayScale.midGray
		invalidate()
	}

	fun setBlueStyle(top: Int? = null) {
		textSize = 14.uiPX().toFloat()
		layoutParams =
			LinearLayout.LayoutParams(ScreenSize.Width - PaddingSize.device * 2, 45.uiPX()).apply {
				topMargin = top ?: marginTop
				leftMargin = PaddingSize.device
			}

		addTouchRippleAnimation(
			Spectrum.blue, Spectrum.white, RippleMode.Square, layoutParams.height / 2f
		)
		textPaint.color = Spectrum.white
		invalidate()
	}

	fun setSmallButton(color: Int, textColor: Int = Spectrum.white) {
		textSize = 11.uiPX().toFloat()
		layoutParams = RelativeLayout.LayoutParams(75.uiPX(), 30.uiPX()).apply {
			topMargin = marginTop
			leftMargin = PaddingSize.device
		}
		addTouchRippleAnimation(color, Spectrum.white, RippleMode.Square, layoutParams.height / 2f)
		textPaint.color = textColor
		invalidate()
	}

	fun updateColor(color: Int, textColor: Int = Spectrum.white) {
		addTouchRippleAnimation(color, Spectrum.white, RippleMode.Square, layoutParams.height / 2f)
		textPaint.color = textColor
		invalidate()
	}

}