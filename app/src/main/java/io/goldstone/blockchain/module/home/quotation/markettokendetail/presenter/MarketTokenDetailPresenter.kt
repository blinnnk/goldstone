package io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter

import android.text.format.DateUtils
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeGet
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.*
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
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
			fragment.getMainActivity()?.showLoadingView()
			GoldStoneAPI.getQuotationCurrencyChart(pair, period, 8) {
				fragment.context?.apply {
					runOnUiThread {
						fragment.getMainActivity()?.removeLoadingView()
						chartView.chartData = it.sortedBy {
							it.timestamp
						}.map {
							Point(
								DateUtils.formatDateTime(this, it.timestamp.toLong(), dateType),
								it.price.toFloat()
							)
						}.toArrayList()
					}
				}
			}
		}
	}

	fun setCurrencyInf(
		currencyInfo: QuotationModel?,
		tokenInfomation: TokenInfomation,
		priceHistroy: PriceHistoryView,
		tokenInfo: TokenInfoView
	) {
		currencyInfo?.let { info ->
			GoldStoneAPI.getQuotationCurrencyInfo(info.pair) {
				fragment.context?.runOnUiThread {
					tokenInfomation.model = TokenInfomationModel(it, info.symbol)
					priceHistroy.model = PriceHistoryModel(it, info.quoteSymbol)
				}
			}
			loadDescriptionFromLocalOrServer(info, tokenInfo)
		}
	}

	private fun loadDescriptionFromLocalOrServer(info: QuotationModel, tokenInfo: TokenInfoView) {
		QuotationSelectionTable.getSelectionByPair(info.pair) {
			it?.apply {

				val maxCount: (String?) -> Int = { it ->
					if (it?.length.orZero() < 300) it?.length.orZero()
					else 300
				}

				// 判断本地是否有数据, 或者本地的描述的语言和用户的选择语言是否一致
				if (
					description.isNullOrBlank()
					|| !description?.substring(0, 2).equals(HoneyLanguage.getLanguageSymbol(GoldStoneApp.currentLanguage.orZero()), true)
					) {
					GoldStoneAPI.getQuotationCurrencyDescription(info.symbol) { description ->
						fragment.context?.runOnUiThread {
							tokenInfo.setTokenDescription(description.substring(0, maxCount(description)) + "...")
						}
						QuotationSelectionTable.updateDescription(info.pair, description)
					}
				} else {
					tokenInfo.setTokenDescription(description?.substring(2, maxCount(description)) + "...")
				}
			}
		}
	}

	private var currentSocket: GoldStoneWebSocket? = null
	private fun QuotationModel.updateCurrencyPriceInfo() {
		// 传默认值
		fragment.currentPriceInfo.model = CurrentPriceModel()
		// 长连接获取数据
		QuotationPresenter.getPriceInfoBySocket(arrayListOf(pair), {
			currentSocket = it
			currentSocket?.runSocket()
		}) {
			if (it.pair == pair) {
				fragment.currentPriceInfo.model = CurrentPriceModel(it, quoteSymbol)
			}
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		currentSocket?.closeSocket()
	}
}