package io.goldstone.blockchain.common.component.chart.candle

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.MotionEvent
import com.blinnnk.extension.isNull
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
abstract class CandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
	
  private var xRangeVisibleNum = 30
	private var leftLabelCount = 6
	private var realData = arrayListOf<CandleEntry>()
	private var labelTextSize = fontSize(8)
	protected var dateType = DateUtils.FORMAT_SHOW_TIME
	
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
	
  open fun resetData(dateType: Int, dataRows: ArrayList<CandleEntry>) {
		
		this.dateType = dateType
		realData = dataRows
		isScaleXEnabled = false
		isScaleYEnabled = false
		val candleDecreasingColor = Spectrum.red
		val candleIncreasingColor = Spectrum.green
		val candleNeutralColor = Color.BLUE
		val candleBarSpace = 0.1f
		val candleShadowWidth = 2f // 蜡烛柄宽度
		resetTracking()
    clear()
		xRangeVisibleNum = if (dataRows.size < 30) { dataRows.size }else { 30 }
		val ratio = when(dateType) {
			DateUtils.FORMAT_SHOW_TIME -> 5
			else -> 3
		}
		mXAxis.labelCount = if (dataRows.size > xRangeVisibleNum) xRangeVisibleNum / ratio else dataRows.size / ratio
		when(dateType) {
			DateUtils.FORMAT_SHOW_TIME -> {
				if (dataRows.size < 5) {
					mXAxis.labelCount = dataRows.size
				}
			}
			else -> {
				if (dataRows.size < 10) {
					mXAxis.labelCount = dataRows.size
				}
			}
		}
		val dataSet = CandleDataSet(dataRows, "Candle Set")
    dataSet.apply {
      setDrawIcons(false)
      axisDependency = YAxis.AxisDependency.LEFT
      shadowWidth = candleShadowWidth
      decreasingColor = candleDecreasingColor
      decreasingPaintStyle = Paint.Style.FILL
      increasingColor = candleIncreasingColor
      increasingPaintStyle = Paint.Style.FILL
      neutralColor = candleNeutralColor
      setDrawValues(false)
      barSpace = candleBarSpace
      showCandleBar = true
      shadowColorSameAsCandle = true
    }
    val data = CandleData(dataSet)
    setData(data)
    setVisibleXRangeMaximum(xRangeVisibleNum.toFloat())
    setVisibleXRangeMinimum(xRangeVisibleNum.toFloat())
    invalidate()
		calculateHandler.removeCallbacks(calculateRunnable)
		if (needCalculate) {
			calculateHandler.postDelayed(calculateRunnable, 15)
		}
  }
  
  private fun resetAxisStyle() {
		// 初始化一些属性
		val labelColor = GrayScale.midGray
		val gridLineColor = GrayScale.lightGray
		val xAxisSpace = 0.5f
		// 为了防止方法执行到此，以下数据还没有被初始化，所以在这里重新赋值
		leftLabelCount = 6
		xRangeVisibleNum = 30
		minOffset = 0f
		labelTextSize = fontSize(10)
    isScaleXEnabled = false
		isScaleYEnabled = false
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
		
    with(xAxis) {
			valueFormatter = IAxisValueFormatter { value, _ ->
				var result = ""
				if (!this@CandleStickChart.data.isNull() ||
					!this@CandleStickChart.data.getDataSetByIndex(0).isNull()){
					val position = value.toInt()
					var values = (this@CandleStickChart.data.getDataSetByIndex(0) as CandleDataSet).values
					if (position < values.size) {
						val entry = values[position]
						if ((entry.data is Long)) {
							result = if (entry.data == 0) "" else  formateDateByType(entry.data as Long)
						}
						if (entry.data is String) {
							result = if ((entry.data as String).isEmpty()) "" else formateDateByType((entry.data as String).toLong())
						}
					}else {
						result = ""
					}
				}
				result
			}
      textColor = labelColor
      position = XAxis.XAxisPosition.BOTTOM
      setDrawGridLines(true)
			gridColor = gridLineColor
			axisLineColor = gridLineColor
      spaceMin = xAxisSpace
      spaceMax = xAxisSpace
			textSize = labelTextSize
			typeface = GoldStoneFont.heavy(context)
    }
    
    with(axisLeft) {
      setDrawAxisLine(true)
      setDrawLabels(true)
			setDrawGridLines(true)
			gridColor = gridLineColor
			axisLineColor = gridLineColor
			textColor = labelColor
			setLabelCount(leftLabelCount, true)
			textSize = labelTextSize
			typeface = GoldStoneFont.heavy(context)
	
		}
    with(axisRight) {
			setDrawAxisLine(true)
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
  
  fun setEmptyData() {
		if (realData.size != 0) {
			return
		}
    val candleEntrySet = arrayListOf<CandleEntry>()
		(0 until xRangeVisibleNum).forEach {
			index -> candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(index.toFloat()),
			0f, 0f, 0f, 0f, System.currentTimeMillis()))
		}
    resetData(dateType, candleEntrySet)
  }
	/**
	 * @date: 2018/8/22
	 * @author: yanglihai
	 * @description: 计算显示在屏幕上蜡烛的最高值和最低值
	 */
	fun resetMaxMin(firstVisibleIndex: Int) {
		val endIndex: Int = firstVisibleIndex + xRangeVisibleNum
		val max = if (endIndex > realData.size) realData.size else endIndex
		var high = realData[firstVisibleIndex].high
		var low = realData[firstVisibleIndex].low
		(firstVisibleIndex until max).forEachIndexed {
				i, _ ->
			if (realData[firstVisibleIndex + i].low < low) {
				low = realData[firstVisibleIndex + i].low
			}
			if (realData[firstVisibleIndex + i].high > high) {
				high = realData[firstVisibleIndex + i].high
			}
		}
		val distance = (high - low) /20 //距离上下的间距
		context.runOnUiThread {
			if (high != 0f && low != 0f) {
				with(axisLeft) {
					axisMinimum = low - distance
					axisMaximum = high + distance
					setLabelCount(leftLabelCount, true)
					textSize = labelTextSize
				}
			}
			mXAxis.textSize = labelTextSize
			resetData(dateType, realData)
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
		realData.forEachIndexed {
				index, candleEntry ->
			buffers[0] = candleEntry.x - 0.5f + mData.dataSets[0].barSpace
			buffers[1] = candleEntry.close
			buffers[2] = buffers[0]
			buffers[3] = candleEntry.open
			trans.pointValuesToPixel(buffers) // 计算出蜡烛在view的坐标
			// 最左侧的label也会被遮挡 所以要计算出左侧label的宽度，得到精确地第一个显示的蜡烛
			val measurePaint = Paint()
			measurePaint.textSize = axisLeft.textSize
			val rect = Rect()
			measurePaint.getTextBounds(axisLeft.longestLabel, 0, axisLeft.longestLabel.length, rect)
			if (buffers[0] > rect.width() + 20) {
				resetMaxMin(index)
				return
			}
		}
	}
	
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			needCalculate = true
			calculateHandler.removeCallbacks(calculateRunnable)
			calculateHandler.post(calculateRunnable)
		}else if (event.action == MotionEvent.ACTION_UP) {
			calculateHandler.postDelayed(disCalculateRunnable, 1000)
		}
		return super.onTouchEvent(event)
	}
	
	
	private val calculateHandler = Handler()
	
	private val calculateRunnable = Runnable {
		doAsync { calculateVisibleIndex() }
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
			tempHighlight = arrayOf(this[0])
		}
		super.clear()
		tempHighlight?.apply {
			mIndicesToHighlight = this
		}
	}
	
	abstract fun formateDateByType(date: Long) : String
}
