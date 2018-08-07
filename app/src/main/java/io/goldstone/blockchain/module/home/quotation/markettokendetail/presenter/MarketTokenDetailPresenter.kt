package io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.text.format.DateUtils
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.TimeUtils
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.ContentScrollOverlayView
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.ChartModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.*
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */
class MarketTokenDetailPresenter(
	override val fragment: MarketTokenDetailFragment
) : BasePresenter<MarketTokenDetailFragment>() {
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.currencyInfo?.apply {
			updateCurrencyPriceInfo()
		}
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
							ChartModel(JSONObject(jsonArray[it]?.toString()))
						}.toArrayList().let {
							val databaseTime = it.maxBy {
								it.timestamp
							}?.timestamp?.toLongOrNull().orElse(0)
							// 校验数据库的数据时间是否有效，是否需要更新
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
	
	fun showAllDescription(parent: ViewGroup) {
		if (parent.findViewById<ContentScrollOverlayView>(ElementID.contentScrollview).isNull()) {
			val overlay = ContentScrollOverlayView(parent.context)
			overlay.into(parent)
			overlay.apply {
				setTitle(QuotationText.tokenDescription)
				setContentPadding()
				addContent {
					DefaultTokenTable.getTokenBySymbolAndContractFromAllChains(
						fragment.currencyInfo?.symbol!!,
						fragment.currencyInfo?.contract!!
					) {
						// 描述的第一位存储了语言的标识, 所以从第二位开始展示
						val content =
							if (it?.description.isNullOrBlank() || it?.description?.count() == 0) QuotationText
								.emptyDescription
							else it?.description?.substring(1)
						textView(content) {
							textColor = GrayScale.gray
							textSize = fontSize(14)
							typeface = GoldStoneFont.medium(context)
							layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
						}
					}
				}
				recoveryBackEvent = Runnable {
					fragment.recoveryBackEvent()
				}
			}
		}
	}
	
	fun showWebfragumentWithLink(link: String, title: String, previousTitle: String) {
		showTargetFragment<WebViewFragment, QuotationOverlayFragment>(
			title,
			previousTitle,
			Bundle().apply { putString(ArgumentKey.webViewUrl, link) },
			true
		)
	}
	
	fun openSystemBrowser(url: String) {
		val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		startActivity(fragment.context!!, browserIntent, null)
	}
	
	fun setCurrencyInfo(
		currencyInfo: QuotationModel?,
		tokenInformation: TokenInformation,
		priceHistroy: PriceHistoryView,
		tokenInfo: TokenInfoView,
		tokenInfoLink: TokenInfoLink,
		tokenSocialMedia: TokenSocialMedia
	) {
		currencyInfo?.let { info ->
			// 首先展示数据库数据
			getCurrencyInfoFromDatabase(info) { tokenData, priceData ->
				if (
					tokenData.marketCap.isEmpty()
					|| tokenData.description.firstOrNull()?.toString()?.toIntOrNull() != Config.getCurrentLanguageCode()
				) {
					// 本地没有数据的话从服务端拉取 `Coin Infomation`
					loadCoinInfoFromServer(info) {
						val data = TokenInformationModel(it, info.symbol)
						tokenInformation.model = data
						tokenInfo.setTokenDescription(it.description)
						tokenInfoLink.model = data
						tokenSocialMedia.model = data
					}
				} else {
					tokenInfo.setTokenDescription(tokenData.description)
					tokenInformation.model = tokenData
					tokenInfoLink.model = tokenData
					tokenSocialMedia.model = tokenData
				}
				priceHistroy.model = priceData
			}
			// 更新行情价目网络数据, 更新界面并更新数据库
			getCurrencyInfoFromServer(info) { priceData ->
				priceHistroy.model = priceData
				QuotationSelectionTable.getSelectionByPair(info.pair) {
					doAsync {
						if (it.isNull()) return@doAsync
						else
							GoldStoneDataBase.database.quotationSelectionDao()
								.update(it!!.apply {
									high24 = priceData.dayHighest
									low24 = priceData.dayLow
									highTotal = priceData.totalHighest
									lowTotal = priceData.totalLow
								})
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
		fragment.getMainActivity()?.showLoadingView()
		GoldStoneAPI.getQuotationCurrencyChart(pair, period, 8, {
			// Show the error exception to user
			fragment.context.alert(it.toString().showAfterColonContent())
		}) {
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
	
	private fun MarketTokenChart.updateChartUI(
		data: ArrayList<ChartModel>,
		dateType: Int
	) {
		fragment.context?.apply {
			runOnUiThread {
				fragment.getMainActivity()?.removeLoadingView()
				// 服务器抓取的数据返回有一定概率返回错误格式数据
				try {
					updateData(
						data.sortedBy {
							it.timestamp.toLongOrNull().orElse(0)
						}.map {
							// 服务器抓取数据这里很容易返回格式不正确的数据, 使用 `try catch` 捕捉
							val date = DateUtils.formatDateTime(this, it.timestamp.toLong(), dateType)
							ChartPoint(date, it.price.toFloat())
						}.toArrayList()
					)
				} catch (error: Exception) {
					LogUtil.error("updateChartUI", error)
					return@runOnUiThread
				}
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
	
	private fun getCurrencyInfoFromServer(
		info: QuotationModel,
		hold: (PriceHistoryModel) -> Unit
	) {
		GoldStoneAPI.getQuotationCurrencyInfo(
			info.pair,
			{
				// Show error information to user
				fragment.context.alert(it.toString().showAfterColonContent())
			}
		) { serverData ->
			val priceData = PriceHistoryModel(serverData, info.quoteSymbol)
			fragment.context?.runOnUiThread {
				hold(priceData)
			}
		}
	}
	
	private fun getCurrencyInfoFromDatabase(
		info: QuotationModel,
		hold: (
			tokenData: TokenInformationModel,
			priceData: PriceHistoryModel
		) -> Unit
	) {
		DefaultTokenTable.getTokenBySymbolAndContractFromAllChains(
			info.symbol,
			info.contract
		) { default ->
			QuotationSelectionTable.getSelectionByPair(info.pair) { quotation ->
				val tokenData =
					if (default.isNull()) {
						TokenInformationModel()
					} else {
						TokenInformationModel(default!!, info.symbol)
					}
				val priceData = if (quotation.isNull()) {
					PriceHistoryModel(info.symbol)
				} else {
					PriceHistoryModel(quotation!!, info.symbol)
				}
				hold(tokenData, priceData)
			}
		}
	}
	
	// Async Function
	private fun loadCoinInfoFromServer(
		info: QuotationModel,
		hold: (DefaultTokenTable) -> Unit
	) {
		val chainID = when {
			info.contract.equals(CryptoValue.etcContract, true) -> ChainID.ETCMain.id
			info.contract.isNotEmpty() -> ChainID.Main.id
			info.symbol.equals(CryptoSymbol.btc, true) -> ChainID.BTCMain.id
			info.symbol.equals(CryptoSymbol.ltc, true) -> ChainID.BTCMain.id
			else -> ""
		}
		GoldStoneAPI.getTokenInfoFromMarket(
			info.symbol,
			chainID,
			{
				LogUtil.error("loadCoinInformationFromServer", it)
			}
		) {
			DefaultTokenTable.updateOrInsertCoinInfo(it) {
				DefaultTokenTable.getTokenBySymbolAndContractFromAllChains(
					info.symbol,
					info.contract
				) { it?.let(hold) }
			}
		}
	}
	
	private var currentSocket: GoldStoneWebSocket? = null
	
	private fun QuotationModel.updateCurrencyPriceInfo() {
		// 长连接获取数据
		QuotationPresenter.getPriceInfoBySocket(
			arrayListOf(pair),
			{
				currentSocket = it
				currentSocket?.runSocket()
			}
		) { model, isDisconnected ->
			if (model.pair.equals(pair, true)) {
				fragment.currentPriceInfo.model = CurrentPriceModel(model, quoteSymbol, isDisconnected)
			}
		}
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		currentSocket?.closeSocket()
		
		fragment.getMainActivity()?.getQuotationFragment()?.presenter?.resetSocket()
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		// 从 `WebViewFragment` 返回到这个界面更改 `HeaderTitle`
		// 因为这个页面的 HeaderTitle 是动态数据所以无法用抽象方法实现.
		fragment.getParentFragment<QuotationOverlayFragment> {
			headerTitle = fragment.currencyInfo?.pairDisplay.orEmpty()
		}
	}
}