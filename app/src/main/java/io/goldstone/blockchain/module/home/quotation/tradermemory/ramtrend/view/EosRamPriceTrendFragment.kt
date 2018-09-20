package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view

import android.graphics.Color
import android.support.v4.app.Fragment
import android.text.format.DateUtils
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EosRamChartType
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter.EosRamPriceTrendPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EosRamPriceTrendFragment : BaseFragment<EosRamPriceTrendPresenter>() {
	
	private val candleChart by lazy { EosRamPriceTrendCandleChart(context!!) }
	private val menu by lazy { ButtonMenu(context!!) }
	
	override val presenter: EosRamPriceTrendPresenter = EosRamPriceTrendPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		menu.apply {
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 15.uiPX()
			}
		}
		menu.titles = arrayListOf(
			EosRamChartType.MINUTE.display,
			EosRamChartType.Hour.display,
			EosRamChartType.DAY.display
		)
		menu.getButton { button ->
			button.onClick {
				val dateType = when(button.id) {
					EosRamChartType.MINUTE.code -> DateUtils.FORMAT_SHOW_TIME
					EosRamChartType.Hour.code -> DateUtils.FORMAT_SHOW_TIME
					EosRamChartType.DAY.code -> DateUtils.FORMAT_SHOW_DATE
					else -> DateUtils.FORMAT_SHOW_TIME
				}
				
				val period = when(button.id) {
					EosRamChartType.MINUTE.code -> EosRamChartType.MINUTE.info
					EosRamChartType.Hour.code -> EosRamChartType.Hour.info
					EosRamChartType.DAY.code -> EosRamChartType.DAY.info
					else -> EosRamChartType.MINUTE.info
				}
				
				presenter.updateCandleData(candleChart, period,  dateType)
				menu.selected(button.id)
				button.preventDuplicateClicks()
			}
		}
		menu.selected(EosRamChartType.MINUTE.code)
		verticalLayout {
			addView(menu)
			addView(candleChart)
			presenter.updateCandleData(candleChart, EosRamChartType.MINUTE.info, DateUtils.FORMAT_SHOW_TIME)
		}
	}
}