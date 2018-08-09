package io.goldstone.blockchain.common.component.chart.candle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.blinnnk.extension.isNull
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.component.chart.XValueFormatter
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.Spectrum

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
open class CandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
	
  private var xRangeVisibleNum = 10f
	
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
	
		val candleDecreasingColor = Spectrum.red
		val candleIncreasingColor = Spectrum.green
		val candleNeutralColor = Color.BLUE
		val candleBarSpace = 0.2f
		val candleShadowWidth = 2f // 蜡烛柄宽度
		
		resetTracking()
    clear()
	
		mXAxis.labelCount = if (dataRows.size > xRangeVisibleNum) xRangeVisibleNum.toInt() else dataRows.size
		
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
    setVisibleXRangeMaximum(xRangeVisibleNum)
    setVisibleXRangeMinimum(xRangeVisibleNum)
    invalidate()
  }
  
  private fun resetAxisStyle() {
	
		// 初始化一些属性
		val labelColor = Color.rgb(152, 152, 152)
		val gridLineColor = Color.rgb(236, 236, 236)
		val xAxisSpace = 0.5f
		
		xRangeVisibleNum = 10f
		minOffset = 0f
		
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
							result = if (entry.data == 0) "" else  TimeUtils.formatMdDate(entry.data as Long)
						}
						if (entry.data is String) {
							result = if ((entry.data as String).isEmpty()) "" else TimeUtils.formatMdDate((entry.data as String).toLong())
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
    val candleEntrySet = arrayListOf<CandleEntry>()
		
		(0 until xRangeVisibleNum.toInt()).forEach {
			index -> candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(index.toFloat()),
			0f, 0f, 0f, 0f, System.currentTimeMillis()))
		}
    resetData(candleEntrySet)
  }
	
}
