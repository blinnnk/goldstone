package io.goldstone.blinnnk.common.component.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 绘制横轴 gridLine 的 render
 */
class XAxisRenderer(
  viewPortHandler: ViewPortHandler,
  xAxis: XAxis,
  trans: Transformer
) : XAxisRenderer(viewPortHandler, xAxis, trans) {
	
  override fun renderAxisLine(canvas: Canvas) {
    super.renderAxisLine(canvas)
		
		val topLinePaint = Paint()
		topLinePaint.color = Color.rgb(236, 236, 236)
		
    canvas.drawLine(mViewPortHandler.contentLeft(),
      mViewPortHandler.contentTop(),
      mViewPortHandler.contentRight(),
      mViewPortHandler.contentTop(),
			topLinePaint)
  }
}
