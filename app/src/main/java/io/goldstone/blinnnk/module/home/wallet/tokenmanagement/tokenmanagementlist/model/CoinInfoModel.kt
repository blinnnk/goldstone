package io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import com.blinnnk.extension.safeGet
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import org.json.JSONObject
import java.io.Serializable

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
	val contract: TokenContract,
	val chainID: String
) : Serializable {

	constructor(data: JSONObject, symbol: String, chainID: ChainID) : this(
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
		TokenContract(data.safeGet("address"), symbol, null),
		chainID.id
	)
}