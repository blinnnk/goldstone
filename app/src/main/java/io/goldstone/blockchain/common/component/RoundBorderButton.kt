package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.Spectrum

/**
 * @date 23/03/2018 10:31 PM
 * @author KaySaith
 */
open class RoundBorderButton(context: Context) : View(context) {
	
	var text by observing("") {
		invalidate()
	}
	var themeColor: Int by observing(Spectrum.white) {
		paint.color = themeColor
		textPaint.color = themeColor
		invalidate()
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
	}
	private val titleSize = 11.uiPX().toFloat()
	private val textPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		textSize = titleSize
		typeface = GoldStoneFont.heavy(context)
	}
	
	open fun setBorderWidth(width: Float = BorderSize.default) {
		paint.strokeWidth = width
	}
	
	init {
		this.setBorderWidth()
	}
	
	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val rectF = RectF(
			BorderSize.default,
			BorderSize.default,
			width - BorderSize.default,
			height - BorderSize.default
		)
		
		canvas?.drawRoundRect(rectF, height / 2f, height / 2f, paint)
		val textX = (width - textPaint.measureText(text)) / 2f
		val textY = (height + titleSize) / 2f - 2.uiPX()
		canvas?.drawText(text, textX, textY, textPaint)
	}
	
	fun setAdjustWidth() {
		// 有些地方需要做自适应宽度的 `Button` 需要用这个工具来测量
		layoutParams.width = textPaint.measureText(text).toInt() + 35.uiPX()
	}
}