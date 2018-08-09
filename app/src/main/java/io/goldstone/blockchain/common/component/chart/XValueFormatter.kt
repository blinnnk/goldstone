package io.goldstone.blockchain.common.component.chart

import com.blinnnk.extension.isNull
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import io.goldstone.blockchain.common.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: x轴的坐标展示的时间字符串转换器
 */

class XValueFormatter(private val chart: BarLineChartBase<*>) : IAxisValueFormatter {
	
  override fun getFormattedValue(value: Float, axis: AxisBase): String {
    val position = value.toInt()
    if (chart.data.isNull()
      || chart.data.getDataSetByIndex(0).isNull()) {
      return ""
    }
		
		
    var values = (this.chart.data.getDataSetByIndex(0) as DataSet<*>).values
    if (position >= values.size) return ""
    val entry = values[position]
		if ((entry.data is Long)) {
			if (entry.data == 0) return ""
			return TimeUtils.formatMdDate(entry.data as Long)
		}
		if (entry.data is String) {
			if ((entry.data as String).isEmpty()) return ""
			return TimeUtils.formatMdDate((entry.data as String).toLong())
		}
		
		return ""
  
  
  
  }
  
  
}

