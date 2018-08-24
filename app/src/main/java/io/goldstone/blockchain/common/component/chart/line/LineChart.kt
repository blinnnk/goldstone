package io.goldstone.blockchain.common.component.chart.line

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import com.blinnnk.extension.isNull
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.Utils
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import java.util.*

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表
 */
abstract class LineChart : BarLineChartBase<LineData>, LineDataProvider {
	
	private var chartColor: Int = Color.RED
	private var chartShadowResource: Int = R.drawable.fade_red
	private var labelTextSize = fontSize(8)
	abstract val isDrawPoints: Boolean
	abstract val isPerformBezier: Boolean
	abstract val dragEnable : Boolean
	abstract val touchEnable : Boolean
	abstract val animateEnable : Boolean
	abstract fun lineLabelCount() : Int
	
	constructor(context: Context) : super(context)
	
	constructor(
		context: Context,
		attrs: AttributeSet
	) : super(context, attrs)
	
	constructor(
		context: Context,
		attrs: AttributeSet,
		defStyle: Int
	) : super(context, attrs, defStyle)
	
	
	override fun init() {
		super.init()
		mRenderer = LineChartRenderer(this, mAnimator, mViewPortHandler)
		mXAxisRenderer = XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer)
		
		initAxisStyle()
	}
	
	private fun initAxisStyle() {
		chartColor = Color.RED
		labelTextSize = fontSize(8)
		
		val gridLineColor = GrayScale.lightGray
		val labelColor = GrayScale.midGray
		
		isScaleXEnabled = false
		isScaleYEnabled = false
		mPinchZoomEnabled = true
		isDragEnabled = dragEnable
		legend.isEnabled = false // 标签是否显示
		description.isEnabled = false // 描述信息展示
		
		
		marker = object : LineMarkerView(context) {
			override fun getChartWidth(): Int {
				return this@LineChart.width
			}
			
			override fun getChartHeight(): Int {
				return this@LineChart.height
			}
		}
		
		xAxis.apply {
			valueFormatter = IAxisValueFormatter { value, _ ->
				var result = ""
				if (!this@LineChart.data.isNull() ||
					!this@LineChart.data.getDataSetByIndex(0).isNull()){
					val position = value.toInt()
					val values = (this@LineChart.data.getDataSetByIndex(0) as LineDataSet).values
					if (position < values.size) {
						val entry = values[position]
						if ((entry.data is Long)) {
							result = if (entry.data == 0) "" else  TimeUtils.formatMdDate(entry.data as Long)
						}
						if (entry.data is String) {
							result = try {
								if ((entry.data as String).isEmpty()) "" else TimeUtils.formatMdDate((entry.data as String).toLong())
							}catch (exception: NumberFormatException) {
								entry.data.toString()
							}
						}
					}else {
						result = ""
					}
				}
				
				result
			}
			position = XAxis.XAxisPosition.BOTTOM
			setDrawAxisLine(false)
			setDrawLabels(true)
			gridColor = gridLineColor
			System.out.println("+++$labelColor")
			textColor = labelColor
			mAxisMinimum = 10f
			textSize = labelTextSize
			typeface = GoldStoneFont.heavy(context)
		}
		mAxisLeft.apply {
			axisLineColor = gridLineColor
			gridColor = gridLineColor
			textColor = labelColor
			setLabelCount(lineLabelCount(), true)
			textSize = labelTextSize
			typeface = GoldStoneFont.heavy(context)
		}
		
		axisRight.apply {
			axisLineColor = gridLineColor
			isEnabled = true
			setDrawLabels(false)
			setDrawGridLines(false)
		}
		
		if (animateEnable){
			animateY(1000)
		}
	}
	
	fun resetData(dataRows: List<Entry>, fitLabelCount: Boolean) {
		if (fitLabelCount) {
			mXAxis.labelCount = dataRows.size - 1
		}
		
		resetData(dataRows)
	}
	
	open fun resetData(dataRows: List<Entry>) {
		
		val pointColor = Spectrum.deepBlue
		val chartWidth = 3f
		
		val dataSet: LineDataSet
		
		if (mData != null && mData.dataSetCount > 0) {
			dataSet = mData.getDataSetByIndex(0) as LineDataSet
			dataSet.values = dataRows
			dataSet.color = chartColor
			dataSet.fillDrawable = ContextCompat.getDrawable(context, chartShadowResource)
			mData.notifyDataChanged()
			notifyDataSetChanged()
		} else {
			// create a dataset and give it a type
			dataSet = LineDataSet(dataRows, "lineData ")
			dataSet.apply {
				valueFormatter = IValueFormatter { _, entry, _, _ ->
					val entryBean = entry.data as Entry
					entryBean.y.toString()
				}
				
				// 平划的曲线
				if (isPerformBezier) {
					mode = LineDataSet.Mode.CUBIC_BEZIER
					cubicIntensity = 0.2f
				}
				
				setDrawIcons(false) // 显示图标
				setDrawValues(false) // 展示每个点的值
				
				color = chartColor
				lineWidth = chartWidth
				if (isDrawPoints) {
					val pointRadius = arrayListOf(7f, 4f)
					// 峰值点
					setDrawCircles(true)
					setCircleColor(pointColor)
					circleRadius = pointRadius[0]
					circleHoleRadius = pointRadius[1]
					setDrawCircleHole(true)
				} else {
					setDrawCircles(false)
				}
				
				setDrawFilled(true)
				
				if (Utils.getSDKInt() >= 18) {
					// fill drawable only supported on api level 18 and above
					fillDrawable = ContextCompat.getDrawable(context, chartShadowResource)
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
			if (dragEnable){
				val xRangeVisibleNum = 8f
				// set visible num xRange
				setVisibleXRangeMaximum(xRangeVisibleNum)
				setVisibleXRangeMinimum(xRangeVisibleNum)
			}
		}
		invalidate()
	}
	
	fun setChartColorAndShadowResource(color: Int, resource: Int) {
		chartColor = color
		chartShadowResource = resource
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
		val candleEntrySet = arrayListOf<Entry>()
		(0 until 19).forEach {
			index -> 	candleEntrySet.add(Entry(index.toFloat(), 0f, System.currentTimeMillis()))
		}
		resetData(candleEntrySet)
	}
	
	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		return if (touchEnable) { super.dispatchTouchEvent(ev)} else { false }
	}
}
