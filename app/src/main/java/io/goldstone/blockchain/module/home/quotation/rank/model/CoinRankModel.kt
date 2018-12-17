package io.goldstone.blockchain.module.home.quotation.rank.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankModel(
	@SerializedName("id_name")
	val idName: String,
	@SerializedName("market_cap")
	val marketCap: String,
	@SerializedName("change_percent_24h")
	val changePercent24h: String,
	val name: String,
	val color: String,
	val symbol: String,
	val price: Float,
	val rank: Int,
	val icon: String
)