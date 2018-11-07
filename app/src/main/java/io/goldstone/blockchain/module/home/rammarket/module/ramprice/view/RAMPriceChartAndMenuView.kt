package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import org.jetbrains.anko.sdk25.coroutines.onClick
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
	
	val candleChart by lazy {
		EOSRAMPriceCandleChart(context).apply {
		}
	}
	private val menu by lazy {
		ButtonMenu(context).apply {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 40.uiPX(), 32.uiPX())
		}
	}
	
	init {
	  orientation = LinearLayout.VERTICAL
		menu.setMargins<LinearLayout.LayoutParams> {
			topMargin = 16.uiPX()
			leftMargin = 20.uiPX()
			rightMargin = 20.uiPX()
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
		addView(candleChart)
	}
	
	private fun updateCurrentData(buttonId: Int){
		hold( when(buttonId) {
			EOSRAMChartType.Minute.code -> EOSRAMChartType.Minute
			EOSRAMChartType.Hour.code -> EOSRAMChartType.Hour
			EOSRAMChartType.Day.code -> EOSRAMChartType.Day
			else -> EOSRAMChartType.Minute
		})
	}
}