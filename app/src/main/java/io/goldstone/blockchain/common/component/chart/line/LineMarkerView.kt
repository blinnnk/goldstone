package io.goldstone.blockchain.common.component.chart.line

import android.content.Context
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.goldstone.blockchain.common.component.chart.BaseMarkerView
import io.goldstone.blockchain.common.language.WalletSettingsText

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表详情这是marker
 */
abstract class LineMarkerView(context: Context) : BaseMarkerView(context) {
  
  override fun refreshContent(entry: Entry, highlight: Highlight) {
    
    textViewContent.text = WalletSettingsText.balance + ":" + entry.y
    
    super.refreshContent(entry, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }
}
