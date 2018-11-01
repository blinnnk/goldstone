package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
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
		topPadding = 20.uiPX()
		currentPriceView = CurrentPriceView(context)
		todayPriceView = RAMTodayPriceView(context)
		addView(currentPriceView)
		addView(todayPriceView)
	}
	
}