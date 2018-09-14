package io.goldstone.blockchain.common.component.chart.candle

import android.content.Context
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.goldstone.blockchain.common.component.chart.BaseMarkerView
import io.goldstone.blockchain.common.language.QuotationText
import java.math.BigDecimal


/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 点击candleChartView的item的时候展示的具体详情
 */

abstract class CandleMarkerView(context: Context) : BaseMarkerView(context) {
  
  override fun refreshContent(entry: Entry, highlight: Highlight) {
    val candleEntry = entry as CandleEntry
    textViewContent.text = QuotationText.candleStickDescription(
			BigDecimal(entry.high.toString()).toPlainString(),
			BigDecimal(entry.low.toString()).toPlainString(),
			BigDecimal(entry.open.toString()).toPlainString(),
			BigDecimal(entry.close.toString()).toPlainString()
		)
    super.refreshContent(candleEntry, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }

}

