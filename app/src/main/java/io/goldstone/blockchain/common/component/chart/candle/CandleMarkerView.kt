package io.goldstone.blockchain.common.component.chart.candle

import android.content.Context
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.goldstone.blockchain.common.component.chart.BaseMarkerView


/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 点击candleChartView的item的时候展示的具体详情
 */

abstract class CandleMarkerView(context: Context) : BaseMarkerView(context) {
  
  override fun refreshContent(entry: Entry, highlight: Highlight) {
    val candleEntry = entry as CandleEntry
    textViewContent.text = "最高：" + entry.high + "\n" + "最低：" + entry.low + "\n" + "开盘：" + entry.open + "\n" + "收盘：" + entry.close
    
    super.refreshContent(candleEntry, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }

}

