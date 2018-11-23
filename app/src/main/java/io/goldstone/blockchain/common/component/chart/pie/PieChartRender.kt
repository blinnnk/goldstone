package io.goldstone.blockchain.common.component.chart.pie


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.renderer.DataRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import io.goldstone.blockchain.common.utils.GoldStoneFont

@Suppress("DEPRECATION")
/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */
class PieChartRenderer(
	private var pieChart: PieChart,
	animator: ChartAnimator,
	viewPortHandler: ViewPortHandler
) : DataRenderer(
	animator,
	viewPortHandler
) {
	
	/**
	 * paint for the hole in the center of the pie chart and the transparent
	 * circle
	 */
	var paintHole = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.WHITE
		style = Paint.Style.FILL
	}
	var paintTransparentCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.WHITE
		style = Paint.Style.FILL
		alpha = 105
	}
	private var valueLinePaint: Paint
	
	/**
	 * paint object for the text that can be displayed in the center of the
	 * chart
	 */
	val paintCenterText = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.BLACK
		textSize = Utils.convertDpToPixel(12f)
	}
	
	/**
	 * paint object used for drwing the slice-text
	 */
	val paintEntryLabels: Paint
	
	private lateinit var centerTextLayout: StaticLayout
	private var centerTextLastValue: CharSequence? = null
	private val centerTextLastBounds = RectF()
	private val rectBuffer = arrayOf(
		RectF(),
		RectF(),
		RectF()
	)
	
	private val pathBuffer = Path()
	private val innerRectBuffer = RectF()
	
	private val holeCirclePath = Path()
	
	private var drawCenterTextPathBuffer = Path()
	
	private var drawHighlightedRectF = RectF()
	
	init {
		
		mValuePaint.textSize = Utils.convertDpToPixel(13f)
		mValuePaint.color = Color.WHITE
		mValuePaint.textAlign = Paint.Align.CENTER
		
		paintEntryLabels = Paint(Paint.ANTI_ALIAS_FLAG)
		paintEntryLabels.color = Color.WHITE
		paintEntryLabels.textAlign = Paint.Align.CENTER
		paintEntryLabels.textSize = Utils.convertDpToPixel(13f)
		
		valueLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
		valueLinePaint.style = Paint.Style.STROKE
	}
	
	override fun initBuffers() {
	
	}
	
	override fun drawData(canvas: Canvas) {
		
		val pieData = pieChart.data
		
		for (set in pieData.dataSets) {
			
			if (set.isVisible && set.entryCount > 0) drawDataSet(canvas, set)
		}
	}
	
	private fun calculateMinimumRadiusForSpacedSlice(
		center: MPPointF,
		radius: Float,
		angle: Float,
		arcStartPointX: Float,
		arcStartPointY: Float,
		startAngle: Float,
		sweepAngle: Float
	): Float {
		val angleMiddle = startAngle + sweepAngle / 2f
		
		// Other point of the arc
		val arcEndPointX = center.x + radius * Math.cos(((startAngle + sweepAngle) * Utils.FDEG2RAD).toDouble()).toFloat()
		val arcEndPointY = center.y + radius * Math.sin(((startAngle + sweepAngle) * Utils.FDEG2RAD).toDouble()).toFloat()
		
		// Middle point on the arc
		val arcMidPointX = center.x + radius * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
		val arcMidPointY = center.y + radius * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
		
		// This is the base of the contained triangle
		val basePointsDistance = Math.sqrt(
			Math.pow((arcEndPointX - arcStartPointX).toDouble(), 2.0)
				+ Math.pow((arcEndPointY - arcStartPointY).toDouble(), 2.0)
		)
		
		// After reducing space from both sides of the "slice",
		// the angle of the contained triangle should stay the same.
		// So let's find out the height of that triangle.
		val containedTriangleHeight = (basePointsDistance / 2.0 * Math.tan((180.0 - angle) / 2.0 * Utils.DEG2RAD)).toFloat()
		
		// Now we subtract that from the radius
		var spacedRadius = radius - containedTriangleHeight
		
		// And now subtract the height of the arc that's between the triangle and the outer circle
		spacedRadius -= Math.sqrt(
			Math.pow((arcMidPointX - (arcEndPointX + arcStartPointX) / 2f).toDouble(), 2.0)
				+ Math.pow((arcMidPointY - (arcEndPointY + arcStartPointY) / 2f).toDouble(), 2.0)
		).toFloat()
		
		return spacedRadius
	}
	
	/**
	 * Calculates the sliceSpace to use based on visible values and their size compared to the set sliceSpace.
	 *
	 * @param dataSet
	 * @return
	 */
	private fun getSliceSpace(dataSet: IPieDataSet): Float {
		
		if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled) return dataSet.sliceSpace
		
		val spaceSizeRatio = dataSet.sliceSpace / mViewPortHandler.smallestContentExtension
		val minValueRatio = dataSet.yMin / pieChart.data.yValueSum * 2
		
		return if (spaceSizeRatio > minValueRatio) 0f else dataSet.sliceSpace
	}
	
	private fun drawDataSet(canvas: Canvas, dataSet: IPieDataSet) {
		var angle = 0f
		val rotationAngle = pieChart.rotationAngle
		
		val phaseX = mAnimator.phaseX
		val phaseY = mAnimator.phaseY
		
		val circleBox = pieChart.circleBox
		
		val entryCount = dataSet.entryCount
		val drawAngles = pieChart.drawAngles
		val center = pieChart.centerCircleBox
		val radius = pieChart.radius
		val drawInnerArc = pieChart.isDrawHoleEnabled && !pieChart.isDrawSlicesUnderHoleEnabled
		val userInnerRadius = if (drawInnerArc) radius * (pieChart.holeRadius / 100f)
		else 0f
		
		var visibleAngleCount = 0
		for (entryIndex in 0 until entryCount) {
			// draw only if the value is greater than zero
			if (Math.abs(dataSet.getEntryForIndex(entryIndex).y) > Utils.FLOAT_EPSILON) {
				visibleAngleCount++
			}
		}
		
		val sliceSpace = if (visibleAngleCount <= 1) 0f else getSliceSpace(dataSet)
		
		for (entryIndex in 0 until entryCount) {
			
			val sliceAngle = drawAngles[entryIndex]
			var innerRadius = userInnerRadius
			
			val pieEntry = dataSet.getEntryForIndex(entryIndex)
			
			// draw only if the value is greater than zero
			if (Math.abs(pieEntry.y) > Utils.FLOAT_EPSILON) {
				
				if (!pieChart.needsHighlight(entryIndex)) {
					
					val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f
					
					mRenderPaint.color = dataSet.getColor(entryIndex)
					
					val sliceSpaceAngleOuter = if (visibleAngleCount == 1) 0f
					else sliceSpace / (Utils.FDEG2RAD * radius)
					val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
					var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
					if (sweepAngleOuter < 0f) {
						sweepAngleOuter = 0f
					}
					
					pathBuffer.reset()
					
					val arcStartPointX = center.x + radius * Math.cos((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat()
					val arcStartPointY = center.y + radius * Math.sin((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat()
					
					if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
						// Android is doing "mod 360"
						pathBuffer.addCircle(
							center.x,
							center.y,
							radius,
							Path.Direction.CW
						)
					} else {
						pathBuffer.moveTo(
							arcStartPointX,
							arcStartPointY
						)
						
						pathBuffer.arcTo(
							circleBox,
							startAngleOuter,
							sweepAngleOuter
						)
					}
					
					// API < 21 does not receive floats in addArc, but a RectF
					innerRectBuffer.set(
						center.x - innerRadius,
						center.y - innerRadius,
						center.x + innerRadius,
						center.y + innerRadius
					)
					
					if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
						if (accountForSliceSpacing) {
							var minSpacedRadius = calculateMinimumRadiusForSpacedSlice(
								center,
								radius,
								sliceAngle * phaseY,
								arcStartPointX,
								arcStartPointY,
								startAngleOuter,
								sweepAngleOuter
							)
							
							if (minSpacedRadius < 0f) minSpacedRadius = -minSpacedRadius
							
							innerRadius = Math.max(innerRadius, minSpacedRadius)
						}
						
						val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f) 0f
						else sliceSpace / (Utils.FDEG2RAD * innerRadius)
						val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
						var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
						if (sweepAngleInner < 0f) {
							sweepAngleInner = 0f
						}
						val endAngleInner = startAngleInner + sweepAngleInner
						
						if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
							// Android is doing "mod 360"
							pathBuffer.addCircle(
								center.x,
								center.y,
								innerRadius,
								Path.Direction.CCW
							)
						} else {
							
							pathBuffer.lineTo(
								center.x + innerRadius * Math.cos((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat(),
								center.y + innerRadius * Math.sin((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat()
							)
							
							pathBuffer.arcTo(
								innerRectBuffer,
								endAngleInner,
								-sweepAngleInner
							)
						}
					} else {
						
						if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
							if (accountForSliceSpacing) {
								
								val angleMiddle = startAngleOuter + sweepAngleOuter / 2f
								
								val sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(
									center,
									radius,
									sliceAngle * phaseY,
									arcStartPointX,
									arcStartPointY,
									startAngleOuter,
									sweepAngleOuter
								)
								
								val arcEndPointX = center.x + sliceSpaceOffset * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
								val arcEndPointY = center.y + sliceSpaceOffset * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
								
								pathBuffer.lineTo(arcEndPointX, arcEndPointY)
								
							} else {
								pathBuffer.lineTo(center.x, center.y)
							}
						}
						
					}
					
					pathBuffer.close()
					
					canvas.drawPath(pathBuffer, mRenderPaint)
				}
			}
			
			angle += sliceAngle * phaseX
		}
		
		MPPointF.recycleInstance(center)
	}
	
	override fun drawValues(canvas: Canvas) {
		
		val center = pieChart.centerCircleBox
		
		// get whole the radius
		val radius = pieChart.radius
		val rotationAngle = pieChart.rotationAngle
		val drawAngles = pieChart.drawAngles
		val absoluteAngles = pieChart.absoluteAngles
		
		val phaseX = mAnimator.phaseX
		val phaseY = mAnimator.phaseY
		
		val holeRadiusPercent = pieChart.holeRadius / 100f
		var labelRadiusOffset = radius / 10f * 3.6f
		
		if (pieChart.isDrawHoleEnabled) {
			labelRadiusOffset = (radius - radius * holeRadiusPercent) / 2f
		}
		
		val labelRadius = radius - labelRadiusOffset
		
		val data = pieChart.data
		val dataSets = data.dataSets
		
		val yValueSum = data.yValueSum
		
		val drawEntryLabels = pieChart.isDrawEntryLabelsEnabled
		
		var angle: Float
		
		canvas.save()
		
		val offset = Utils.convertDpToPixel(5f)
		
		for (dataSetIndex in dataSets.indices) {
			
			val dataSet = dataSets[dataSetIndex]
			
			val drawValues = dataSet.isDrawValuesEnabled
			
			if (!drawValues && !drawEntryLabels) continue
			
			val xValuePosition = dataSet.xValuePosition
			val yValuePosition = dataSet.yValuePosition
			
			// apply the text-styling defined by the DataSet
			applyValueTextStyle(dataSet)
			
			val lineHeight = Utils.calcTextHeight(
				mValuePaint,
				"Q"
			) + Utils.convertDpToPixel(4f)
			
			val formatter = dataSet.valueFormatter
			
			val entryCount = dataSet.entryCount
			
			valueLinePaint.strokeWidth = Utils.convertDpToPixel(dataSet.valueLineWidth)
			
			val sliceSpace = getSliceSpace(dataSet)
			
			val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
			iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
			iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)
			
			for (entryIndex in 0 until entryCount) {
				
				valueLinePaint.color = dataSet.colors[entryIndex % entryCount]
				
				val entry = dataSet.getEntryForIndex(entryIndex)
				
				angle = if (entryIndex == 0) 0f
				else {
					absoluteAngles[entryIndex - 1] * phaseX
				}
				
				val sliceAngle = drawAngles[entryIndex]
				val sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * labelRadius)
				
				// offset needed to center the drawn text in the slice
				val angleOffset = (sliceAngle - sliceSpaceMiddleAngle / 2f) / 2f
				
				angle += angleOffset
				
				val transformedAngle = rotationAngle + angle * phaseY
				
				val value = if (pieChart.isUsePercentValuesEnabled) entry.y / yValueSum * 100f
				else entry.y
				
				val sliceXBase = Math.cos((transformedAngle * Utils.FDEG2RAD).toDouble()).toFloat()
				val sliceYBase = Math.sin((transformedAngle * Utils.FDEG2RAD).toDouble()).toFloat()
				
				val drawXOutside = drawEntryLabels && xValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE
				val drawYOutside = drawValues && yValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE
				val drawXInside = drawEntryLabels && xValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE
				val drawYInside = drawValues && yValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE
				
				if (drawXOutside || drawYOutside) {
					
					val valueLineLength1 = dataSet.valueLinePart1Length
					val valueLineLength2 = dataSet.valueLinePart2Length
					val valueLinePart1OffsetPercentage = dataSet.valueLinePart1OffsetPercentage / 100f
					
					val positionEndx: Float
					val positionEndy: Float
					val labelPositionx: Float
					val labelPositiony: Float
					
					val line1Radius: Float
					
					line1Radius = if (pieChart.isDrawHoleEnabled) (radius - radius * holeRadiusPercent) * valueLinePart1OffsetPercentage + radius * holeRadiusPercent
					else radius * valueLinePart1OffsetPercentage
					
					val polyline2Width = if (dataSet.isValueLineVariableLength) labelRadius * valueLineLength2 * Math.abs(
						Math.sin((transformedAngle * Utils.FDEG2RAD).toDouble())
					).toFloat()
					else labelRadius * valueLineLength2
					
					val positionStartx = line1Radius * sliceXBase + center.x
					val positionStarty = line1Radius * sliceYBase + center.y
					
					val positionCenterx = labelRadius * (1 + valueLineLength1) * sliceXBase + center.x
					val positionCentery = labelRadius * (1 + valueLineLength1) * sliceYBase + center.y
					
					if (transformedAngle % 360.0 in 90.0..270.0) {
						positionEndx = positionCenterx - polyline2Width
						positionEndy = positionCentery
						
						mValuePaint.textAlign = Paint.Align.RIGHT
						
						if (drawXOutside) paintEntryLabels.textAlign = Paint.Align.RIGHT
						
						labelPositionx = positionEndx - offset
						labelPositiony = positionEndy
					} else {
						positionEndx = positionCenterx + polyline2Width
						positionEndy = positionCentery
						mValuePaint.textAlign = Paint.Align.LEFT
						
						if (drawXOutside) paintEntryLabels.textAlign = Paint.Align.LEFT
						
						labelPositionx = positionEndx + offset
						labelPositiony = positionEndy
					}
					
					if (dataSet.valueLineColor != ColorTemplate.COLOR_NONE) {
						canvas.drawLine(
							positionStartx,
							positionStarty,
							positionCenterx,
							positionCentery,
							valueLinePaint
						)
						canvas.drawLine(
							positionCenterx,
							positionCentery,
							positionEndx,
							positionEndy,
							valueLinePaint
						)
					}
					
					// draw everything, depending on settings
					if (drawXOutside && drawYOutside) {
						
						drawValue(
							canvas,
							formatter,
							value,
							entry,
							0,
							labelPositionx,
							labelPositiony,
							dataSet.getValueTextColor(entryIndex)
						)
						
						if (entryIndex < data.entryCount && entry.label != null) {
							drawEntryLabel(
								canvas,
								entry.label,
								labelPositionx,
								labelPositiony + lineHeight
							)
						}
						
					} else if (drawXOutside) {
						if (entryIndex < data.entryCount && entry.label != null) {
							drawEntryLabel(
								canvas,
								entry.label,
								labelPositionx,
								labelPositiony + lineHeight / 2f
							)
						}
					} else if (drawYOutside) {
						
						drawValue(
							canvas,
							formatter,
							value,
							entry,
							0,
							labelPositionx,
							labelPositiony + lineHeight / 2f,
							dataSet.getValueTextColor(entryIndex)
						)
					}
				}
				
				if (drawXInside || drawYInside) {
					// calculate the text position
					val x = labelRadius * sliceXBase + center.x
					val y = labelRadius * sliceYBase + center.y
					
					mValuePaint.textAlign = Paint.Align.CENTER
					
					// draw everything, depending on settings
					if (drawXInside && drawYInside) {
						
						drawValue(
							canvas,
							formatter,
							value,
							entry,
							0,
							x,
							y,
							dataSet.getValueTextColor(entryIndex)
						)
						
						if (entryIndex < data.entryCount && entry.label != null) {
							drawEntryLabel(
								canvas,
								entry.label,
								x,
								y + lineHeight
							)
						}
						
					} else if (drawXInside) {
						if (entryIndex < data.entryCount && entry.label != null) {
							drawEntryLabel(
								canvas,
								entry.label,
								x,
								y + lineHeight / 2f
							)
						}
					} else if (drawYInside) {
						
						drawValue(
							canvas,
							formatter,
							value,
							entry,
							0,
							x,
							y + lineHeight / 2f,
							dataSet.getValueTextColor(entryIndex)
						)
					}
				}
				
				if (entry.icon != null && dataSet.isDrawIconsEnabled) {
					
					val icon = entry.icon
					
					val x = (labelRadius + iconsOffset.y) * sliceXBase + center.x
					var y = (labelRadius + iconsOffset.y) * sliceYBase + center.y
					y += iconsOffset.x
					
					Utils.drawImage(
						canvas,
						icon,
						x.toInt(),
						y.toInt(),
						icon.intrinsicWidth,
						icon.intrinsicHeight
					)
				}
				
			}
			
			MPPointF.recycleInstance(iconsOffset)
		}
		MPPointF.recycleInstance(center)
		canvas.restore()
	}
	
	/**
	 * Draws an entry label at the specified position.
	 *
	 * @param canvas
	 * @param label
	 * @param x
	 * @param y
	 */
	private fun drawEntryLabel(
		canvas: Canvas,
		label: String,
		x: Float,
		y: Float
	) {
		canvas.drawText(
			label,
			x,
			y,
			paintEntryLabels
		)
	}
	
	override fun drawExtras(canvas: Canvas) {
		drawHole(canvas)
		drawCenterText(canvas)
	}
	
	/**
	 * draws the hole in the center of the chart and the transparent circle /
	 * hole
	 */
	private fun drawHole(canvas: Canvas) {
		
		if (pieChart.isDrawHoleEnabled) {
			
			val radius = pieChart.radius
			val holeRadius = radius * (pieChart.holeRadius / 100)
			val center = pieChart.centerCircleBox
			
			if (Color.alpha(paintHole.color) > 0) {
				// draw the hole-circle
				canvas.drawCircle(
					center.x,
					center.y,
					holeRadius,
					paintHole
				)
			}
			
			// only draw the circle if it can be seen (not covered by the hole)
			if (Color.alpha(paintTransparentCircle.color) > 0 && pieChart.transparentCircleRadius > pieChart.holeRadius) {
				
				val alpha = paintTransparentCircle.alpha
				val secondHoleRadius = radius * (pieChart.transparentCircleRadius / 100)
				
				paintTransparentCircle.alpha = (alpha.toFloat() * mAnimator.phaseX * mAnimator.phaseY).toInt()
				
				// draw the transparent-circle
				holeCirclePath.reset()
				holeCirclePath.addCircle(
					center.x,
					center.y,
					secondHoleRadius,
					Path.Direction.CW
				)
				holeCirclePath.addCircle(
					center.x,
					center.y,
					holeRadius,
					Path.Direction.CCW
				)
				canvas.drawPath(
					holeCirclePath,
					paintTransparentCircle
				)
				
				// reset alpha
				paintTransparentCircle.alpha = alpha
			}
			MPPointF.recycleInstance(center)
		}
	}
	
	/**
	 * draws the description text in the center of the pie chart makes most
	 * sense when center-hole is enabled
	 */
	private fun drawCenterText(canvas: Canvas) {
		
		val centerText = pieChart.centerText
		
		if (pieChart.isDrawCenterTextEnabled && centerText != null) {
			
			val center = pieChart.centerCircleBox
			val offset = pieChart.centerTextOffset
			
			val x = center.x + offset.x
			val y = center.y + offset.y
			
			val innerRadius = if (pieChart.isDrawHoleEnabled && !pieChart.isDrawSlicesUnderHoleEnabled) pieChart.radius * (pieChart.holeRadius / 100f)
			else pieChart.radius
			
			val holeRect = rectBuffer[0]
			holeRect.left = x - innerRadius
			holeRect.top = y - innerRadius
			holeRect.right = x + innerRadius
			holeRect.bottom = y + innerRadius
			val boundingRect = rectBuffer[1]
			boundingRect.set(holeRect)
			
			val radiusPercent = pieChart.centerTextRadiusPercent / 100f
			if (radiusPercent > 0.0) {
				boundingRect.inset(
					(boundingRect.width() - boundingRect.width() * radiusPercent) / 2f,
					(boundingRect.height() - boundingRect.height() * radiusPercent) / 2f
				)
			}
			
			if (centerText != centerTextLastValue || boundingRect != centerTextLastBounds) {
				
				// Next time we won't recalculate StaticLayout...
				centerTextLastBounds.set(boundingRect)
				centerTextLastValue = centerText
				
				val width = centerTextLastBounds.width()
				
				// If width is 0, it will crash. Always have a minimum of 1
				centerTextLayout = StaticLayout(
					centerText,
					0,
					centerText.length,
					paintCenterText,
					Math.max(
						Math.ceil(width.toDouble()),
						1.0
					).toInt(),
					Layout.Alignment.ALIGN_CENTER,
					1f,
					0f,
					false
				)
				
			}
			
			//float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
			val layoutHeight = centerTextLayout.height.toFloat()
			
			canvas.save()
			val path = drawCenterTextPathBuffer
			path.reset()
			path.addOval(holeRect, Path.Direction.CW)
			canvas.clipPath(path)
			canvas.translate(
				boundingRect.left,
				boundingRect.top + (boundingRect.height() - layoutHeight) / 2f
			)
			centerTextLayout.draw(canvas)
			
			canvas.restore()
			
			MPPointF.recycleInstance(center)
			MPPointF.recycleInstance(offset)
		}
	}
	
	override fun drawHighlighted(
		canvas: Canvas,
		indices: Array<Highlight>
	) {
		
		val phaseX = mAnimator.phaseX
		val phaseY = mAnimator.phaseY
		
		var angle: Float
		val rotationAngle = pieChart.rotationAngle
		
		val drawAngles = pieChart.drawAngles
		val absoluteAngles = pieChart.absoluteAngles
		val center = pieChart.centerCircleBox
		val radius = pieChart.radius
		val drawInnerArc = pieChart.isDrawHoleEnabled && !pieChart.isDrawSlicesUnderHoleEnabled
		val userInnerRadius = if (drawInnerArc) radius * (pieChart.holeRadius / 100f)
		else 0f
		
		val highlightedCircleBox = drawHighlightedRectF
		highlightedCircleBox.set(
			0f,
			0f,
			0f,
			0f
		)
		
		for (indicesIndex in indices.indices) {
			
			// get the index to highlight
			val index = indices[indicesIndex].x.toInt()
			
			if (index >= drawAngles.size) continue
			
			val set = pieChart.data.getDataSetByIndex(
				indices[indicesIndex].dataSetIndex
			)
			
			if (set == null || !set.isHighlightEnabled) continue
			
			val entryCount = set.entryCount
			var visibleAngleCount = 0
			for (entryIndex in 0 until entryCount) {
				// draw only if the value is greater than zero
				if (Math.abs(set.getEntryForIndex(entryIndex).y) > Utils.FLOAT_EPSILON) {
					visibleAngleCount++
				}
			}
			
			angle = if (index == 0) 0f
			else absoluteAngles[index - 1] * phaseX
			
			val sliceSpace = if (visibleAngleCount <= 1) 0f else set.sliceSpace
			
			val sliceAngle = drawAngles[index]
			var innerRadius = userInnerRadius
			
			val shift = set.selectionShift
			val highlightedRadius = radius + shift
			highlightedCircleBox.set(pieChart.circleBox)
			highlightedCircleBox.inset(
				-shift,
				-shift
			)
			
			val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f
			
			mRenderPaint.color = set.getColor(index)
			
			val sliceSpaceAngleOuter = if (visibleAngleCount == 1) 0f
			else sliceSpace / (Utils.FDEG2RAD * radius)
			
			val sliceSpaceAngleShifted = if (visibleAngleCount == 1) 0f
			else sliceSpace / (Utils.FDEG2RAD * highlightedRadius)
			
			val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
			var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
			if (sweepAngleOuter < 0f) {
				sweepAngleOuter = 0f
			}
			
			val startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted / 2f) * phaseY
			var sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY
			if (sweepAngleShifted < 0f) {
				sweepAngleShifted = 0f
			}
			
			pathBuffer.reset()
			
			if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
				// Android is doing "mod 360"
				pathBuffer.addCircle(
					center.x,
					center.y,
					highlightedRadius,
					Path.Direction.CW
				)
			} else {
				
				pathBuffer.moveTo(
					center.x + highlightedRadius * Math.cos((startAngleShifted * Utils.FDEG2RAD).toDouble()).toFloat(),
					center.y + highlightedRadius * Math.sin((startAngleShifted * Utils.FDEG2RAD).toDouble()).toFloat()
				)
				
				pathBuffer.arcTo(
					highlightedCircleBox,
					startAngleShifted,
					sweepAngleShifted
				)
			}
			
			var sliceSpaceRadius = 0f
			if (accountForSliceSpacing) {
				sliceSpaceRadius = calculateMinimumRadiusForSpacedSlice(
					center,
					radius,
					sliceAngle * phaseY,
					center.x + radius * Math.cos((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat(),
					center.y + radius * Math.sin((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat(),
					startAngleOuter,
					sweepAngleOuter
				)
			}
			
			// API < 21 does not receive floats in addArc, but a RectF
			innerRectBuffer.set(
				center.x - innerRadius,
				center.y - innerRadius,
				center.x + innerRadius,
				center.y + innerRadius
			)
			
			if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
				
				if (accountForSliceSpacing) {
					var minSpacedRadius = sliceSpaceRadius
					
					if (minSpacedRadius < 0f) minSpacedRadius = -minSpacedRadius
					
					innerRadius = Math.max(
						innerRadius,
						minSpacedRadius
					)
				}
				
				val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f) 0f
				else sliceSpace / (Utils.FDEG2RAD * innerRadius)
				val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
				var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
				if (sweepAngleInner < 0f) {
					sweepAngleInner = 0f
				}
				val endAngleInner = startAngleInner + sweepAngleInner
				
				if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
					// Android is doing "mod 360"
					pathBuffer.addCircle(
						center.x,
						center.y,
						innerRadius,
						Path.Direction.CCW
					)
				} else {
					
					pathBuffer.lineTo(
						center.x + innerRadius * Math.cos((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat(),
						center.y + innerRadius * Math.sin((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat()
					)
					
					pathBuffer.arcTo(
						innerRectBuffer,
						endAngleInner,
						-sweepAngleInner
					)
				}
			} else {
				
				if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
					
					if (accountForSliceSpacing) {
						val angleMiddle = startAngleOuter + sweepAngleOuter / 2f
						
						val arcEndPointX = center.x + sliceSpaceRadius * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
						val arcEndPointY = center.y + sliceSpaceRadius * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
						
						pathBuffer.lineTo(arcEndPointX, arcEndPointY)
						
					} else {
						
						pathBuffer.lineTo(
							center.x,
							center.y
						)
					}
					
				}
				
			}
			
			pathBuffer.close()
			
			canvas.drawPath(
				pathBuffer,
				mRenderPaint
			)
		}
		
		MPPointF.recycleInstance(center)
	}
	
	/**
	 * This gives all pie-slices a rounded edge.
	 *
	 * @param canvas
	 */
	private fun drawRoundedSlices(canvas: Canvas) {
		
		if (!pieChart.isDrawRoundedSlicesEnabled) return
		
		val dataSet = pieChart.data.dataSet
		
		if (!dataSet.isVisible) return
		
		val phaseX = mAnimator.phaseX
		val phaseY = mAnimator.phaseY
		
		val center = pieChart.centerCircleBox
		val radius = pieChart.radius
		
		// calculate the radius of the "slice-circle"
		val circleRadius = (radius - radius * pieChart.holeRadius / 100f) / 2f
		
		val drawAngles = pieChart.drawAngles
		var angle = pieChart.rotationAngle
		
		for (index in 0 until dataSet.entryCount) {
			
			val sliceAngle = drawAngles[index]
			
			val entry = dataSet.getEntryForIndex(index)
			
			// draw only if the value is greater than zero
			if (Math.abs(entry.y) > Utils.FLOAT_EPSILON) {
				
				val x = ((radius - circleRadius) * Math.cos(Math.toRadians(((angle + sliceAngle) * phaseY).toDouble())) + center.x).toFloat()
				val y = ((radius - circleRadius) * Math.sin(Math.toRadians(((angle + sliceAngle) * phaseY).toDouble())) + center.y).toFloat()
				
				mRenderPaint.color = dataSet.getColor(index)
				canvas.drawCircle(
					x,
					y,
					circleRadius,
					mRenderPaint
				)
			}
			
			angle += sliceAngle * phaseX
		}
		MPPointF.recycleInstance(center)
	}
	
}