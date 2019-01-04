package io.goldstone.blinnnk.module.home.quotation.quotation.view

import android.content.Context
import com.github.mikephil.charting.data.Entry
import io.goldstone.blinnnk.common.component.chart.line.LineChart

/**
 * @date: 2018/8/27.
 * @author: yanglihai
 */
abstract class QuotationLineChart(context: Context) : LineChart(context) {

	/**
	 * @date: 2018/8/27
	 * @author: yanglihai
	 * [dataRows]
	 * @description: 设置数据之前，计算下上下的边距
	 */
	override fun resetData(dataRows: List<Entry>) {
		var maxValue = dataRows.firstOrNull()?.y ?: 0f
		var minValue = dataRows.firstOrNull()?.y ?: 0f
		dataRows.forEach {
			if (it.y > maxValue) {
				maxValue = it.y
			}
			if (it.y < minValue) {
				minValue = it.y
			}
		}
		if (maxValue * minValue == 0f) {
			//空数据给的默认格式
			axisLeft.axisMinimum = 0f
			axisLeft.axisMaximum = 5f
			dataRows.forEach {
				it.y = 0.1f
			}
		} else {
			val distance = (maxValue - minValue) / 5f
			axisLeft.axisMinimum = minValue - distance
			axisLeft.axisMaximum = maxValue + distance
		}

		super.resetData(dataRows)
	}
}