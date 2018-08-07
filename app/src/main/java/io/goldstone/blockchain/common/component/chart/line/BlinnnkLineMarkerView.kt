package example.cat.com.candlechartdemo.ktd.line

import android.content.Context
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import example.cat.com.candlechartdemo.ktd.BlinnnkBaseMarkerView

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表详情这是marker
 */
class BlinnnkLineMarkerView(context: Context) : BlinnnkBaseMarkerView(context) {
  
  override fun refreshContent(e: Entry, highlight: Highlight) {
    
    textViewContent.text =  "收盘：" + e.y
    
    super.refreshContent(e, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }
}
