package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.chart.candle.CandleStickChart
import io.goldstone.blockchain.common.value.ScreenSize

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description: 详情的蜡烛图
 */
class MarketTokenCandleChart : CandleStickChart {
	constructor(context: Context) : super(context)
	
	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 250.uiPX())
	}
}