package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.graphics.*
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.AxisRenderer
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.*

/**
 * @date: 2018/9/3.
 * @author: yanglihai
 * @description:
 */
open class MarketCandleYaxisRenderer(
	viewPortHandler: ViewPortHandler?,
	yAxis: YAxis?,
	trans: Transformer?
) : YAxisRenderer(viewPortHandler, yAxis, trans) {
	override fun drawYLabels(
		canvas: Canvas,
		fixedPosition: Float,
		positions: FloatArray,
		offset: Float
	) {
		val from = if (mYAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
		val to = if (mYAxis.isDrawTopYLabelEntryEnabled) mYAxis.mEntryCount
		else mYAxis.mEntryCount - 1
		
		// draw
		for (i in from until to) {
			
			val text = mYAxis.getFormattedLabel(i)
			
			canvas.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint)
		}
	}
	
	/**
	 * Transforms the values contained in the axis entries to screen pixels and returns them in form of a float array
	 * of x- and y-coordinates.
	 *
	 * @return
	 */
	override fun getTransformedPositions(): FloatArray {
		
		if (mYAxis.mEntries.lastIndex > 2) {
			var distance = mYAxis.mEntries[1] - mYAxis.mEntries[0]
			mYAxis.mEntries[0] = mYAxis.mEntries[0] + distance * 0.1f
			mYAxis.mEntries[mYAxis.mEntries.lastIndex] = mYAxis.mEntries[mYAxis.mEntries.lastIndex] - distance * 0.1f
		}
		
		return super.getTransformedPositions()
	}
}
