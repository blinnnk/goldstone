package io.goldstone.blockchain.common.component.chart.pie

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import com.github.mikephil.charting.charts.PieRadarChartBase

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data .PieData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils

/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */

open class PieChart : PieRadarChartBase<PieData> {
  
  /**
   * rect object that represents the bounds of the piechart, needed for
   * drawing the circle
   */
  /**
   * returns the circlebox, the boundingbox of the pie-chart slices
   *
   * @return
   */
  val circleBox = RectF()
  
  /**
   * flag indicating if entry labels should be drawn or not
   */
  /**
   * Returns true if drawing the entry labels is enabled, false if not.
   *
   * @return
   */
  var isDrawEntryLabelsEnabled = true
    
  
  /**
   * array that holds the width of each pie-slice in degrees
   */
  /**
   * returns an integer array of all the different angles the chart slices
   * have the angles in the returned array determine how much space (of 360°)
   * each slice takes
   *
   * @return
   */
  var drawAngles = FloatArray(1)
    
  
  /**
   * array that holds the absolute angle in degrees of each slice
   */
  /**
   * returns the absolute angles of the different chart slices (where the
   * slices end)
   *
   * @return
   */
  var absoluteAngles = FloatArray(1)
    
  
  /**
   * if true, the white hole inside the chart will be drawn
   */
  /**
   * returns true if the hole in the center of the pie-chart is set to be
   * visible, false if not
   *
   * @return
   */
  /**
   * set this to true to draw the pie center empty
   *
   * @param enabled
   */
  var isDrawHoleEnabled = true
  
  /**
   * if true, the hole will see-through to the inner tips of the slices
   */
  /**
   * Returns true if the inner tips of the slices are visible behind the hole,
   * false if not.
   *
   * @return true if slices are visible behind the hole.
   */
  var isDrawSlicesUnderHoleEnabled = false
  
  /**
   * if true, the values inside the piechart are drawn as percent values
   */
  /**
   * Returns true if using percentage values is enabled for the chart.
   *
   * @return
   */
  var isUsePercentValuesEnabled = false
  
  /**
   * if true, the slices of the piechart are rounded
   */
  /**
   * Returns true if the chart is set to draw each end of a pie-slice
   * "rounded".
   *
   * @return
   */
  val isDrawRoundedSlicesEnabled = false
  
  /**
   * variable for the text that is drawn in the center of the pie-chart
   */
  /**
   * returns the text that is drawn in the center of the pie-chart
   *
   * @return
   */
  /**
   * Sets the text String that is displayed in the center of the PieChart.
   *
   * @param text
   */
  var centerText: CharSequence? = ""
  
  private val mCenterTextOffset = MPPointF.getInstance(0f, 0f)
  
  /**
   * indicates the size of the hole in the center of the piechart, default:
   * radius / 2
   */
  /**
   * Returns the size of the hole radius in percent of the total radius.
   *
   * @return
   */
  /**
   * sets the radius of the hole in the center of the piechart in percent of
   * the maximum radius (max = the radius of the whole chart), default 50%
   *
   * @param percent
   */
  var holeRadius = 50f
  
  /**
   * the radius of the transparent circle next to the chart-hole in the center
   */
  /**
   * sets the radius of the transparent circle that is drawn next to the hole
   * in the piechart in percent of the maximum radius (max = the radius of the
   * whole chart), default 55% -> means 5% larger than the center-hole by
   * default
   *
   * @param percent
   */
  var transparentCircleRadius = 55f
  
  /**
   * if enabled, centertext is drawn
   */
  /**
   * returns true if drawing the center text is enabled
   *
   * @return
   */
  var isDrawCenterTextEnabled = true
    
  
  /**
   * the rectangular radius of the bounding box for the center text, as a percentage of the pie
   * hole
   * default 1.f (100%)
   */
  /**
   * the rectangular radius of the bounding box for the center text, as a percentage of the pie
   * hole
   * default 1.f (100%)
   */
  var centerTextRadiusPercent = 100f
  
