package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX

/**
 * @date: 2018/9/26.
 * @author: yanglihai
 * @description:
 */
class RAMPercentChartView(context: Context) : View(context) {
	
	private val maxBodyHeight = 100.uiPX()
	
	private val itemWidth = ScreenSize.Width.toFloat() / 2 / 5
	
	private val itemSpace = itemWidth * 2 / 4
	
	private val valueTextSize = 39
	
	private val bodyPaint = Paint()
	
	private val textPaint = Paint().apply {
		color = Color.BLACK
		textSize = valueTextSize.toFloat()
	}
	
	private var maxValue = 0f
	
	private var bodyColors = arrayOf(
		Color.parseColor("#FF4500"),
		Color.parseColor("#FF3E96"),
		Color.parseColor("#FF00FF"))
	
	private var values = arrayOf(0f, 0f, 0f)
	
	init {
		layoutParams = ViewGroup.LayoutParams(
			ScreenSize.Width / 2,
			maxBodyHeight + valueTextSize * 2)
	}
	
	
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		
		values.forEachIndexed { index, value ->
			val text = value.toInt().toString() + "kb"
			val textRect = Rect()
			textPaint.getTextBounds(text, 0, text.length, textRect)
			bodyPaint.color = bodyColors[index]
			val bodyLeft = itemWidth * index + itemSpace * (index + 1)
			val bodyTop = ((maxValue - value) / maxValue) * maxBodyHeight
			canvas.drawRect(
				bodyLeft,
				bodyTop + textRect.height(),
				(itemWidth + itemSpace) * (index + 1).toFloat(),
				maxBodyHeight.toFloat() + textRect.height(),
				bodyPaint)
			canvas.drawText(
				text,
				bodyLeft + (itemWidth - textRect.width()) / 2,
				bodyTop + textRect.height(),
				textPaint)
			
			val fontMetrics = textPaint.fontMetrics
			
			canvas.drawText(
				"打单",
				bodyLeft + (itemWidth - textRect.width()) / 2,
				bottom - fontMetrics.bottom,
				textPaint)
		}
		
	}
	
	
	
	fun setDataAndColors(values: Array<Float>, colors: Array<Int>?, maxValue: Float) {
		this.values = values
		colors?.apply {
			bodyColors = this
		}
		this.maxValue = maxValue
		invalidate()
	}

}