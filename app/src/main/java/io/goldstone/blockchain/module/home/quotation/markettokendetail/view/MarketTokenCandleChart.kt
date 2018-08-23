package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.os.Handler
import android.text.format.DateUtils
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.highlight.Highlight
import io.goldstone.blockchain.common.component.chart.candle.CandleStickChart
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.ScreenSize

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description: 详情的蜡烛图
 */
class MarketTokenCandleChart : CandleStickChart {
	
	constructor(context: Context) : super(context)
	
	private val hightValueHandler by lazy {
		Handler()
	}
	
	private val hightValueRunnable by lazy {
		Runnable {
			mIndicesToHighlight = null
			invalidate()
		}
	}
	
	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 250.uiPX())
	}
	
	
	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		requestDisallowInterceptTouchEvent(true)
		return super.dispatchTouchEvent(ev)
	}
	
	override fun highlightValue(
		high: Highlight?,
		callListener: Boolean
	) {
		hightValueHandler.removeCallbacks(hightValueRunnable)
		super.highlightValue(high, callListener)
		hightValueHandler.postDelayed(hightValueRunnable,1000 * 2)
	}
	
	
	override fun formateDateByType(date: Long): String {
		val formatDateString: String
		when(dateType) {
			DateUtils.FORMAT_SHOW_TIME -> formatDateString = TimeUtils.formatMdHmDate(date)
			else -> formatDateString = TimeUtils.formatMdDate(date)
		}
		return formatDateString
		
	}
	
}