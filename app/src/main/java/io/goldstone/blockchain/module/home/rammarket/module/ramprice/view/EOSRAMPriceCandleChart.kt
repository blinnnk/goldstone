package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.content.Context
import android.os.Handler
import android.text.format.DateUtils
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.highlight.Highlight
import io.goldstone.blockchain.common.component.chart.candle.CandleStickChart
import io.goldstone.blockchain.common.utils.TimeUtils

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description: 详情的蜡烛图
 */
class EOSRAMPriceCandleChart(context: Context) : CandleStickChart(context) {
	private val highLightValueHandler by lazy { Handler() }
	private val highLightValueRunnable by lazy {
		Runnable {
			mIndicesToHighlight = null
			invalidate()
		}
	}
	
	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width - 10.uiPX(), 260.uiPX())
	}
	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		requestDisallowInterceptTouchEvent(true)
		return super.dispatchTouchEvent(ev)
	}
	
	override fun highlightValue(high: Highlight?, callListener: Boolean) {
		highLightValueHandler.removeCallbacks(highLightValueRunnable)
		super.highlightValue(high, callListener)
		highLightValueHandler.postDelayed(highLightValueRunnable, 1000 * 2)
	}
	
	override fun formattedDateByType(date: Long): String {
		return when (dateType) {
			DateUtils.FORMAT_SHOW_TIME -> TimeUtils.formatMdHmDate(date)
			else -> TimeUtils.formatMdDate(date)
		}
	}
}