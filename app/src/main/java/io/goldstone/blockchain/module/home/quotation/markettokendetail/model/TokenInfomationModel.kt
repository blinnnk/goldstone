package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import com.blinnnk.extension.safeGet
import org.json.JSONObject

/**
 * @date 2018/5/11 2:03 PM
 * @author KaySaith
 */

data class TokenInformationModel(
	val rankValue: String = "",
	val avaliableSupply: String = "",
	val marketCap: Double = 0.0
) {
	constructor(
		data: JSONObject,
		symbol: String
	) : this(
		data.safeGet("rank"), data.safeGet("supply") + " " + symbol,
		if (data.safeGet("market_cap").isNotEmpty()) data.safeGet("market_cap").replace(
			",", ""
		).toDouble() else 0.0
	)
}

enum class MarketTokenDetailChartType(
	val code: Int,
	val info: String,
	val display: String
) {
	Hour(0, "1hour", "HOUR"),
	DAY(1, "1day", "DAY"),
	WEEK(2, "1week", "WEEK"),
	MONTH(3, "1month", "MONTH")
}