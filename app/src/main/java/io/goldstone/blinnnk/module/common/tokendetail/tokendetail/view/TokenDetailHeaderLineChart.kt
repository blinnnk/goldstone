package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.view

import android.content.Context
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.goldstone.blinnnk.common.component.chart.BaseMarkerView
import io.goldstone.blinnnk.common.component.chart.line.LineChart
import io.goldstone.blinnnk.common.language.WalletSettingsText

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
		dataRows.forEach {
			if (it.y > maxValue) maxValue = it.y
			if (it.y < minValue) minValue = it.y
		}
		if (maxValue == 0f && minValue == 0f) {
			//空数据给的默认格式
			axisLeft.axisMinimum = 0f
			axisLeft.axisMaximum = 5f
			dataRows.forEach {
				it.y = 0.1f
			}
			marker = object : BaseMarkerView(context) {
				override fun getChartWidth(): Int = this@TokenDetailHeaderLineChart.width
				override fun getChartHeight(): Int = this@TokenDetailHeaderLineChart.height
				override fun getOffset(): MPPointF = MPPointF((-width / 2).toFloat(), -height.toFloat())
				override fun refreshContent(entry: Entry, highlight: Highlight) {
					textViewContent.text = "${WalletSettingsText.balance}：0"
					super.refreshContent(entry, highlight)
				}
			}
		} else {
			var distance = (maxValue - minValue) / 2f
			if (distance == 0f) {
				distance = maxValue * 0.2f
			}
			axisLeft.axisMinimum = minValue - distance
			axisLeft.axisMaximum = maxValue + distance
			resetMarkerView()
		}
		isScaleXEnabled = false
		isScaleYEnabled = false
		mPinchZoomEnabled = false
		super.resetData(dataRows)
	}
}