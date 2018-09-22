package io.goldstone.blockchain.common.component.button

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
import io.goldstone.blockchain.common.language.CommonText
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
	private val shadowSize = 4f
	private val buttonHeight = 40.uiPX()
	private val textPaint = Paint()
	private var textSize: Float by observing(0f) {
		textPaint.textSize = textSize
		invalidate()
	}

	init {
		setWillNotDraw(false)
		textPaint.isAntiAlias = true
		textPaint.style = Paint.Style.FILL
		textPaint.typeface = GoldStoneFont.black(context)
		elevation = shadowSize
		textSize = 14.uiPX().toFloat()
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
			if (findViewById<ProgressBar>(ElementID.buttonLoading).isNull()) {
				ProgressBar(context, null, android.R.attr.progressBarStyleInverse).apply {
					id = ElementID.buttonLoading
					indeterminateDrawable.setColorFilter(
						color, android.graphics.PorterDuff.Mode.MULTIPLY
					)
					layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())
					setCenterInParent()
				}.into(this)
			}
		} else {
			findViewById<ProgressBar>(ElementID.buttonLoading)?.let {
				removeView(it)
			}
			text = recoveryText
			isEnabled = true
		}
	}

	fun setGrayStyle(top: Int? = null) {
		layoutParams = LinearLayout.LayoutParams(
			ScreenSize.widthWithPadding,
			buttonHeight
		).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			GrayScale.whiteGray,
			Spectrum.green,
			RippleMode.Square,
			CornerSize.normal
		)
		textPaint.color = GrayScale.midGray
		invalidate()
	}

	fun setBlueStyle(top: Int? = null, width: Int = ScreenSize.widthWithPadding, height: Int = buttonHeight) {
		layoutParams = LinearLayout.LayoutParams(width, height).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			Spectrum.blue,
			Spectrum.white,
			RippleMode.Square,
			CornerSize.normal
		)
		textPaint.color = Spectrum.white
		invalidate()
	}

	fun setDarkStyle(top: Int? = null) {
		layoutParams = LinearLayout.LayoutParams(
			ScreenSize.widthWithPadding,
			buttonHeight
		).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			GrayScale.Opacity3Black,
			Spectrum.white,
			RippleMode.Square,
			CornerSize.normal
		)
		textPaint.color = Spectrum.white
		invalidate()
	}

	fun setGreenStyle(top: Int? = null) {
		layoutParams = LinearLayout.LayoutParams(
			ScreenSize.widthWithPadding,
			buttonHeight
		).apply {
			topMargin = top ?: marginTop
			bottomMargin = 5.uiPX()
		}

		addTouchRippleAnimation(
			Spectrum.green,
			Spectrum.yellow,
			RippleMode.Square,
			CornerSize.normal
		)
		textPaint.color = Spectrum.white
		invalidate()
	}
}