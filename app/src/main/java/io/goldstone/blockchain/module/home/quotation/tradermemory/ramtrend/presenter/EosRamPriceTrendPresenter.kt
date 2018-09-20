package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter

import com.github.mikephil.charting.data.CandleEntry
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EosRamPriceTrendCandleChart
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EosRamPriceTrendFragment
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EosRamPriceTrendPresenter(override val fragment: EosRamPriceTrendFragment)
	: BasePresenter<EosRamPriceTrendFragment>() {
	
	fun updateCandleData(
		candleChart: EosRamPriceTrendCandleChart,
		pair: String,
		dateType: Int
	) {
		updateEosRamPriceTrendcy(candleChart, pair , dateType)
	}
	
	private fun updateEosRamPriceTrendcy(
		candleChart: EosRamPriceTrendCandleChart,
		period: String,
		dateType: Int
	) {
		// 请求的数据条目数量
		val size = DataValue.candleChartCount
		fragment.getMainActivity()?.showLoadingView()
		GoldStoneAPI.getEosRamPriceTendcyCandle(
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
	
	private fun EosRamPriceTrendCandleChart.updateCandleChartUI(
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
}











