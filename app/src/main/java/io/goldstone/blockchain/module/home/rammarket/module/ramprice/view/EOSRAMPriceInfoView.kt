package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import org.jetbrains.anko.*

/**
 * @date: 2018/9/21.
 * @author: yanglihai
 * @description: 头部价格信息详情
 */
class EOSRAMPriceInfoView(context: Context) : LinearLayout(context) {
	
	var currentPriceView: CurrentPriceView
	var todayPriceView: RAMTodayPriceView
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		currentPriceView = CurrentPriceView(context)
		todayPriceView = RAMTodayPriceView(context)
		addView(currentPriceView)
		addView(todayPriceView)
	}
	
}