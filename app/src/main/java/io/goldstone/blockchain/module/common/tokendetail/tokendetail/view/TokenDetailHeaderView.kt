package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.LineChart
import io.goldstone.blockchain.common.value.TokenDetailSize
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */
class TokenDetailHeaderView(context: Context) : RelativeLayout(context) {
	
	private val chartView = object : LineChart(context) {
		override fun canClickPoint() = true
		override fun setChartStyle() = LineChart.Companion.Style.PointStyle
		override fun hasAnimation() = true
	}
	
	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, TokenDetailSize.headerHeight)
		chartView.apply {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width - 20.uiPX(), matchParent)
			setMargins<RelativeLayout.LayoutParams> { margin = 10.uiPX() }
		}.into(this)
	}
	
	fun setCharData(data: ArrayList<ChartPoint>) {
		chartView.updateData(data)
	}
}