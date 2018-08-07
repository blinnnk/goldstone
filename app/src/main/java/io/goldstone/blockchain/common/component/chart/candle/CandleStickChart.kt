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

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
class CandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
	
  private val labelColor = Color.rgb(152, 152, 152)
  private val shadowColor = Color.DKGRAY // 蜡烛柄颜色
  private val decreasingColor = Color.rgb(219, 74, 76)
  private val increasingColor = Color.rgb(67, 200, 135)
  private val labelCount = 9
  private val labelRotationAngle = 320f
  
  private val neutralColor = Color.BLUE
  private val barSpace = 0.2f
  private val shadowWidth = 2f // 蜡烛柄宽度
  
  private val xRangeVisibleNum = 20f
  
  private val xAxisSpace = 0.5f
  
  private lateinit var blinnnkXValueFormatter: XValueFormatter
  
  private lateinit var blinnnkMarkerView: CandleMarkerView
  
  private var isGetLeftZero = false
  
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
  
    blinnnkXValueFormatter = XValueFormatter(this@CandleStickChart)
    mXAxisRenderer = XAxisRenderer(mViewPortHandler,
      mXAxis,
      mLeftAxisTransformer)
    mRenderer = CandleStickChartRenderer(this, mAnimator, mViewPortHandler)
    
    post {
      resetAxisStyle()
      setEmptyData()
    }
    
  }
  
  private val getLeftRunnable = object: Runnable {
    override fun run() {
      isGetLeftZero = false
    }
  }
  
	
  fun notifyData(dataRows: List<CandleEntry>) {
    setEmptyData()
    postDelayed(object : Runnable{
      override fun run() {
        resetData(dataRows)
      }
    },500)
  }
  
  private fun resetData(dataRows: List<CandleEntry>) {
    
    isGetLeftZero = true
    
    resetTracking()
    clear()
    
    val dataSet = CandleDataSet(dataRows, "Candle Set")
    
    dataSet.apply {
      setDrawIcons(false)
      axisDependency = YAxis.AxisDependency.LEFT
      shadowColor = this@CandleStickChart.shadowColor
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
    setVisibleXRangeMaximum(this@CandleStickChart.xRangeVisibleNum)
    setVisibleXRangeMinimum(this@CandleStickChart.xRangeVisibleNum)
    invalidate()
  }
  
  private fun resetAxisStyle() {
    
    setScaleEnabled(false)
    setPinchZoom(true)
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
    
    with(xAxis) {
      textColor = this@CandleStickChart.labelColor
      position = XAxis.XAxisPosition.BOTTOM
      labelCount = this@CandleStickChart.labelCount
      labelRotationAngle = this@CandleStickChart.labelRotationAngle
      valueFormatter = blinnnkXValueFormatter
      setDrawGridLines(true)
      spaceMin = xAxisSpace
      spaceMax = xAxisSpace
    }
    
    with(axisLeft) {
      setDrawAxisLine(true)
      setDrawLabels(false)
    }
    with(axisRight) {
      textColor = this@CandleStickChart.labelColor
      axisLineColor = this@CandleStickChart.labelColor
    }
    
    marker = blinnnkMarkerView
  }
  
  override fun getCandleData(): CandleData {
    return mData
  }
  
  fun setEmptyData() {
    val candleEntrySet = mutableListOf<CandleEntry>()
    for (i in 0 until 19) {
      candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(i.toFloat()),
        0f,0f,0f,0f,java.lang.Long.valueOf(0)))
    
    }
    resetData(candleEntrySet)
  }
  
}
