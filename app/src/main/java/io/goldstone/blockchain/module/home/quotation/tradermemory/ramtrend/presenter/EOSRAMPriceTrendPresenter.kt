package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter

import com.github.mikephil.charting.data.CandleEntry
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSRAMUtil
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EOSRAMPriceTrendCandleChart
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EOSRAMPriceTrendFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.textColor

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EOSRAMPriceTrendPresenter(override val fragment: EOSRAMPriceTrendFragment)
	: BasePresenter<EOSRAMPriceTrendFragment>() {
	
	private var todayOpenPrice: String? = null
	private var todayCurrentPrice: String? = null
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		getTodayPrice()
		EOSRAMUtil.getRAMPrice(EOSUnit.KB) {
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramInformationHeader.apply {
					currentPrice.text = it.toString()
					todayCurrentPrice = it.toString()
					setTrendPercent()
					
				}
			}
			
		}
	}
	
	fun updateCandleData(
		candleChart: EOSRAMPriceTrendCandleChart,
		pair: String,
		dateType: Int
	) {
		updateEosRamPriceTrendcy(candleChart, pair , dateType)
	}
	
	private fun updateEosRamPriceTrendcy(
		candleChart: EOSRAMPriceTrendCandleChart,
		period: String,
		dateType: Int
	) {
		// 请求的数据条目数量
		val size = DataValue.candleChartCount
		fragment.getMainActivity()?.showLoadingView()
		GoldStoneAPI.getEOSRAMPriceTendcyCandle(
			period,
			size,
			{
				// Show the error exception to user
				fragment.context.alert(it.toString().showAfterColonContent())
				fragment.getMainActivity()?.removeLoadingView()
				candleChart.updateCandleChartUI(arrayListOf(), dateType)
			}
		) {
			// 把数据更新到数据库
			// 更新 `UI` 界面
			GoldStoneAPI.context.runOnUiThread {
				fragment.getMainActivity()?.removeLoadingView()
				candleChart.updateCandleChartUI(it, dateType)
			}
		}
	}
	
	private fun EOSRAMPriceTrendCandleChart.updateCandleChartUI(
		data: ArrayList<CandleChartModel>,
		dateType:Int
	) {
		resetData(dateType, data.mapIndexed { index, entry ->
			CandleEntry(
				index.toFloat(),
				entry.high.toFloat(),
				entry.low.toFloat(),
				entry.open.toFloat(),
				entry.close.toFloat(),
				entry.time)
			}
		)
		
	}
	
	private fun getTodayPrice() {
		GoldStoneAPI.getEOSRAMPriceToday( {
			// Show the error exception to user
			fragment.context.alert(it.toString().showAfterColonContent())
		}) {
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramInformationHeader.apply {
					this@EOSRAMPriceTrendPresenter.todayOpenPrice = it.open
					startPrice.text = it.open
					highPrice.text = it.high
					lowPrice.text = it.low
					setTrendPercent()
				}
			}
			
		}
	}
	
	private fun setTrendPercent() {
		todayOpenPrice?.let { open ->
			todayCurrentPrice?.let { current ->
				var trend = (current.toDouble() - open.toDouble()) / open.toDouble()
				trend *= 100.toDouble()
				fragment.ramInformationHeader.apply {
					if (trend > 0) {
						trendcyPercent.text = "+$trend%"
						trendcyPercent.textColor = Spectrum.green
					} else {
						trendcyPercent.text = "-$trend%"
						trendcyPercent.textColor = Spectrum.red
					}
				}
			}
		}
	}
}











