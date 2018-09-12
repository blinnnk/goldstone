package io.goldstone.blockchain.common.component.chart.candle

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.text.format.DateUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.listener.BarLineChartTouchListener
import com.github.mikephil.charting.listener.ChartTouchListener
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.math.BigDecimal

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
abstract class CandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
	
	private var xRangeVisibleCount = 30
	private var leftLabelCount = 10
	private var realData = listOf<CandleEntry>()
	private var labelTextSize = fontSize(8)
	protected var dateType = DateUtils.FORMAT_SHOW_TIME
	private var lastStartIndex = 0 // 第一个蜡烛的角标
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(
		context: Context,
		attrs: AttributeSet,
		defStyle: Int
	) : super(context, attrs, defStyle)
	
	override fun init() {
		super.init()
		mXAxisRenderer = XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer)
		mRenderer = CandleStickChartRenderer(this, mAnimator, mViewPortHandler)
		resetAxisStyle()
		post { setEmptyData() }
	}
	
	open fun resetData(dateType: Int, dataRows: List<CandleEntry>) {
		lastStartIndex = 0
		calculateHandler.removeCallbacksAndMessages(null)
		// data重新赋值后，最高值最低值需要动态计算，不然会导致蜡烛显示在错误，所以要释放最大值最小值的强制设置
		mAxisLeft.resetAxisMaximum()
		notifyData(dateType, dataRows)
		postDelayed( {
			performDrag(-4000f) // 滑动平移至右侧，
			calculateHandler.postDelayed( {
				postCalculate()
				calculateHandler.postDelayed(disCalculateRunnable, 1000)
			}, 200)
		}, 200)
		
		
	}
	
	private fun notifyData(dateType: Int, dataRows: List<CandleEntry>) {
		this.dateType = dateType
		if (realData != dataRows) {
			realData = dataRows
			resetFormatLimit()
		}
		isScaleXEnabled = false
		isScaleYEnabled = false
		val candleDecreasingColor = Spectrum.lightRed
		val candleIncreasingColor = Spectrum.green
		val candleNeutralColor = GrayScale.lightGray
		val candleShadowWidth = 1f // 蜡烛柄宽度
		resetTracking()
		clear()
		xRangeVisibleCount = if (dataRows.size < 30) dataRows.size else 30
		val ratio = when (dateType) {
			DateUtils.FORMAT_SHOW_TIME -> 5
			else -> 3
		}
		mXAxis.labelCount =
			if (dataRows.size > xRangeVisibleCount) xRangeVisibleCount / ratio
			else dataRows.size / ratio
		when (dateType) {
			DateUtils.FORMAT_SHOW_TIME -> if (dataRows.size < 5) {
				mXAxis.labelCount = dataRows.size
			}
			else -> if (dataRows.size < 10) mXAxis.labelCount = dataRows.size
		}
		val dataSet = CandleDataSet(dataRows, "Candle Set")
		dataSet.apply {
			setDrawIcons(false)
			shadowWidth = candleShadowWidth
			decreasingColor = candleDecreasingColor
			increasingColor = candleIncreasingColor
			increasingPaintStyle = Paint.Style.FILL
			neutralColor = candleNeutralColor
			setDrawValues(false)
			shadowColorSameAsCandle = true
		}
		val data = CandleData(dataSet)
		setData(data)
		setVisibleXRangeMaximum(xRangeVisibleCount.toFloat())
		setVisibleXRangeMinimum(xRangeVisibleCount.toFloat())
		invalidate()
		calculateHandler.removeCallbacks(calculateRunnable)
		if (needCalculate) {
			calculateHandler.postDelayed(calculateRunnable, 15)
		}
	}
	
	/**
	 * 模拟手势滑动，滑动到最右侧
	 */
	private fun performDrag(distanceX: Float) {
		(mChartTouchListener as? BarLineChartTouchListener)?.apply {
			stopDeceleration() // 停止computescroll，防止下面的矩阵计算冲突
			var touchMode = ChartTouchListener::class.java.getDeclaredField("mTouchMode")
			touchMode.isAccessible = true
			touchMode.set(this, 1)
			
			var saveMatrix = BarLineChartTouchListener::class.java.getDeclaredField("mSavedMatrix")
			saveMatrix.isAccessible = true
			(saveMatrix.get(this) as? Matrix)?.apply { reset() }
			
			val event = MotionEvent.obtain(System.currentTimeMillis(),
				System.currentTimeMillis(),
				MotionEvent.ACTION_MOVE,
				distanceX,
				0f,
				0)
			onTouch(this@CandleStickChart, event)
			event.recycle()
		}
	}
	
	private fun resetAxisStyle() {
		// 初始化一些属性
		val labelColor = GrayScale.midGray
		val gridLineColor = GrayScale.lightGray
		val xAxisSpace = 1f // 蜡烛图内部的左右 `Offset` 值
		// 为了防止方法执行到此，以下数据还没有被初始化，所以在这里重新赋值
		xRangeVisibleCount = 30
		minOffset = 0.5f
		labelTextSize = fontSize(10)
		isScaleXEnabled = false
		isScaleYEnabled = false
		legend.isEnabled = false
		description.isEnabled = false
		with(xAxis) {
			valueFormatter = IAxisValueFormatter { value, _ ->
				val valueData = (data.getDataSetByIndex(0) as CandleDataSet).values
				val position = if (value.toInt() > valueData.lastIndex) valueData.lastIndex else value.toInt()
				val entry = try {
					valueData[position]
				} catch (error: Exception) {
					LogUtil.error("resetAxisStyle", error)
					return@IAxisValueFormatter ""
				}
				when {
					position > valueData.lastIndex -> ""
					!entry.toString().toLongOrNull().isNull() -> formattedDateByType(entry.toString().toLong())
					else -> formattedDateByType(entry.data.toString().toLong())
				}
			}
			textColor = labelColor
			position = XAxis.XAxisPosition.BOTTOM
			gridColor = gridLineColor
			axisLineColor = gridLineColor
			spaceMin = xAxisSpace
			spaceMax = xAxisSpace
			textSize = labelTextSize
			typeface = GoldStoneFont.heavy(context)
		}
		
		with(axisLeft) {
			gridColor = gridLineColor
			axisLineColor = gridLineColor
			textColor = labelColor
			setLabelCount(leftLabelCount, true)
			textSize = labelTextSize
			typeface = GoldStoneFont.heavy(context)
		}
		with(axisRight) {
			setDrawLabels(false)
			setDrawGridLines(false)
			axisLineColor = gridLineColor
			textColor = labelColor
		}
		marker = object : CandleMarkerView(context) {
			override fun getChartWidth(): Int {
				return this@CandleStickChart.width
			}
			
			override fun getChartHeight(): Int {
				return this@CandleStickChart.height
			}
		}
	}
	
	override fun getCandleData(): CandleData {
		return mData
	}
	
	private fun setEmptyData() {
		if (realData.isEmpty()) {
			val candleEntrySet = arrayListOf<CandleEntry>()
			(0 until xRangeVisibleCount).forEach { index ->
				candleEntrySet.add(
					CandleEntry(
						index.toFloat(),
						0f,
						0f,
						0f,
						0f,
						System.currentTimeMillis()
					)
				)
			}
			resetData(dateType, candleEntrySet)
		}
	}
	
	/**
	 * @date: 2018/8/22
	 * @author: yanglihai
	 * @description: 计算显示在屏幕上蜡烛的最高值和最低值
	 */
	private fun resetMaxMin(firstVisibleIndex: Int) {
		val endIndex: Int = firstVisibleIndex + xRangeVisibleCount
		val max = if (endIndex > realData.size) realData.size else endIndex
		val targetDataRange = realData.subList(firstVisibleIndex, max)
		val high = targetDataRange.maxBy { it.high }?.high.orZero()
		val low = targetDataRange.minBy { it.low }?.low.orZero()
		val distance = (high - low) / 20 // 距离上下的间距
		context.runOnUiThread {
			if (high * low != 0f) {
				with(axisLeft) {
					axisMinimum = low - distance
					axisMaximum = high + distance
					setLabelCount(leftLabelCount, true)
					textSize = labelTextSize
				}
			}
			mXAxis.textSize = labelTextSize
			notifyData(dateType, realData)
		}
	}
	
	private fun resetFormatLimit() {
		val valueString = BigDecimal(realData[0].high.toString()).toPlainString()
		if (!valueString.contains(".")) {
			axisLeft.valueFormatter = CandleLeftLabelFormatter(0)
		} else {
			var pointIndex = 0
			valueString.forEachIndexed { index, c ->
				if (c == '.') {
					pointIndex = index
				} else if (pointIndex > 0) {
					if (c != '0') {
						val formatLimit = index + 2 - pointIndex
						axisLeft.valueFormatter = CandleLeftLabelFormatter(formatLimit)
						return
					}
				}
			}
		}
		
		
	}
	
	/**
	 * @date: 2018/8/22
	 * @author: yanglihai
	 * @description: 计算图标显示的第一个蜡烛的下标
	 */
	private fun calculateVisibleIndex() {
		val trans = getTransformer(mData.dataSets[0].axisDependency)
		val buffers = FloatArray(4)
		realData.forEachIndexed { index, candleEntry ->
			buffers[0] = candleEntry.x - 0.5f + mData.dataSets[0].barSpace
			buffers[1] = candleEntry.close
			buffers[2] = buffers[0]
			buffers[3] = candleEntry.open
			trans.pointValuesToPixel(buffers) // 计算出蜡烛在view的坐标
			// 最左侧的label也会被遮挡 所以要计算出左侧label的宽度，得到精确地第一个显示的蜡烛
			val measurePaint = Paint()
			measurePaint.textSize = axisLeft.textSize
			val rect = Rect()
			var measureStr = ""
			axisLeft.longestLabel.forEach {
				measureStr += if (it == '.') it else '0'
			}
			measurePaint.getTextBounds(measureStr, 0, measureStr.length, rect)
			if (buffers[0] > rect.width()) {
				if (Math.abs(lastStartIndex - index) > 1 || index == 0) {
					// 防止频繁刷新造成抖动，所以要间隔>1, 如果是第一个item，那么直接进入这里，执行计算
					lastStartIndex = index
					resetMaxMin(index)
				} else {
					if (needCalculate) calculateHandler.postDelayed(calculateRunnable, 15)
				}
				return
			}
		}
	}
	
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			postCalculate()
		} else if (event.action == MotionEvent.ACTION_UP) {
			calculateHandler.postDelayed(disCalculateRunnable, 1000)
		}
		return super.onTouchEvent(event)
	}
	
	private fun postCalculate() {
		calculateHandler.removeCallbacks(disCalculateRunnable)
		needCalculate = true
		calculateHandler.removeCallbacks(calculateRunnable)
		calculateHandler.post(calculateRunnable)
	}
	
	private val calculateHandler = Handler()
	
	private val calculateRunnable = Runnable {
		 calculateVisibleIndex()
	}
	
	private val disCalculateRunnable = Runnable {
		needCalculate = false
	}
	private var needCalculate = false
	
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		calculateHandler.removeCallbacks(calculateRunnable)
	}
	
	
	/**
	 * @date: 2018/8/22
	 * @author: yanglihai
	 * @description: 不清除marker，需要重写clear
	 */
	override fun clear() {
		var tempHighlight: Array<Highlight>? = null
		mIndicesToHighlight?.apply {
			tempHighlight = arrayOf(first())
		}
		super.clear()
		tempHighlight?.apply {
			mIndicesToHighlight = this
		}
	}
	
	abstract fun formattedDateByType(date: Long): String
}
