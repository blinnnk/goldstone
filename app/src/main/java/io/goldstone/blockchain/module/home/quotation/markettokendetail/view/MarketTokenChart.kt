package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.LineChart
import io.goldstone.blockchain.common.value.ScreenSize

/**
 * @date 25/04/2018 7:44 AM
 * @author KaySaith
 */
class MarketTokenChart(context: Context) : LineChart(context) {
	
	override fun setChartValueType() = LineChart.Companion.ChartType.MarketTokenDetail
	override fun canClickPoint() = true
	override fun setChartStyle() = LineChart.Companion.Style.LineStyle
	
	override fun hasAnimation() = true
	
	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 150.uiPX())
	}
}