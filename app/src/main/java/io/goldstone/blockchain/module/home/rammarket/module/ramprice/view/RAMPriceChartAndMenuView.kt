package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.common.component.overlay.TopMiniLoadingView
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
@SuppressLint("ViewConstructor")
/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description:
 */
class RAMPriceChartAndMenuView(
	context: Context,
	private val hold: (EOSRAMChartType) -> Unit
): LinearLayout(context) {
	
	val candleChart = EOSRAMPriceCandleChart(context)
	
	private val menu = ButtonMenu(context).apply {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - RAMMarketPadding * 2, 32.uiPX())
		}
	
	
	private val loadingView by lazy {
		TopMiniLoadingView(context)
	}
	
	init {
	  orientation = LinearLayout.VERTICAL
		menu.setMargins<LinearLayout.LayoutParams> {
			topMargin = 16.uiPX()
			leftMargin = RAMMarketPadding
			rightMargin = RAMMarketPadding
		}
		
		menu.titles = arrayListOf(
			EOSRAMChartType.Minute.display,
			EOSRAMChartType.Hour.display,
			EOSRAMChartType.Day.display
		)
		menu.getButton { button ->
			button.onClick {
				updateCurrentData(button.id)
				menu.selected(button.id)
				button.preventDuplicateClicks()
			}
		}
		menu.selected(EOSRAMChartType.Minute.code)
		
		addView(menu)
		relativeLayout {
			gravity = Gravity.CENTER
			addView(candleChart)
			addView(loadingView.apply {
				setMargins<RelativeLayout.LayoutParams> {
					topMargin = 100.uiPX()
				}
			})
			removeLoadingView()
		}
	}
	
	fun showLoadingView() {
		loadingView.visibility = View.VISIBLE
	}
	
	fun removeLoadingView() {
		loadingView.visibility = View.GONE
	}
	
	private fun updateCurrentData(buttonId: Int){
		hold(when(buttonId) {
			EOSRAMChartType.Minute.code -> EOSRAMChartType.Minute
			EOSRAMChartType.Hour.code -> EOSRAMChartType.Hour
			EOSRAMChartType.Day.code -> EOSRAMChartType.Day
			else -> EOSRAMChartType.Minute
		})
	}
}