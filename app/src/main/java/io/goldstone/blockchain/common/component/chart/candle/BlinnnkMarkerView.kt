package example.cat.com.candlechartdemo.ktd.candle

import android.content.Context
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import example.cat.com.candlechartdemo.ktd.BlinnnkBaseMarkerView


/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 点击candleChartView的item的时候展示的具体详情
 */

class BlinnnkMarkerView(context: Context) : BlinnnkBaseMarkerView(context) {
  
  override fun refreshContent(e: Entry, highlight: Highlight) {
    val entry = e as CandleEntry
    textViewContent.text = "最高：" + entry.high + "\n" + "最低：" + entry.low + "\n" + "开盘：" + entry.open + "\n" + "收盘：" + entry.close
    
    super.refreshContent(e, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }

}

