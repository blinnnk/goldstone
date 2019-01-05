package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.data.Entry
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.TokenDetailSize
import io.goldstone.blinnnk.module.home.quotation.quotation.model.ChartPoint
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */
class TokenDetailHeaderView(context: Context) : RelativeLayout(context) {

	private val lineChart = TokenDetailHeaderLineChart(context)

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, TokenDetailSize.headerHeight)
		lineChart.apply {
			layoutParams = RelativeLayout.LayoutParams(matchParent, TokenDetailSize.headerHeight)
			lineChart.setChartColorAndShadowResource(Spectrum.blue, R.drawable.fade_green)
		}.into(this)
		lineChart.setMargins<RelativeLayout.LayoutParams> { margin = 10.uiPX() }
	}

	fun setCharData(data: ArrayList<ChartPoint>) {
		lineChart.resetDataWithTargetLabelCount(
			data.mapIndexed { index, chartPoint ->
				Entry(index.toFloat(), chartPoint.value, chartPoint.label)
			}
		)
	}
}