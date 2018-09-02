package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.content.Context
import com.github.mikephil.charting.data.Entry
import io.goldstone.blockchain.common.component.chart.line.LineChart

/**
 * @date: 2018/8/23.
 * @author: yanglihai
 * @description:
 */
class TokenDetailHeaderLineChart(context: Context) : LineChart(context) {

	override fun lineLabelCount(): Int = 8
	override val dragEnable: Boolean = false
	override val touchEnable: Boolean = true
	override val isDrawPoints: Boolean = true
	override val isPerformBezier: Boolean = true
	override val animateEnable: Boolean = true
	override fun resetData(dataRows: List<Entry>) {

		var maxValue = dataRows[0].y
		var minValue = dataRows[0].y
		if (maxValue == minValue) maxValue = minValue * 1.5f
		dataRows.forEach {
			if (it.y > maxValue) maxValue = it.y
			if (it.y < minValue) minValue = it.y
		}
		if (maxValue * minValue == 0f) {
			//空数据给的默认格式
			axisLeft.axisMinimum = 0f
			axisLeft.axisMaximum = 5f
			dataRows.forEach {
				it.y = 0.3f
			}
		} else {
			val distance = (maxValue - minValue) / 2f
			axisLeft.axisMinimum = minValue - distance
			axisLeft.axisMaximum = maxValue + distance
		}
		super.resetData(dataRows)
	}
}