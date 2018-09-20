package io.goldstone.blockchain.common.component.chart.pie


import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.PieRadarHighlighter

/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */
class PieHighlighter(chart: PieChart) : PieRadarHighlighter<PieChart>(chart) {
  
  override fun getClosestHighlight(
    index: Int,
    x: Float,
    y: Float
  ): Highlight {
    
    val set = mChart.data.dataSet
    
    val entry = set.getEntryForIndex(index)
    
    return Highlight(
      index.toFloat(),
      entry.y,
      x,
      y,
      0,
      set.axisDependency
    )
  }
}