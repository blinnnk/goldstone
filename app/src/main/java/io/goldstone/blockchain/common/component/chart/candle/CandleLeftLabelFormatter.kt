package io.goldstone.blockchain.common.component.chart.candle

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat

/**
 * @date: 2018/8/27.
 * @author: yanglihai
 * @description: 左侧label的显示转换器 小数点后边保留几位
 */
class CandleLeftLabelFormatter(digits: Int) : IAxisValueFormatter {

	private var mFormat: DecimalFormat

	init {
		val stringBuffer = StringBuffer()
		for (index in 0 until digits) {
			if (index == 0) stringBuffer.append(".")
			stringBuffer.append("0")
		}
		mFormat = DecimalFormat("###,###,###,##0" + stringBuffer.toString())
	}

	override fun getFormattedValue(
		value: Float,
		axis: AxisBase?
	): String {
		return mFormat.format(value)
	}
}