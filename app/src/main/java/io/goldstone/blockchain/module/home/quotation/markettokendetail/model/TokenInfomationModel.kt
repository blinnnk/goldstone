package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import io.goldstone.blockchain.common.utils.safeGet
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
		data.safeGet("rank"),
		data.safeGet("supply") + " " + symbol,
		data.safeGet("market_cap").replace(
			",",
			""
		).toDouble()
	)
}