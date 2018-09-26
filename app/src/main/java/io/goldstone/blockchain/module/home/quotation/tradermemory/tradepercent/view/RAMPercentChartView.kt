package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view

import android.content.Context
import android.graphics.*
import android.view.View
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX

/**
 * @date: 2018/9/26.
 * @author: yanglihai
 * @description:
 */
class RAMPercentChartView(context: Context) : View(context) {
	
	private val maxHeight = 100.uiPX()
	
	private val itemWidth = ScreenSize.Width / 2 / 5
	
	private val itemSpace = itemWidth * 2 / 4
	
	private val bodyPaint = Paint()
	
	private var maxValue = 0f
	
	private var bodyColors = arrayOf(
		Color.parseColor("#FF4500"),
		Color.parseColor("#FF3E96"),
		Color.parseColor("#FF00FF"))
	
	private var values = arrayOf(0f, 0f, 0f)
	
	init {
	
	}
	
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		
		for (index in 0 .. 2) {
			bodyPaint.color = bodyColors[index]
			canvas.drawRect((itemWidth + itemSpace) * index.toFloat(),
				(maxValue - values[index] / maxValue) * maxHeight,
				(itemWidth + itemSpace) * (index + 1).toFloat(),
				maxHeight.toFloat(),
				bodyPaint)
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