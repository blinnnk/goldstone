package io.goldstone.blockchain.common.component.chart.line

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.Utils
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.component.chart.XValueFormatter
import java.util.*

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表
 */
abstract class LineChart : BarLineChartBase<LineData>, LineDataProvider {
  
  
  private lateinit var blinnnkMarkerView: LineMarkerView
  private lateinit var blinnnkXValueFormatter: XValueFormatter
  private val xRangeVisibleNum = 8f
  private val lineYValueFormatter = LineYValueFormatter()
  
  private var pointColor: Int = Color.BLACK
  
  private val gridlineColor = Color.rgb(236,236,236)
  private val labelColor = Color.rgb(152, 152, 152)
	
  private var chartColor: Int = Color.RED
  private val chartWidth = 3f
  private val pointRadius = arrayListOf(5f, 2f)
  private var chartShadowResource: Int = R.drawable.fade_red
	
	abstract fun isDrawPoints() : Boolean
	abstract fun isPerformBezier() : Boolean
  
  constructor(context: Context) : super(context)
  
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  
  constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
    super(context, attrs, defStyle)
  
  override fun init() {
    super.init()
    mRenderer = LineChartRenderer(this, mAnimator, mViewPortHandler)
    mXAxisRenderer = XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer)
    
    blinnnkMarkerView = LineMarkerView(context)
    blinnnkMarkerView.setChartView(this)
    blinnnkXValueFormatter = XValueFormatter(this)
    
    post {
      initAxisStyle()
    }
  }
  
  fun initAxisStyle() {
    isScaleXEnabled = false
    isScaleYEnabled = false
    mPinchZoomEnabled = true
    isDragEnabled = true
    legend.isEnabled = false // 标签是否显示
    description.isEnabled = false // 描述信息展示
  
    marker = blinnnkMarkerView
  
    xAxis.apply {
      valueFormatter = blinnnkXValueFormatter
      position = XAxis.XAxisPosition.BOTTOM
      setDrawAxisLine(false)
      setDrawLabels(true)
      gridColor = gridlineColor
      textColor = labelColor
			mAxisMinimum = 10f
    }
    mAxisLeft.apply {
			axisLineColor = gridlineColor
      gridColor = gridlineColor
      textColor = labelColor
			setLabelCount(4,true)
    }
  
    axisRight.apply {
			axisLineColor = gridlineColor
      isEnabled = true
      setDrawLabels(false)
      setDrawGridLines(false)
    }
    
    animateY(1000)
  }
  
  fun notifyData(dataRows: List<Entry>) {
    setEmptyData()
    postDelayed(object : Runnable{
      override fun run() {
        resetData(dataRows)
      }
    },500)
  }
  
	fun resetData(dataRows: List<Entry>) {
    
    val dataSet: LineDataSet
    
    if (mData != null && mData.dataSetCount > 0) {
      dataSet = mData.getDataSetByIndex(0) as LineDataSet
      dataSet.values = dataRows
      mData.notifyDataChanged()
      notifyDataSetChanged()
    } else {
      // create a dataset and give it a type
      dataSet = LineDataSet(dataRows, "lineData ")
      dataSet.apply {
        valueFormatter = lineYValueFormatter

        // 平划的曲线
        if (isPerformBezier()){
          mode = LineDataSet.Mode.CUBIC_BEZIER
          cubicIntensity = 0.2f
        }
        
        setDrawIcons(false) // 显示图标
        setDrawValues(false) // 展示每个点的值
        
        color = chartColor
        lineWidth = chartWidth
        if (isDrawPoints()) {
          // 峰值点
          setDrawCircles(true)
          setCircleColor(pointColor)
          circleRadius = pointRadius[0]
          circleHoleRadius = pointRadius[1]
          setDrawCircleHole(true)
        }else {
          setDrawCircles(false)
        }
        
        setDrawFilled(true)
  
        if (Utils.getSDKInt() >= 18) {
          // fill drawable only supported on api level 18 and above
          fillDrawable= ContextCompat.getDrawable(context, chartShadowResource)
        } else {
          fillColor = Color.TRANSPARENT
        }
      }
      
      
      val dataSets = ArrayList<ILineDataSet>()
      dataSets.add(dataSet) // add the datasets
      
      // create a data object with the datasets
      val data = LineData(dataSets)
      
      // set data
      setData(data)
			// set visible num xRange
      setVisibleXRangeMaximum(xRangeVisibleNum)
      setVisibleXRangeMinimum(xRangeVisibleNum)
    }
  }
	
	fun setChartColor(color: Int) {
		chartColor = color
		if (mData != null) {
			(mData.getDataSetByIndex(0) as LineDataSet).color = chartColor
			mData.notifyDataChanged()
			notifyDataSetChanged()
			invalidate()
		}
		
		
	}
	
	fun setShadowResource(resource: Int) {
		chartShadowResource = resource
		if (mData != null) {
			(mData.getDataSetByIndex(0) as LineDataSet).fillDrawable = ContextCompat.getDrawable(context, chartShadowResource)
			mData.notifyDataChanged()
			notifyDataSetChanged()
			invalidate()
		}
		
	}
  
  override fun getLineData(): LineData {
    return mData
  }
  
  override fun onDetachedFromWindow() {
    // releases the bitmap in the renderer to avoid oom error
    if (mRenderer != null && mRenderer is LineChartRenderer) {
      (mRenderer as LineChartRenderer).releaseBitmap()
    }
    super.onDetachedFromWindow()
  }
  
  fun setEmptyData() {
    val candleEntrySet = mutableListOf<Entry>()
    for (i in 0 until 19) {
      candleEntrySet.add(Entry(i.toFloat(),0f, java.lang.Long.valueOf(0)))
      
    }
    resetData(candleEntrySet)
  }
	
	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		return false
	}
}
