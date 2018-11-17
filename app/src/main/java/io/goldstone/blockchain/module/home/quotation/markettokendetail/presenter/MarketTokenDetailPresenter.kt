package io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.content.ContextCompat.startActivity
import android.text.format.DateUtils
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.blinnnk.extension.*
import com.blinnnk.uikit.TimeUtils
import com.blinnnk.util.getParentFragment
import com.blinnnk.util.load
import com.blinnnk.util.then
import com.github.mikephil.charting.data.CandleEntry
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.getMainnetChainID
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.CurrentPriceModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenCandleChart
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.PriceHistoryModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */
class MarketTokenDetailPresenter(
	override val fragment: MarketTokenDetailFragment
) : BasePresenter<MarketTokenDetailFragment>() {

	private val marketCenter by lazy {
		fragment.getParentFragment<MarketTokenCenterFragment>()
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.currencyInfo?.apply {
			updateCurrencyPriceInfo(this)
			setCurrencyInfo(this)
		}
	}

	fun updateChartByMenu(chartView: MarketTokenCandleChart, buttonID: Int) {
		val period = when (buttonID) {
			MarketTokenDetailChartType.WEEK.code -> MarketTokenDetailChartType.WEEK.info
			MarketTokenDetailChartType.DAY.code -> MarketTokenDetailChartType.DAY.info
			MarketTokenDetailChartType.MONTH.code -> MarketTokenDetailChartType.MONTH.info
			MarketTokenDetailChartType.Hour.code -> MarketTokenDetailChartType.Hour.info
			else -> ""
		}
		val dateType: Int = when (period) {
			MarketTokenDetailChartType.WEEK.info,
			MarketTokenDetailChartType.DAY.info,
			MarketTokenDetailChartType.MONTH.info -> DateUtils.FORMAT_NUMERIC_DATE
			MarketTokenDetailChartType.Hour.info -> DateUtils.FORMAT_SHOW_TIME
			else -> 1000
		}

		val pair = fragment.currencyInfo?.pair ?: return
		load {
			QuotationSelectionTable.dao.getSelectionByPair(pair)
		} then {
			if (it.isNull()) return@then
			val data = when (period) {
				MarketTokenDetailChartType.WEEK.info -> it.lineChartWeek
				MarketTokenDetailChartType.DAY.info -> it.lineChartDay
				MarketTokenDetailChartType.MONTH.info -> it.lineChartMonth
				else -> it.lineChartHour
			}
			// 更新网络数据
			if (data.isNullOrBlank() && NetworkUtil.hasNetwork(fragment.context)) {
				fragment.showLoadingView()
				updateCandleChartDataBy(chartView, pair, period, dateType)
			} else if (!data.isNullOrBlank()) {
				// 本地数据库有数据的话判断是否是有效的数据
				val candleData = CandleChartModel.convertData(data)
				val databaseTime = candleData.maxBy { it.time }?.time?.toLongOrNull().orElse(0)
				// 校验数据库的数据时间是否有效，是否需要更新
				checkDatabaseTimeIsValidBy(period, databaseTime) {
					chartView.updateCandleChartUI(candleData, dateType)
					// 不合规且有网络环境就更新网络数据
					if (!this && NetworkUtil.hasNetwork(fragment.context))
						updateCandleChartDataBy(chartView, pair, period, dateType)
				}
			}
		}
	}

	fun showAllDescription(parent: ViewGroup) {
		DefaultTokenTable.getToken(
			fragment.currencyInfo?.contract.orEmpty(),
			fragment.currencyInfo?.symbol.orEmpty(),
			TokenContract(fragment.currencyInfo).getMainnetChainID()
		) {
			// 描述的第一位存储了语言的标识, 所以从第二位开始展示
			val content =
				if (it?.description.isNullOrBlank() || it?.description.isNullOrEmpty())
					QuotationText.emptyDescription
				else it?.description?.substring(1)
			MaterialDialog(parent.context)
				.title(text = QuotationText.tokenDescription)
				.message(text = content)
				.positiveButton(text = CommonText.gotIt)
				.show()
		}
	}

	fun showWebFragmentWithLink(link: String, title: String) {
		marketCenter?.presenter
			?.showTargetFragment<WebViewFragment, QuotationOverlayFragment>(
				Bundle().apply {
					putString(ArgumentKey.webViewUrl, link)
					putString(ArgumentKey.webViewName, title)
				},
				true
			)
	}

	fun openSystemBrowser(url: String) {
		val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		startActivity(fragment.context!!, browserIntent, null)
	}

	private fun setCurrencyInfo(info: QuotationModel) {
		// 首先展示数据库数据
		getCurrencyInfoFromDatabase(info) { tokenData, priceData ->
			// 首先显示数据库的数据
			fragment.showCurrencyInfo(tokenData, priceData)
			val localLanguageCode = tokenData.description.firstOrNull()?.toString()?.toIntOrNull()
			val needToUpdate =
				tokenData.marketCap.isEmpty()
					|| tokenData.rankValue.isEmpty()
					|| localLanguageCode != SharedWallet.getCurrentLanguageCode()
			if (needToUpdate && NetworkUtil.hasNetwork(fragment.context)) {
				var newPriceData = priceData
				var newTokenData = tokenData
				// 本地没有数据的话从服务端拉取 `Coin Information`
				loadCoinInfoFromServer(info) {
					newTokenData = TokenInformationModel(it, info.symbol)
					fragment.showCurrencyInfo(newTokenData, newPriceData)
				}
				// 更新行情价目网络数据, 更新界面并更新数据库
				getCurrencyInfoFromServer(info) { serverPriceData, error ->
					// 容错, 当 `Server` 返回空数据的时候跳出逻辑
					if (serverPriceData.isNotNull() && error.isNone()) {
						newPriceData = serverPriceData
						launchUI {
							fragment.showCurrencyInfo(newTokenData, serverPriceData)
						}
						serverPriceData.apply {
							QuotationSelectionTable.dao.updatePriceInfo(dayHighest, dayLow, totalHighest, totalLow, info.pair)
						}
					}
				}
			}
		}
	}

	private fun updateCandleChartDataBy(
		chart: MarketTokenCandleChart,
		pair: String,
		period: String,
		dateType: Int
	) {
		// 请求的数据条目数量
		val size = DataValue.candleChartCount
		GoldStoneAPI.getQuotationCurrencyCandleChart(pair, period, size) { candleData, error ->
			val data = if (candleData.isNullOrEmpty() || error.hasError()) arrayListOf() else candleData
			if (data.isNotEmpty()) data.updateLocalCandleChartData(period, pair)
			launchUI {
				chart.updateCandleChartUI(data, dateType)
			}
		}
	}

	private fun checkDatabaseTimeIsValidBy(
		period: String,
		databaseTime: Long,
		callback: Boolean.() -> Unit
	) {
		when (period) {
			// 如果本地数据库的时间周的最大时间小当前于自然周一的时间
			MarketTokenDetailChartType.WEEK.info ->
				callback(databaseTime > TimeUtils.getNatureSundayTimeInMill() - TimeUtils.ondDayInMills)
			// 如果本地数据库的时间是1小时之前的那么更新网络数据
			MarketTokenDetailChartType.DAY.info ->
				callback(databaseTime > 0.daysAgoInMills() - TimeUtils.oneHourInMills)
			// 如果本地数据库的时间是1小时之前的那么更新网络数据
			MarketTokenDetailChartType.MONTH.info ->
				callback(databaseTime > TimeUtils.getNatureMonthFirstTimeInMill() - TimeUtils.ondDayInMills)
			// 如果本地数据库的时间是1小时之前的那么更新网络数据
			else -> callback(databaseTime > System.currentTimeMillis() - TimeUtils.oneHourInMills)
		}
	}

	private fun MarketTokenCandleChart.updateCandleChartUI(data: List<CandleChartModel>, dateType: Int) {
		fragment.removeLoadingView()
		// 服务器抓取的数据返回有一定概率返回错误格式数据
		data.isEmpty() isFalse {
			try {
				resetData(
					dateType,
					data.asSequence().sortedBy { it.time.toLong() }.mapIndexed { index, entry ->
						CandleEntry(
							index.toFloat(),
							entry.high.toFloat(),
							entry.low.toFloat(),
							entry.open.toFloat(),
							entry.close.toFloat(),
							entry.time)
					}.toList()
				)
			} catch (error: Exception) {
				return
			}
		}
	}

	private fun List<CandleChartModel>.updateLocalCandleChartData(period: String, pair: String) {
		map {
			JSONObject("{\"open\":\"${it.open}\",\"close\":\"${it.close}\",\"high\":\"${it.high}\",\"low\":\"${it.low}\",\"time\":${it.time}}")
		}.let {
			when (period) {
				MarketTokenDetailChartType.WEEK.info ->
					QuotationSelectionTable.updateLineChartWeekBy(pair, it.toString()) {}
				MarketTokenDetailChartType.DAY.info ->
					QuotationSelectionTable.updateLineChartDataBy(pair, it.toString()) {}
				MarketTokenDetailChartType.MONTH.info ->
					QuotationSelectionTable.updateLineChartMontyBy(pair, it.toString()) {}
				MarketTokenDetailChartType.Hour.info ->
					QuotationSelectionTable.updateLineChartHourBy(pair, it.toString()) {}
			}
		}
	}

	private fun getCurrencyInfoFromServer(
		info: QuotationModel,
		@WorkerThread hold: (priceInfo: PriceHistoryModel?, error: RequestError) -> Unit
	) {
		GoldStoneAPI.getQuotationCurrencyInfo(info.pair) { serverData, error ->
			if (serverData.isNotNull() && error.isNone()) {
				val priceData = PriceHistoryModel(serverData, info.quoteSymbol)
				hold(priceData, error)
			} else hold(null, error)
		}
	}

	private fun getCurrencyInfoFromDatabase(
		info: QuotationModel,
		@UiThread hold: (
			tokenData: TokenInformationModel,
			priceData: PriceHistoryModel
		) -> Unit
	) {
		GlobalScope.launch(Dispatchers.Default) {
			val default =
				DefaultTokenTable.dao.getTokenFromAllChains(info.contract, info.symbol).firstOrNull()
			val quotation =
				QuotationSelectionTable.dao.getSelectionByPair(info.pair)
			if (quotation.isNotNull()) {
				val tokenData =
					if (default.isNull()) TokenInformationModel()
					else TokenInformationModel(default, info.symbol)
				val priceData = PriceHistoryModel(quotation, info.symbol)
				launchUI {
					hold(tokenData, priceData)
				}
			}
		}
	}

	private fun loadCoinInfoFromServer(
		info: QuotationModel,
		@UiThread hold: (DefaultTokenTable) -> Unit
	) {
		val chainID = TokenContract(info.contract, info.symbol, null).getMainnetChainID()
		GoldStoneAPI.getTokenInfoFromMarket(info.symbol, chainID) { coinInfo, error ->
			if (coinInfo.isNotNull() && error.isNone()) DefaultTokenTable.updateOrInsertCoinInfo(coinInfo) {
				DefaultTokenTable.getToken(info.contract, info.symbol, chainID) {
					it?.let(hold)
				}
			}
		}
	}

	private var currentSocket: GoldStoneWebSocket? = null

	private fun updateCurrencyPriceInfo(info: QuotationModel) {
		// 长连接获取数据
		QuotationPresenter.getPriceInfoBySocket(
			arrayListOf(info.pair),
			{
				currentSocket = it
				currentSocket?.runSocket()
			}
		) { model, isDisconnected ->
			if (model.pair.equals(info.pair, true)) {
				fragment.currentPriceInfo.model = CurrentPriceModel(model, info.quoteSymbol, isDisconnected)
			}
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		currentSocket?.closeSocket()
		fragment.getMainActivity()?.getQuotationFragment()?.presenter?.resetSocket()
	}
}