package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import com.blinnnk.extension.safeGet
import org.json.JSONObject

/**
 * @date 2018/6/26 6:57 PM
 * @author KaySaith
 */
data class CoinInfoModel(
	val symbol: String,
	val description: String,
	val website: String,
	val marketCap: String,
	val supply: String,
	val exchange: String,
	val rank: String,
	val socialMedia: String,
	val whitePaper: String,
	val startDate: String,
	val contract: String,
	val chainID: String
) {
	
	constructor(data: JSONObject, symbol: String, chainID: String) : this(
		symbol,
		data.safeGet("description"),
		data.safeGet("website"),
		data.safeGet("market_cap"),
		data.safeGet("supply"),
		data.safeGet("exchange"),
		data.safeGet("rank"),
		data.safeGet("social_media"),
		data.safeGet("white_paper"),
		data.safeGet("start_date"),
		data.safeGet("address"),
		chainID
	)
}