  var mMaxAngle = 360f
  
  /**
   * returns the center of the circlebox
   *
   * @return
   */
  val centerCircleBox: MPPointF
    get() = MPPointF.getInstance(
      circleBox.centerX(),
      circleBox.centerY()
    )
  
  /**
   * Returns the offset on the x- and y-axis the center text has in dp.
   *
   * @return
   */
  val centerTextOffset: MPPointF
    get() = MPPointF.getInstance(
      mCenterTextOffset.x,
      mCenterTextOffset.y
    )
  
  /**
   * Sets the max angle that is used for calculating the pie-circle. 360f means
   * it's a full PieChart, 180f results in a half-pie-chart. Default: 360f
   *
   * @param maxangle min 90, max 360
   */
  var maxAngle: Float
    get() = mMaxAngle
    set(maxangle) {
      var maxangle = maxangle
      
      if (maxangle > 360) maxangle = 360f
      
      if (maxangle < 90) maxangle = 90f
      
      this.mMaxAngle = maxangle
    }
  
  constructor(context: Context) : super(context)
  
  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(
    context,
    attrs
  )
  
  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
  ) : super(
    context,
    attrs,
    defStyle
  )
  
  override fun init() {
    super.init()
    
    mRenderer = PieChartRenderer(
      this,
      mAnimator,
      mViewPortHandler
    )
    mXAxis = null
    
    mHighlighter = PieHighlighter(this)
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    
    if (mData == null) return
    
    mRenderer.drawData(canvas)
    
    if (valuesToHighlight()) mRenderer.drawHighlighted(
      canvas,
      mIndicesToHighlight
    )
    
    mRenderer.drawExtras(canvas)
    
    mRenderer.drawValues(canvas)
    
    mLegendRenderer.renderLegend(canvas)
    
    drawDescription(canvas)
    
    drawMarkers(canvas)
  }
  
  override fun calculateOffsets() {
    super.calculateOffsets()
    
    // prevent nullpointer when no data set
    if (mData == null) return
    
    val diameter = diameter
    val radius = diameter / 2f
    
    val center = centerOffsets
    
    val shift = mData.dataSet.selectionShift
    
    // create the circle box that will contain the pie-chart (the bounds of
    // the pie-chart)
    circleBox.set(
      center.x - radius + shift,
      center.y - radius + shift,
      center.x + radius - shift,
      center.y + radius - shift
    )
    
    MPPointF.recycleInstance(center)
  }
  
  override fun calcMinMax() {
    calcAngles()
  }
  
  override fun getMarkerPosition(highlight: Highlight): FloatArray {
    
    val center = centerCircleBox
    var radius = this.radius
    
    var off = radius / 10f * 3.6f
    
    if (isDrawHoleEnabled) {
      off = (radius - radius / 100f * holeRadius) / 2f
    }
    
    radius -= off // offset to keep things inside the chart
    
    val rotationAngle = rotationAngle
    
    val entryIndex = highlight.x.toInt()
    
    // offset needed to center the drawn text in the slice
    val offset = drawAngles[entryIndex] / 2
    
    // calculate the text position
    val x = (radius * Math.cos(Math.toRadians(((rotationAngle + absoluteAngles[entryIndex] - offset) * mAnimator.phaseY).toDouble())) + center.x).toFloat()
    val y = (radius * Math.sin(Math.toRadians(((rotationAngle + absoluteAngles[entryIndex] - offset) * mAnimator.phaseY).toDouble())) + center.y).toFloat()
    
    MPPointF.recycleInstance(center)
    return floatArrayOf(x, y)
  }
  
  /**
   * calculates the needed angles for the chart slices
   */
  private fun calcAngles() {
    
    val entryCount = mData.entryCount
    
    if (drawAngles.size != entryCount) {
      drawAngles = FloatArray(entryCount)
    } else {
      for (i in 0 until entryCount) {
        drawAngles[i] = 0f
      }
    }
    if (absoluteAngles.size != entryCount) {
      absoluteAngles = FloatArray(entryCount)
    } else {
      for (i in 0 until entryCount) {
        absoluteAngles[i] = 0f
      }
    }
    
    val yValueSum = mData.yValueSum
    
    val dataSets = mData.dataSets
    
    for (dataSetIndex in 0 until mData.dataSetCount) {
      
      val set = dataSets[dataSetIndex]
      
      for (entryIndex in 0 until set.entryCount) {
        
        drawAngles[entryIndex] = calcAngle(
          Math.abs(set.getEntryForIndex(entryIndex).y),
          yValueSum
        )
        
        if (entryIndex == 0) {
          absoluteAngles[entryIndex] = drawAngles[entryIndex]
        } else {
          absoluteAngles[entryIndex] = absoluteAngles[entryIndex - 1] + drawAngles[entryIndex]
        }
        
      }
    }
    
  }
  
  /**
   * Checks if the given index is set to be highlighted.
   *
   * @param index
   * @return
   */
  fun needsHighlight(index: Int): Boolean {
    
    // no highlight
    if (!valuesToHighlight()) return false
    
    for (indecieIndex in mIndicesToHighlight.indices)
    
    // check if the xvalue for the given dataset needs highlight
      if (mIndicesToHighlight[indecieIndex].x.toInt() == index) return true
    
    return false
  }
  
  /**
   * calculates the needed angle for a given value
   *
   * @param value
   * @param yValueSum
   * @return
   */
  private fun calcAngle(
    value: Float,
    yValueSum: Float = mData.yValueSum
  ): Float {
    return value / yValueSum * mMaxAngle
  }
  
  /**
   * This will throw an exception, PieChart has no XAxis object.
   *
   * @return
   */
  @Deprecated("")
  override fun getXAxis(): XAxis {
    throw RuntimeException("PieChart has no XAxis")
  }
  
  override fun getIndexForAngle(angle: Float): Int {
    
    // take the current angle of the chart into consideration
    val a = Utils.getNormalizedAngle(angle - rotationAngle)
    
    for (index in absoluteAngles.indices) {
      if (absoluteAngles[index] > a) return index
    }
    
    return -1 // return -1 if no index found
  }
  
  /**
   * Returns the index of the DataSet this x-index belongs to.
   *
   * @param xIndex
   * @return
   */
  fun getDataSetIndexForIndex(xIndex: Int): Int {
    
    val dataSets = mData.dataSets
    
    for (index in dataSets.indices) {
      if (dataSets[index].getEntryForXValue(
          xIndex.toFloat(),
          java.lang.Float.NaN
        ) != null
      ) return index
    }
    
    return -1
  }
  
  /**
   * Sets the color for the hole that is drawn in the center of the PieChart
   * (if enabled).
   *
   * @param color
   */
  fun setHoleColor(color: Int) {
    (mRenderer as PieChartRenderer).paintHole.color = color
  }
  
  /**
   * Enable or disable the visibility of the inner tips of the slices behind the hole
   */
  fun setDrawSlicesUnderHole(enable: Boolean) {
    isDrawSlicesUnderHoleEnabled = enable
  }
  
  /**
   * set this to true to draw the text that is displayed in the center of the
   * pie chart
   *
   * @param enabled
   */
  fun setDrawCenterText(enabled: Boolean) {
    this.isDrawCenterTextEnabled = enabled
  }
  
  override fun getRequiredLegendOffset(): Float {
    return mLegendRenderer.labelPaint.textSize * 2f
  }
  
  override fun getRequiredBaseOffset(): Float {
    return 0f
  }
  
  override fun getRadius(): Float {
    return if (circleBox == null) 0f
    else Math.min(
      circleBox.width() / 2f,
      circleBox.height() / 2f
    )
  }
  
  /**
   * sets the typeface for the center-text paint
   *
   * @param t
   */
  fun setCenterTextTypeface(t: Typeface) {
    (mRenderer as PieChartRenderer).paintCenterText.typeface = t
  }
  
  /**
   * Sets the size of the center text of the PieChart in dp.
   *
   * @param sizeDp
   */
  fun setCenterTextSize(sizeDp: Float) {
    (mRenderer as PieChartRenderer).paintCenterText.textSize = Utils.convertDpToPixel(sizeDp)
  }
  
  /**
   * Sets the size of the center text of the PieChart in pixels.
   *
   * @param sizePixels
   */
  fun setCenterTextSizePixels(sizePixels: Float) {
    (mRenderer as? PieChartRenderer)?.paintCenterText?.textSize = sizePixels
  }
  
  /**
   * Sets the offset the center text should have from it's original position in dp. Default x = 0, y = 0
   *
   * @param x
   * @param y
   */
  fun setCenterTextOffset(
    x: Float,
    y: Float
  ) {
    mCenterTextOffset.x = Utils.convertDpToPixel(x)
    mCenterTextOffset.y = Utils.convertDpToPixel(y)
  }
  
  /**
   * Sets the color of the center text of the PieChart.
   *
   * @param color
   */
  fun setCenterTextColor(color: Int) {
    (mRenderer as? PieChartRenderer)?.paintCenterText?.color = color
  }
  
  /**
   * Sets the color the transparent-circle should have.
   *
   * @param color
   */
  fun setTransparentCircleColor(color: Int) {
    
    val paint = (mRenderer as? PieChartRenderer)?.paintTransparentCircle
    paint?.apply {
      this.color = color
    }
    
  }
  
  /**
   * Sets the amount of transparency the transparent circle should have 0 = fully transparent,
   * 255 = fully opaque.
   * Default value is 100.
   *
   * @param alpha 0-255
   */
  fun setTransparentCircleAlpha(alpha: Int) {
    (mRenderer as? PieChartRenderer)?.paintTransparentCircle?.alpha = alpha
  }
  
  /**
   * Set this to true to draw the entry labels into the pie slices (Provided by the getLabel() method of the PieEntry class).
   * Deprecated -> use setDrawEntryLabels(...) instead.
   *
   * @param enabled
   */
  @Deprecated("")
  fun setDrawSliceText(enabled: Boolean) {
    isDrawEntryLabelsEnabled = enabled
  }
  
  /**
   * Set this to true to draw the entry labels into the pie slices (Provided by the getLabel() method of the PieEntry class).
   *
   * @param enabled
   */
  fun setDrawEntryLabels(enabled: Boolean) {
    isDrawEntryLabelsEnabled = enabled
  }
  
  /**
   * Sets the color the entry labels are drawn with.
   *
   * @param color
   */
  fun setEntryLabelColor(color: Int) {
    (mRenderer as? PieChartRenderer)?.paintEntryLabels?.color = color
  }
  
  /**
   * Sets a custom Typeface for the drawing of the entry labels.
   *
   * @param tf
   */
  fun setEntryLabelTypeface(tf: Typeface) {
    (mRenderer as? PieChartRenderer)?.paintEntryLabels?.typeface = tf
  }
  
  /**
   * Sets the size of the entry labels in dp. Default: 13dp
   *
   * @param size
   */
  fun setEntryLabelTextSize(size: Float) {
    (mRenderer as? PieChartRenderer)?.paintEntryLabels?.textSize = Utils.convertDpToPixel(size)
  }
  
  /**
   * If this is enabled, values inside the PieChart are drawn in percent and
   * not with their original value. Values provided for the IValueFormatter to
   * format are then provided in percent.
   *
   * @param enabled
   */
  fun setUsePercentValues(enabled: Boolean) {
    isUsePercentValuesEnabled = enabled
  }
}