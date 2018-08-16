package io.goldstone.blockchain.module.home.quotation.rank

/**
 * @date: 2018/8/14.
 * @author: yanglihai
 * @description:
 */
data class RankModel(
	var id_name: String = "",
	var market_cap: String = "",
	var change_percent_24h: String = "",
	var name: String = "",
	var color: String = "",
	var symbol: String = "",
	var price: String = "",
	var rank: String = "",
	var icon: String = ""
)