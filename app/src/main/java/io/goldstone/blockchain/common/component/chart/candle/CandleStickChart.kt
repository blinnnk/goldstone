package io.goldstone.blockchain.common.component.chart.candle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.component.chart.XValueFormatter
import io.goldstone.blockchain.common.value.Spectrum

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
open class CandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
	
  private var labelColor = Color.rgb(152, 152, 152)
  private var shadowColor = Color.DKGRAY // 蜡烛柄颜色
  private var decreasingColor = Color.rgb(219, 74, 76)
  private var increasingColor = Color.rgb(67, 200, 135)
	private var gridLineColor = Color.rgb(236, 236, 236)
  
  private var neutralColor = Color.BLUE
  private var barSpace = 0.2f
  private var shadowWidth = 2f // 蜡烛柄宽度
  
  private var xRangeVisibleNum = 10f
  
  private var xAxisSpace = 0.5f
  
  private lateinit var blinnnkXValueFormatter: XValueFormatter
  
  private lateinit var blinnnkMarkerView: CandleMarkerView
	
  constructor(context: Context) : super(context)
  
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  
  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
  ) : super(context, attrs, defStyle)
  
  
  override fun init() {
    super.init()
    
    blinnnkMarkerView = CandleMarkerView(context)
    blinnnkMarkerView.setChartView(this)
  
    blinnnkXValueFormatter = XValueFormatter(this)
    mXAxisRenderer = XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer)
    mRenderer = CandleStickChartRenderer(this, mAnimator, mViewPortHandler)
    
		resetAxisStyle()
		post {
			setEmptyData()
		}
		
  }
	
	open fun resetDataPost(dataRows: List<CandleEntry>) {
		setEmptyData()
		postDelayed( {
			resetData(dataRows)
		},  500)
	}
  
  open fun resetData(dataRows: List<CandleEntry>) {
	
		isScaleXEnabled = false
		isScaleYEnabled = false
		
    resetTracking()
    clear()
	
		mXAxis.labelCount = if (dataRows.size > xRangeVisibleNum) xRangeVisibleNum.toInt() else dataRows.size
		
		val dataSet = CandleDataSet(dataRows, "Candle Set")
    
    dataSet.apply {
      setDrawIcons(false)
      axisDependency = YAxis.AxisDependency.LEFT
      shadowWidth = this@CandleStickChart.shadowWidth
      decreasingColor = this@CandleStickChart.decreasingColor
      decreasingPaintStyle = Paint.Style.FILL
      increasingColor = this@CandleStickChart.increasingColor
      increasingPaintStyle = Paint.Style.FILL
      neutralColor = this@CandleStickChart.neutralColor
      setDrawValues(false)
      barSpace = this@CandleStickChart.barSpace
      showCandleBar = true
      shadowColorSameAsCandle = true
    }
    
    val data = CandleData(dataSet)
    
    setData(data)
    setVisibleXRangeMaximum(xRangeVisibleNum)
    setVisibleXRangeMinimum(xRangeVisibleNum)
    invalidate()
  }
  
  private fun resetAxisStyle() {
	
		// 初始化一些属性
		labelColor = Color.rgb(152, 152, 152)
		shadowColor = Color.DKGRAY // 蜡烛柄颜色
		decreasingColor = Spectrum.red
		increasingColor = Spectrum.green
		gridLineColor = Color.rgb(236, 236, 236)
		neutralColor = Color.BLUE
		barSpace = 0.2f
		shadowWidth = 2f // 蜡烛柄宽度
		xRangeVisibleNum = 10f
		xAxisSpace = 0.5f
		minOffset = 0f
		
    isScaleXEnabled = false
		isScaleYEnabled = false
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
    
    with(xAxis) {
      textColor = this@CandleStickChart.labelColor
      position = XAxis.XAxisPosition.BOTTOM
      valueFormatter = blinnnkXValueFormatter
      setDrawGridLines(true)
			gridColor = gridLineColor
			axisLineColor = gridLineColor
      spaceMin = xAxisSpace
      spaceMax = xAxisSpace
    }
    
    with(axisLeft) {
      setDrawAxisLine(true)
      setDrawLabels(true)
			setDrawGridLines(true)
			gridColor = gridLineColor
			axisLineColor = gridLineColor
			textColor = Color.rgb(152, 152, 152)
			setLabelCount(6, true)
    }
    with(axisRight) {
			setDrawAxisLine(true)
			setDrawLabels(false)
			setDrawGridLines(false)
			axisLineColor = gridLineColor
      textColor = this@CandleStickChart.labelColor
    }
    
    marker = blinnnkMarkerView
  }
  
  override fun getCandleData(): CandleData {
    return mData
  }
  
  fun setEmptyData() {
    val candleEntrySet = mutableListOf<CandleEntry>()
    for (i in 0 until xRangeVisibleNum.toInt()) {
      candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(i.toFloat()),
        0f, 0f, 0f, 0f, System.currentTimeMillis()))
    
    }
    resetData(candleEntrySet)
  }
	
}
