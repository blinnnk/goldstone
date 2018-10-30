package io.goldstone.blockchain.module.home.rammarket.ramprice.view

import android.support.v4.app.Fragment
import android.text.format.DateUtils
import android.widget.LinearLayout
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EOSRAMPriceTrendCandleChart
import io.goldstone.blockchain.module.home.rammarket.ramprice.presenter.RAMPricePresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: price信息，包含蜡烛走势图
 */
class RAMPriceDetailFragment : BaseFragment<RAMPricePresenter>() {
	override val pageTitle: String = EOSRAMText.ramTradeRoom
	
	val candleChart by lazy { EOSRAMPriceTrendCandleChart(context!!) }
	private val menu by lazy { ButtonMenu(context!!) }
	
	val ramInformationHeader by lazy { RAMPriceDetailView(context!!) }
	
	override val presenter: RAMPricePresenter = RAMPricePresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		menu.apply {
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 15.uiPX()
			}
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
		verticalLayout {
			addView(ramInformationHeader)
			addView(menu)
			addView(candleChart)
			presenter.updateRAMCandleData(EOSRAMChartType.Minute.info, DateUtils.FORMAT_SHOW_TIME)
		}
	}
	
	private fun updateCurrentData(buttonId: Int){
		val dateType = when(buttonId) {
			EOSRAMChartType.Minute.code -> DateUtils.FORMAT_SHOW_TIME
			EOSRAMChartType.Hour.code -> DateUtils.FORMAT_SHOW_TIME
			EOSRAMChartType.Day.code -> DateUtils.FORMAT_SHOW_DATE
			else -> DateUtils.FORMAT_SHOW_TIME
		}
		
		val period = when(buttonId) {
			EOSRAMChartType.Minute.code -> EOSRAMChartType.Minute.info
			EOSRAMChartType.Hour.code -> EOSRAMChartType.Hour.info
			EOSRAMChartType.Day.code -> EOSRAMChartType.Day.info
			else -> EOSRAMChartType.Minute.info
		}
		presenter.updateRAMCandleData(period,  dateType)
	}
	
}