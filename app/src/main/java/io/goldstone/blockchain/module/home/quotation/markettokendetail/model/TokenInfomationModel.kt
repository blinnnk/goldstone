package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.value.DateAndTimeText
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.json.JSONObject

/**
 * @date 2018/5/11 2:03 PM
 * @author KaySaith
 */
data class TokenInformationModel(
	val rankValue: String = "",
	val avaliableSupply: String = "",
	val marketCap: Double = 0.0,
	val socialMedia: String = "",
	val website: String = "",
	val exchange: String = "",
	val startDate: String = "",
	val whitePaper: String = ""
) {
	
	constructor(
		data: JSONObject,
		symbol: String
	) : this(
		data.safeGet("rank"),
		data.safeGet("supply") + " " + symbol,
		if (data.safeGet("market_cap").isNotEmpty()) data.safeGet("market_cap").replace(
			",", ""
		).toDouble() else 0.0,
		data.safeGet("social_media"),
		data.safeGet("website"),
		data.safeGet("exchange"),
		data.safeGet("start_date"),
		data.safeGet("white_paper")
	)
	
	constructor(
		data: QuotationSelectionTable,
		symbol: String
	) : this(
		data.rank,
		"${data.availableSupply} $symbol",
		data.marketCap,
		data.socialMedia,
		data.website,
		data.exchange,
		data.startDate,
		data.whitePaper
	)
}

enum class MarketTokenDetailChartType(
	val code: Int,
	val info: String,
	val display: String
) {
	
	Hour(0, "1hour", DateAndTimeText.hour),
	DAY(1, "1day", DateAndTimeText.day),
	WEEK(2, "1week", DateAndTimeText.week),
	MONTH(3, "1month", DateAndTimeText.month)
}