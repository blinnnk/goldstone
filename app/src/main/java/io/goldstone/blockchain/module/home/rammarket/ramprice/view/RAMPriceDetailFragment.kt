package io.goldstone.blockchain.module.home.rammarket.ramprice.view

import android.support.v4.app.Fragment
import android.text.format.DateUtils
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.data.CandleEntry
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.rammarket.view.EOSRAMPriceCandleChart
import io.goldstone.blockchain.module.home.rammarket.ramprice.presenter.RAMPricePresenter
import io.goldstone.blockchain.module.home.rammarket.ramtrade.view.TradingView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: price信息，包含蜡烛走势图
 */
class RAMPriceDetailFragment : BaseFragment<RAMPricePresenter>() {
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	val candleChart by lazy { EOSRAMPriceCandleChart(context!!) }
	private val menu by lazy { ButtonMenu(context!!) }
	val ramPriceView by lazy { EOSRAMPriceInfoView(context!!) }
	override val presenter: RAMPricePresenter = RAMPricePresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		menu.apply {
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 16.uiPX()
				leftMargin = 20.uiPX()
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
			addView(ramPriceView)
			addView(menu)
			addView(candleChart.apply { x += 10.uiPX() })
			addView(TradingView(context!!))
			presenter.updateRAMCandleData(EOSRAMChartType.Minute)
		}
		
		
	}
	
	private fun updateCurrentData(buttonId: Int){
		presenter.updateRAMCandleData( when(buttonId) {
			EOSRAMChartType.Minute.code -> EOSRAMChartType.Minute
			EOSRAMChartType.Hour.code -> EOSRAMChartType.Hour
			EOSRAMChartType.Day.code -> EOSRAMChartType.Day
			else -> EOSRAMChartType.Minute
		})
	}
	
	fun setCurrentPriceAndPercent(price: String, percent: Float) {
		ramPriceView.currentPriceView.currentPrice.text = price
		ramPriceView.currentPriceView.trendcyPercent.apply {
			val trendBigDecimal = BigDecimal(percent.toString()).divide(BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP)
			if (percent > 0) {
				text = "+$trendBigDecimal%"
				textColor = Spectrum.green
			} else {
				text = "$trendBigDecimal%"
				textColor = Spectrum.red
			}
		}
		
	}
	
	fun setTodayPrice(startPrice: String, highPrice: String, lowPrice: String) {
		ramPriceView.todayPriceView.startPrice.text = EOSRAMExchangeText.openPrice(startPrice)
		ramPriceView.todayPriceView.highPrice.text = EOSRAMExchangeText.openPrice(highPrice)
		ramPriceView.todayPriceView.lowPrice.text = EOSRAMExchangeText.openPrice(lowPrice)
	}
	
	fun setSocketDisconnectedPercentColor(color: Int) {
		ramPriceView.currentPriceView.trendcyPercent.textColor = color
	}
	
	fun updateCandleChartUI(dateType: Int, data: ArrayList<CandleChartModel>) {
		data.apply {
			candleChart.resetData(dateType, this.mapIndexed { index, entry ->
				CandleEntry(
					index.toFloat(),
					entry.high.toFloat(),
					entry.low.toFloat(),
					entry.open.toFloat(),
					entry.close.toFloat(),
					entry.time)
			})
		}
	}
	
}