package io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter

import android.text.format.DateUtils
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.CurrentPriceModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenChart
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import org.jetbrains.anko.runOnUiThread

@Suppress("DEPRECATION", "IMPLICIT_CAST_TO_ANY")
/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */

class MarketTokenDetailPresenter(
	override val fragment: MarketTokenDetailFragment
) : BasePresenter<MarketTokenDetailFragment>() {

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<QuotationOverlayFragment>()?.apply {
			overlayView.contentLayout.updateHeightAnimation(context?.getRealScreenHeight().orZero())
		}

		fragment.currencyInfo?.apply { updateCurrencyPriceInfo() }
	}

	fun updateChartByMenu(chartView: MarketTokenChart, buttonID: Int) {

		val period = when (buttonID) {
			MarketTokenDetailChartType.WEEK.code -> MarketTokenDetailChartType.WEEK.info
			MarketTokenDetailChartType.DAY.code -> MarketTokenDetailChartType.DAY.info
			MarketTokenDetailChartType.MONTH.code -> MarketTokenDetailChartType.MONTH.info
			MarketTokenDetailChartType.Hour.code -> MarketTokenDetailChartType.Hour.info
			else -> ""
		}

		val dateType: Int = when (period) {
			MarketTokenDetailChartType.WEEK.info -> DateUtils.FORMAT_NUMERIC_DATE
			MarketTokenDetailChartType.DAY.info -> DateUtils.FORMAT_NUMERIC_DATE
			MarketTokenDetailChartType.MONTH.info -> DateUtils.FORMAT_NUMERIC_DATE
			MarketTokenDetailChartType.Hour.info -> DateUtils.FORMAT_SHOW_TIME
			else -> 1000
		}

		fragment.currencyInfo?.apply {
			GoldStoneAPI.getQuotationCurrencyChart(pair, period, 8) {
				fragment.context?.apply {
					runOnUiThread {
						chartView.chartData = it.sortedBy {
							it.timestamp
						}.map {
							Point(
								DateUtils.formatDateTime(this, it.timestamp.toLong(), dateType), it.price.toFloat()
							)
						}.toArrayList()
					}
				}
			}
		}
	}

	private fun QuotationModel.updateCurrencyPriceInfo() {
		// 传默认值
		fragment.currentPriceInfo.model = CurrentPriceModel()
		// 长连接获取数据
		QuotationPresenter.getPriceInfoBySocket(arrayListOf(pair), {
			it.runSocket()
		}) {
			if (it.pair == pair) {
				fragment.currentPriceInfo.model = CurrentPriceModel(it, quoteSymbol)
			}
		}
	}
}