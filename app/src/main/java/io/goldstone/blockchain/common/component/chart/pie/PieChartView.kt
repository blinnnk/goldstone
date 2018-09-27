package io.goldstone.blockchain.common.component.chart.pie

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import io.goldstone.blockchain.common.Language.EOSRAMText
import kotlin.collections.ArrayList

/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */
class PieChartView(context: Context) : PieChart(context) {
	
  override fun init() {
    super.init()
    mRenderer = PieChartRenderer(
      this,
      mAnimator,
      mViewPortHandler
    )
    post {
      initAttrs()
    }
    
  }
  
  private fun initAttrs() {
    setUsePercentValues(true)
    description.isEnabled = false
    setExtraOffsets(
      0f,
      10f,
      0f,
      10f
    )
    
    dragDecelerationFrictionCoef = 0.95f

    centerText = EOSRAMText.tradeDistribute
    setCenterTextSize(18f)
    
    isDrawHoleEnabled = true
    setHoleColor(Color.TRANSPARENT)
    
    setTransparentCircleColor(Color.WHITE)
    setTransparentCircleAlpha(0)
    
    holeRadius = 60f
    transparentCircleRadius = 61f
    
    setDrawCenterText(true)
    
    rotationAngle = 0f
    // enable rotation of the chart by touch
    isRotationEnabled = true
    isHighlightPerTapEnabled = true
    
    animateY(
      1400,
      Easing.EasingOption.EaseInOutQuad
    )
    
    val l = legend
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    l.orientation = Legend.LegendOrientation.VERTICAL
    l.setDrawInside(false)
    l.isEnabled = false
		
    // entry label styling
    setEntryLabelColor(Color.WHITE)
    setEntryLabelTextSize(12f)
  }
  
	fun resetData(entries: ArrayList<PieEntry>, colors: List<Int>) {
		if (entries.size != colors.size) {
			throw RuntimeException("entries size must equals colors size")
		}
    val dataSet = PieDataSet(
      entries,
      "Election Results"
    )
    dataSet.sliceSpace = 3f
    dataSet.selectionShift = 5f
    
    dataSet.colors = colors
    dataSet.sliceSpace = 0f // 每一块之间的间隙
    dataSet.selectionShift = 10f // 点击每一个item放大的区域
    
    dataSet.valueLinePart1OffsetPercentage = 80f
    dataSet.valueLinePart1Length = 0.5f
    dataSet.valueLinePart2Length = 0.4f
    dataSet.valueLineColor = Color.GREEN
    dataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    
    val data = PieData(dataSet)
    data.setValueFormatter(PercentFormatter())
    data.setValueTextSize(11f)
    data.setValueTextColor(Color.BLACK)
    setData(data)
    
    // undo all highlights
    highlightValues(null)
    
    invalidate()
  }
	
	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		return false
	}
	
  
}