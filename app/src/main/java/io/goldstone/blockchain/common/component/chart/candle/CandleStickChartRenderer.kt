package io.goldstone.blockchain.common.component.chart.candle

import android.graphics.Canvas
import android.graphics.Paint
import com.blinnnk.extension.isNotNull
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.renderer.LineScatterCandleRadarRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛绘制的逻辑
 */

class CandleStickChartRenderer(
	private var candleDataProvider: CandleDataProvider,
  animator: ChartAnimator,
  viewPortHandler: ViewPortHandler) : LineScatterCandleRadarRenderer(animator, viewPortHandler) {
	
  private val shadowBuffers = FloatArray(8)
  private val bodyBuffers = FloatArray(4)
  private val rangeBuffers = FloatArray(4)
  private val openBuffers = FloatArray(4)
  private val closeBuffers = FloatArray(4)
  
  override fun initBuffers() {
  
  }
  
  override fun drawData(canvas: Canvas) {
    
    val candleData = candleDataProvider.candleData
    
    for (set in candleData.dataSets) {
      
      if (set.isVisible) drawDataSet(canvas, set)
    }
  }
  
  protected fun drawDataSet(
    canvas: Canvas,
    dataSet: ICandleDataSet
  ) {
	
		val candleRectRadius = 3f // 蜡烛矩形的圆角
		
		val transformer = candleDataProvider.getTransformer(dataSet.axisDependency)
    
    val phaseY = mAnimator.phaseY
    val barSpace = dataSet.barSpace
    val showCandleBar = dataSet.showCandleBar
    
    mXBounds.set(candleDataProvider, dataSet)
    
    mRenderPaint.strokeWidth = dataSet.shadowWidth
    
    // draw the body
    for (entryPosition in mXBounds.min .. mXBounds.range + mXBounds.min) {
      
      // get the entry
      val candleEntry = dataSet.getEntryForIndex(entryPosition)
        ?: continue
      
      val xPosition = candleEntry.x
      
      val open = candleEntry.open
      val close = candleEntry.close
      val high = candleEntry.high
      val low = candleEntry.low
      
      if (showCandleBar) {
        // calculate the shadow
        
        shadowBuffers[0] = xPosition
        shadowBuffers[2] = xPosition
        shadowBuffers[4] = xPosition
        shadowBuffers[6] = xPosition
        
        if (open > close) {
          shadowBuffers[1] = high * phaseY
          shadowBuffers[3] = open * phaseY
          shadowBuffers[5] = low * phaseY
          shadowBuffers[7] = close * phaseY
        } else if (open < close) {
          shadowBuffers[1] = high * phaseY
          shadowBuffers[3] = close * phaseY
          shadowBuffers[5] = low * phaseY
          shadowBuffers[7] = open * phaseY
        } else {
          shadowBuffers[1] = high * phaseY
          shadowBuffers[3] = open * phaseY
          shadowBuffers[5] = low * phaseY
          shadowBuffers[7] = shadowBuffers[3]
        }
        
        transformer.pointValuesToPixel(shadowBuffers)
        
        // draw the shadows
        
        if (dataSet.shadowColorSameAsCandle) {
          
          if (open > close) mRenderPaint.color = if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
            entryPosition)
          else dataSet.decreasingColor
          else if (open < close) mRenderPaint.color = if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
            entryPosition)
          else dataSet.increasingColor
          else mRenderPaint.color = if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
            entryPosition)
          else dataSet.neutralColor
          
        } else {
          mRenderPaint.color = if (dataSet.shadowColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
            entryPosition)
          else dataSet.shadowColor
        }
        
        mRenderPaint.style = Paint.Style.STROKE
        
        mRenderPaint.strokeCap = Paint.Cap.ROUND
        
        canvas.drawLines(shadowBuffers, mRenderPaint)
        
        // calculate the body
        
        bodyBuffers[0] = xPosition - 0.5f + barSpace
        bodyBuffers[1] = close * phaseY
        bodyBuffers[2] = xPosition + 0.5f - barSpace
        bodyBuffers[3] = open * phaseY
        
        transformer.pointValuesToPixel(bodyBuffers)
        
        // draw body differently for increasing and decreasing entry
        if (open > close) { // decreasing
          
          if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) {
            mRenderPaint.color = dataSet.getColor(entryPosition)
          } else {
            mRenderPaint.color = dataSet.decreasingColor
          }
          
          mRenderPaint.style = dataSet.decreasingPaintStyle
          
          mRenderPaint.strokeCap = Paint.Cap.SQUARE
          
					// 柱状图，如果是此种情况（开盘价低于收盘价），那么举行的顶部应该用bodyBuffers[3]，底部用 bodyBuffers[1]
          canvas.drawRoundRect(bodyBuffers[0],
            bodyBuffers[3],
            bodyBuffers[2],
            bodyBuffers[1],
            candleRectRadius,
            candleRectRadius,
            mRenderPaint)
          
        } else if (open < close) {
          
          if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) {
            mRenderPaint.color = dataSet.getColor(entryPosition)
          } else {
            mRenderPaint.color = dataSet.increasingColor
          }
          
          mRenderPaint.style = dataSet.increasingPaintStyle
          
          mRenderPaint.strokeCap = Paint.Cap.SQUARE
          
          canvas.drawRoundRect(bodyBuffers[0],
            bodyBuffers[1],
            bodyBuffers[2],
            bodyBuffers[3],
            candleRectRadius,
            candleRectRadius,
            mRenderPaint)
        } else { // equal values
          
          if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) {
            mRenderPaint.color = dataSet.getColor(entryPosition)
          } else {
            mRenderPaint.color = dataSet.neutralColor
          }
          
          canvas.drawLine(bodyBuffers[0],
            bodyBuffers[1],
            bodyBuffers[2],
            bodyBuffers[3],
            mRenderPaint)
        }
      } else {
        
        rangeBuffers[0] = xPosition
        rangeBuffers[1] = high * phaseY
        rangeBuffers[2] = xPosition
        rangeBuffers[3] = low * phaseY
        
        openBuffers[0] = xPosition - 0.5f + barSpace
        openBuffers[1] = open * phaseY
        openBuffers[2] = xPosition
        openBuffers[3] = open * phaseY
        
        closeBuffers[0] = xPosition + 0.5f - barSpace
        closeBuffers[1] = close * phaseY
        closeBuffers[2] = xPosition
        closeBuffers[3] = close * phaseY
        
        transformer.pointValuesToPixel(rangeBuffers)
        transformer.pointValuesToPixel(openBuffers)
        transformer.pointValuesToPixel(closeBuffers)
        
        // draw the ranges
        val barColor: Int
        
        if (open > close) {
					barColor = if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(entryPosition)
					else dataSet.decreasingColor
				} else if (open < close) {
					barColor = if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(entryPosition)
					else dataSet.increasingColor
				} else {
					barColor = if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(entryPosition)
					else dataSet.neutralColor
				}
        
        mRenderPaint.color = barColor
        canvas.drawLine(rangeBuffers[0],
          rangeBuffers[1],
          rangeBuffers[2],
          rangeBuffers[3],
          mRenderPaint)
        canvas.drawLine(openBuffers[0],
					openBuffers[1],
					openBuffers[2],
					openBuffers[3],
					mRenderPaint)
        canvas.drawLine(closeBuffers[0],
          closeBuffers[1],
          closeBuffers[2],
          closeBuffers[3],
          mRenderPaint)
      }
    }
  }
  
  override fun drawValues(canvas: Canvas) {
    
    // if values are drawn
    if (isDrawingValuesAllowed(candleDataProvider)) {
      
      val dataSets = candleDataProvider.candleData.dataSets
      
      for (dataSetPosition in dataSets.indices) {
        
        val dataSet = dataSets[dataSetPosition]
        
        if (!shouldDrawValues(dataSet)) continue
        
        // apply the text-styling defined by the DataSet
        applyValueTextStyle(dataSet)
        
        val trans = candleDataProvider.getTransformer(dataSet.axisDependency)
        
        mXBounds.set(candleDataProvider, dataSet)
        
        val positions = trans.generateTransformedValuesCandle(dataSet,
          mAnimator.phaseX,
          mAnimator.phaseY,
          mXBounds.min,
          mXBounds.max)
        
        val yOffset = Utils.convertDpToPixel(5f)
        
        val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
        iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
        iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)
        
        var index = 0
        while (index < positions.size) {
          
          val x = positions[index]
          val y = positions[index + 1]
          
          if (!mViewPortHandler.isInBoundsRight(x)) break
          
          if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)) {
            index += 2
            continue
          }
          
          val entry = dataSet.getEntryForIndex(index / 2 + mXBounds.min)
          
          if (dataSet.isDrawValuesEnabled) {
            drawValue(canvas,
              dataSet.valueFormatter,
              entry.high,
              entry,
              dataSetPosition,
              x,
              y - yOffset,
              dataSet.getValueTextColor(index / 2))
            drawValue(canvas,
              dataSet.valueFormatter,
              entry.low,
              entry,
              dataSetPosition,
              x,
              y - yOffset,
              dataSet.getValueTextColor(index / 2))
          }
          
          if (entry.icon.isNotNull() && dataSet.isDrawIconsEnabled) {
            
            val icon = entry.icon
            
            Utils.drawImage(canvas,
              icon,
              (x + iconsOffset.x).toInt(),
              (y + iconsOffset.y).toInt(),
              icon.intrinsicWidth,
              icon.intrinsicHeight)
          }
          index += 2
        }
        
        MPPointF.recycleInstance(iconsOffset)
      }
    }
  }
  
  override fun drawExtras(canvas: Canvas) {}
  
  override fun drawHighlighted(
    canvas: Canvas,
    indices: Array<Highlight>
  ) {
    
    val candleData = candleDataProvider.candleData
    
    for (high in indices) {
      
      val dataSet = candleData.getDataSetByIndex(high.dataSetIndex)
      
      if (dataSet == null || !dataSet.isHighlightEnabled) continue
      
      val candleEntry = dataSet.getEntryForXValue(high.x, high.y)
      
      if (!isInBoundsX(candleEntry, dataSet)) continue
      
      val lowValue = candleEntry.low * mAnimator.phaseY
      val highValue = candleEntry.high * mAnimator.phaseY
      val y = (lowValue + highValue) / 2f
      
      val pix = candleDataProvider.getTransformer(dataSet.axisDependency).getPixelForValues(candleEntry.x, y)
      
      high.setDraw(pix.x.toFloat(), pix.y.toFloat())
      
      // draw the lines
      drawHighlightLines(canvas, pix.x.toFloat(), pix.y.toFloat(), dataSet)
    }
  }
  
}

