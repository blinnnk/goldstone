package io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter

import android.text.format.DateUtils
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.TimeUtils
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.daysAgoInMills
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.ChartModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.*
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Suppress(
	"DEPRECATION", "IMPLICIT_CAST_TO_ANY"
)
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

	fun updateChartByMenu(
		chartView: MarketTokenChart,
		buttonID: Int
	) {
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
			QuotationSelectionTable.getSelectionByPair(pair) {
				it?.apply {
					val data: String? = when (period) {
						MarketTokenDetailChartType.WEEK.info -> lineChartWeek
						MarketTokenDetailChartType.DAY.info -> lineChartDay
						MarketTokenDetailChartType.MONTH.info -> lineChartMonth
						else -> lineChartHour
					}
					if (data.isNullOrBlank()) {
						// 更新网络数据
						chartView.updateChartDataBy(pair, period, dateType)
						// 本地数据库如果没有数据就跳出逻辑
						return@getSelectionByPair
					} else {
						// 本地数据库有数据的话判断是否是有效的数据
						val jsonArray = JSONArray(data)
						// 把数据转换成需要的格式
						(0 until jsonArray.length()).map {
							ChartModel(JSONObject(jsonArray[it].toString()))
						}.toArrayList().let {
							val databaseTime = it.maxBy { it.timestamp }?.timestamp?.toLong().orElse(0)
							/** 校验数据库的数据时间是否有效，是否需要更新 */
							checkDatabaseTimeIsValidBy(period, databaseTime) {
								isTrue {
									// 合规就更新本地数据库的数据
									chartView.updateChartUI(it, dateType)
								} otherwise {
									// 不合规就更新网络数据
									chartView.updateChartDataBy(pair, period, dateType)
								}
							}
						}
					}
				}
			}
		}
	}

	private fun MarketTokenChart.updateChartDataBy(
		pair: String,
		period: String,
		dateType: Int
	) {
		GoldStoneAPI.getQuotationCurrencyChart(pair, period, 8) {
			// 把数据更新到数据库
			it.updateChartDataInDatabaseBy(period, pair)
			// 更新 `UI` 界面
			updateChartUI(it, dateType)
		}
	}

	private fun checkDatabaseTimeIsValidBy(
		period: String,
		databaseTime: Long,
		callback: Boolean.() -> Unit
	) {
		when (period) {
			MarketTokenDetailChartType.WEEK.info -> {
				// 如果本地数据库的时间周的最大时间小当前于自然周一的时间
				callback(databaseTime > TimeUtils.getNatureSundayTimeInMill() - TimeUtils.ondDayInMills)
			}
			MarketTokenDetailChartType.DAY.info -> {
				// 如果本地数据库的时间是1小时之前的那么更新网络数据
				callback(databaseTime > 0.daysAgoInMills() - TimeUtils.oneHourInMills)
			}
			MarketTokenDetailChartType.MONTH.info -> {
				// 如果本地数据库的时间是1小时之前的那么更新网络数据
				callback(databaseTime > TimeUtils.getNatureMonthFirstTimeInMill() - TimeUtils.ondDayInMills)
			}

			else -> {
				// 如果本地数据库的时间是1小时之前的那么更新网络数据
				callback(databaseTime > System.currentTimeMillis() - TimeUtils.oneHourInMills)
			}
		}
	}

	private fun MarketTokenChart.updateChartUI(data: ArrayList<ChartModel>, dateType: Int) {
		fragment.context?.apply {
			runOnUiThread {
				fragment.getMainActivity()?.removeLoadingView()
				chartData = data.sortedBy {
					it.timestamp
				}.map {
					Point(
						DateUtils.formatDateTime(
							this, it.timestamp.toLong(), dateType
						), it.price.toFloat()
					)
				}.toArrayList()
			}
		}
	}

	private fun ArrayList<ChartModel>.updateChartDataInDatabaseBy(
		period: String,
		pair: String
	) {
		map { JSONObject("{\"price\":\"${it.price}\",\"time\":${it.timestamp}}") }.let {
			when (period) {
				MarketTokenDetailChartType.WEEK.info -> {
					QuotationSelectionTable.updateLineChartWeekBy(pair, it.toString())
				}
				MarketTokenDetailChartType.DAY.info -> {
					QuotationSelectionTable.updateLineChartDataBy(pair, it.toString())
				}
				MarketTokenDetailChartType.MONTH.info -> {
					QuotationSelectionTable.updateLineChartMontyBy(pair, it.toString())
				}
				MarketTokenDetailChartType.Hour.info -> {
					QuotationSelectionTable.updateLineChartHourBy(pair, it.toString())
				}
			}
		}
	}

	fun setCurrencyInf(
		currencyInfo: QuotationModel?,
		tokenInformation: TokenInformation,
		priceHistroy: PriceHistoryView,
		tokenInfo: TokenInfoView
	) {
		currencyInfo?.let { info ->
			GoldStoneAPI.getQuotationCurrencyInfo(info.pair) {
				fragment.context?.runOnUiThread {
					tokenInformation.model = TokenInformationModel(it, info.symbol)
					priceHistroy.model = PriceHistoryModel(it, info.quoteSymbol)
				}
			}
			loadDescriptionFromLocalOrServer(info, tokenInfo)
		}
	}

	private fun loadDescriptionFromLocalOrServer(
		info: QuotationModel,
		tokenInfo: TokenInfoView
	) {
		QuotationSelectionTable.getSelectionByPair(info.pair) {
			it?.apply {

				val maxCount: (String?) -> Int = { it ->
					if (it?.length.orZero() < 300) it?.length.orZero()
					else 300
				}

				// 判断本地是否有数据, 或者本地的描述的语言和用户的选择语言是否一致
				if (description.isNullOrBlank() || !description?.substring(0, 2).equals(
						HoneyLanguage.getLanguageSymbol(GoldStoneApp.currentLanguage.orZero()), true
					)
				) {
					GoldStoneAPI.getQuotationCurrencyDescription(info.symbol) { description ->
						fragment.context?.runOnUiThread {
							val content = description.substring(0, maxCount(description)) + "..."
							tokenInfo.setTokenDescription(content)
							tokenInfo.updateHeightByText(content, tokenInfo.fontSize(14), 18.uiPX(), ScreenSize.widthWithPadding)
						}
						QuotationSelectionTable.updateDescription(
							info.pair, description
						)
					}
				} else {
					tokenInfo.setTokenDescription(
						description?.substring(2, maxCount(description)) + "..."
					)
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

		QuotationPresenter.getQuotationFragment(fragment.getMainActivity()) {
			presenter.resetSocket()
		}
	}
}