package example.cat.com.candlechartdemo.ktd


import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: x轴的坐标展示的时间字符串转换器
 */

class BlinnnkXValueFormatter(private val chart: BarLineChartBase<*>) : IAxisValueFormatter {
  
  private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
  
  override fun getFormattedValue(value: Float, axis: AxisBase): String {
    val position = value.toInt()
    if (this.chart.data == null
      || this.chart.data.getDataSetByIndex(0) == null) {
      return ""
    }
		
		
    var values = (this.chart.data.getDataSetByIndex(0) as DataSet<*>).values
    if (position >= values.size) return ""
    val entry = values[position]
    if ((entry.data as Long) == 0.toLong()) return ""
    return simpleDateFormat.format(Date(entry.data as Long))
    
    
  }
  
  
}

