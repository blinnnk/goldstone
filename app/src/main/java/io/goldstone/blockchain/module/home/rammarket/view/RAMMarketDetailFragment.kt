package io.goldstone.blockchain.module.home.rammarket.view

import android.support.v4.app.Fragment
import android.view.Gravity
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
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.view.*
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMPricePresenter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view.TradingView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: price信息，包含蜡烛走势图
 */
class RAMMarketDetailFragment : BaseFragment<RAMPricePresenter>() {
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	private val ramPriceView by lazy { EOSRAMPriceInfoView(context!!) }
	private val priceChartWithMenuLayout by lazy {
		PriceChartWithMenuLayout(context!!) {
			presenter.updateRAMCandleData(it)
		}
	}
	override val presenter: RAMPricePresenter = RAMPricePresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			verticalLayout {
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				
				addView(ramPriceView)
				addView(priceChartWithMenuLayout)
				addView(TradingView(context!!))
				presenter.updateRAMCandleData(EOSRAMChartType.Minute)
				
			}
		}
		
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
			priceChartWithMenuLayout.candleChart.resetData(dateType, this.mapIndexed { index, entry ->
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