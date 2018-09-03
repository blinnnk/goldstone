package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.Utils
import io.goldstone.blockchain.common.component.chart.candle.CandleStickChart
import io.goldstone.blockchain.common.utils.TimeUtils

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description: 详情的蜡烛图
 */
class MarketTokenCandleChart(context: Context) : CandleStickChart(context) {
	private val highLightValueHandler by lazy { Handler() }
	private val highLightValueRunnable by lazy {
		Runnable {
			mIndicesToHighlight = null
			invalidate()
		}
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width - 20.uiPX(), 260.uiPX())
	}
	
	override fun init() {
		super.init()
		
	}
	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		requestDisallowInterceptTouchEvent(true)
		return super.dispatchTouchEvent(ev)
	}

	override fun highlightValue(high: Highlight?, callListener: Boolean) {
		highLightValueHandler.removeCallbacks(highLightValueRunnable)
		super.highlightValue(high, callListener)
		highLightValueHandler.postDelayed(highLightValueRunnable, 1000 * 2)
	}

	override fun formattedDateByType(date: Long): String {
		return when (dateType) {
			DateUtils.FORMAT_SHOW_TIME -> TimeUtils.formatMdHmDate(date)
			else -> TimeUtils.formatMdDate(date)
		}
	}
	private val mOffsetsBuffer = RectF()
	
	override fun calculateOffsets() {
		
		var offsetLeft = 0f
		var offsetRight = 0f
		var offsetTop = 0f
		var offsetBottom = 0f
		
		calculateLegendOffsets(mOffsetsBuffer)
		
		offsetLeft += mOffsetsBuffer.left
		offsetTop += mOffsetsBuffer.top
		offsetRight += mOffsetsBuffer.right
		offsetBottom += mOffsetsBuffer.bottom
		
		// offsets for y-labels
		if (mAxisLeft.needsOffset()) {
			offsetLeft += mAxisLeft.getRequiredWidthSpace(mAxisRendererLeft.paintAxisLabels)
		}
		
		if (mAxisRight.needsOffset()) {
			offsetRight += mAxisRight.getRequiredWidthSpace(mAxisRendererRight.paintAxisLabels)
		}
		
		if (mXAxis.isEnabled && mXAxis.isDrawLabelsEnabled) {
			
			val xlabelheight = mXAxis.mLabelRotatedHeight + mXAxis.yOffset
			
			// offsets for x-labels
			if (mXAxis.position == XAxis.XAxisPosition.BOTTOM) {
				
				offsetBottom += xlabelheight
				
			} else if (mXAxis.position == XAxis.XAxisPosition.TOP) {
				
				offsetTop += xlabelheight
				
			} else if (mXAxis.position == XAxis.XAxisPosition.BOTH_SIDED) {
				
				offsetBottom += xlabelheight
				offsetTop += xlabelheight
			}
		}
		
		offsetTop += extraTopOffset
		offsetRight += extraRightOffset
		offsetBottom += extraBottomOffset
		offsetLeft += extraLeftOffset
		
		val minOffset = 0f
		
		mViewPortHandler.restrainViewPort(Math.max(minOffset, offsetLeft),
			Math.max(minOffset, offsetTop),
			Math.max(minOffset, offsetRight),
			Math.max(Utils.convertDpToPixel(mMinOffset), offsetBottom))
		
		if (mLogEnabled) {
			Log.i(Chart.LOG_TAG,
				"offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom)
			Log.i(Chart.LOG_TAG, "Content: " + mViewPortHandler.contentRect.toString())
		}
		
		
		prepareOffsetMatrix()
		prepareValuePxMatrix()
	}
	
	
	override fun onDraw(canvas: Canvas?) {
		mAxisLeft.xOffset = 0f
		super.onDraw(canvas)
	}
	
}