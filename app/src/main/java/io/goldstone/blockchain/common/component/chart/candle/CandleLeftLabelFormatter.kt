package io.goldstone.blockchain.common.component.chart.candle

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat

/**
 * @date: 2018/8/27.
 * @author: yanglihai
 * @description: 左侧label的显示转换器
 */
class CandleLeftLabelFormatter : IAxisValueFormatter {
	
	private var mFormat: DecimalFormat
	
	/**
	 * [digits] 小数点后边保留几位
	 */
	constructor(digits: Int) {
		val b = StringBuffer()
		for (i in 0 until digits) {
			if (i == 0) b.append(".")
			b.append("0")
		}
		mFormat = DecimalFormat("###,###,###,##0" + b.toString())
	}
	
	override fun getFormattedValue(
		value: Float,
		axis: AxisBase?
	): String {
		return mFormat.format(value)
	}
}