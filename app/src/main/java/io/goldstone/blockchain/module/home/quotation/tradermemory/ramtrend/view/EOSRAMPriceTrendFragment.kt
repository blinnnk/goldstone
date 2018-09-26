package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view

import android.support.v4.app.Fragment
import android.text.format.DateUtils
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter.EOSRAMPriceTrendPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EOSRAMPriceTrendFragment : BaseFragment<EOSRAMPriceTrendPresenter>() {
	
	val candleChart by lazy { EOSRAMPriceTrendCandleChart(context!!) }
	private val menu by lazy { ButtonMenu(context!!) }
	
	val ramInformationHeader by lazy { RAMInformationHeader(context!!) }
	
	override val presenter: EOSRAMPriceTrendPresenter = EOSRAMPriceTrendPresenter(this)
	
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
			presenter.updateEosRamPriceTrend(EOSRAMChartType.Minute.info, DateUtils.FORMAT_SHOW_TIME)
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
		presenter.updateEosRamPriceTrend(period,  dateType)
	}
	
	override fun onPause() {
		super.onPause()
		presenter.onPause()
	}
	
	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		presenter.onHiddenChanged(hidden)
	}
}