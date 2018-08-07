package io.goldstone.blockchain.common.component.chart.line

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表的左侧Y轴数据展示
 */
class LineYValueFormatter : IValueFormatter {
  override fun getFormattedValue(value: Float,
    entry: Entry,
    dataSetIndex: Int,
    viewPortHandler: ViewPortHandler
  ): String {
    val entryBean = entry.data as Entry
    return entryBean.y.toString()
  }
}
