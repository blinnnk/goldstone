package io.goldstone.blockchain.common.component.chart.line

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.orZero
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.chart.XAxisRenderer
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import java.util.*

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表
 */
abstract class LineChart : BarLineChartBase<LineData>, LineDataProvider {

	private var chartColor: Int = Spectrum.red
	private var chartShadowResource: Int = R.drawable.fade_red
	private var labelTextSize = fontSize(8)
	abstract val isDrawPoints: Boolean
	abstract val isPerformBezier: Boolean
	abstract val dragEnable: Boolean
	abstract val touchEnable: Boolean
	abstract val animateEnable: Boolean
	abstract fun lineLabelCount(): Int

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
		chartColor = Spectrum.red
		labelTextSize = fontSize(8)

		val gridLineColor = GrayScale.lightGray
		val labelColor = GrayScale.midGray

		isScaleXEnabled = false
		isScaleYEnabled = false
		mPinchZoomEnabled = true
		isDragEnabled = dragEnable
		legend.isEnabled = false // 标签是否显示
		description.isEnabled = false // 描述信息展示

		resetMarkerView()

		xAxis.apply {
			valueFormatter = IAxisValueFormatter { value, _ ->
				val valueData = (data?.getDataSetByIndex(0) as? LineDataSet)?.values
				val position = value.toInt()
				val entry = try {
					valueData?.get(position)
				} catch (error: Exception) {
					LogUtil.error("LineChart getFormattedValue", error)
					return@IAxisValueFormatter ""
				}
				val label = entry?.data.toString().toLongOrNull()
				when {
					position > valueData?.lastIndex.orZero() -> ""
					label.isNull() -> entry?.data?.toString().orEmpty()
					else -> TimeUtils.formatMdDate(label.orElse(0L))
				}
			}
			position = XAxis.XAxisPosition.BOTTOM
			setDrawAxisLine(false)
			setDrawLabels(true)
			gridColor = gridLineColor
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

		if (animateEnable) {
			animateY(1000)
		}
	}

	protected fun resetMarkerView() {

		marker = object : LineMarkerView(context) {
			override fun getChartWidth(): Int {
				return this@LineChart.width
			}

			override fun getChartHeight(): Int {
				return this@LineChart.height
			}
		}
	}

	fun resetDataWithTargetLabelCount(dataRows: List<Entry>) {
		xAxis.setLabelCount(dataRows.lastIndex + 1, true)
		resetData(dataRows)
	}

	open fun resetData(dataRows: List<Entry>) {
		val pointColor = Spectrum.deepBlue
		val chartWidth = 3.5f
		val dataSet: LineDataSet?
		if (!mData.isNull() && mData.dataSetCount > 0) {
			dataSet = mData.getDataSetByIndex(0) as? LineDataSet
			dataSet?.values = dataRows
			dataSet?.color = chartColor
			dataSet?.fillDrawable = ContextCompat.getDrawable(context, chartShadowResource)
			mData.notifyDataChanged()
			notifyDataSetChanged()
		} else {
			// create a dataSet and give it a type
			dataSet = LineDataSet(dataRows, "lineData ").apply {
				valueFormatter = IValueFormatter { _, entry, _, _ ->
					val entryBean = entry.data as? Entry
					entryBean?.y.toString()
				}
				// 平滑的曲线
				if (isPerformBezier) {
					mode = LineDataSet.Mode.HORIZONTAL_BEZIER
					cubicIntensity = 0.5f
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
				// fill drawable only supported on api level 18 and above
				fillDrawable = ContextCompat.getDrawable(context, chartShadowResource)
			}

			val dataSets = ArrayList<ILineDataSet>()
			// add the dataSets
			dataSets.add(dataSet)
			// create a data object with the dataSets
			val data = LineData(dataSets)
			// set data
			setData(data)
			if (dragEnable) {
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
		if (!mRenderer.isNull() && mRenderer is LineChartRenderer) {
			(mRenderer as LineChartRenderer).releaseBitmap()
		}
		super.onDetachedFromWindow()
	}

	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		return if (touchEnable) super.dispatchTouchEvent(ev)
		else false
	}
}