package io.goldstone.blockchain.common.component.chart

import android.graphics.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 绘制横轴gridline的render
 */
class XAxisRenderer(
  viewPortHandler: ViewPortHandler,
  xAxis: XAxis,
  trans: Transformer
) : XAxisRenderer(viewPortHandler, xAxis, trans) {
	
	private val gridlineColor = Color.rgb(236, 236, 236)
	private val topLinePaint = Paint()
	init {
	  topLinePaint.color = gridlineColor
	}
	
  
  override fun renderAxisLine(c: Canvas) {
    super.renderAxisLine(c)
    c.drawLine(mViewPortHandler.contentLeft(),
      mViewPortHandler.contentTop(),
      mViewPortHandler.contentRight(),
      mViewPortHandler.contentTop(),
			topLinePaint)
  }
}
