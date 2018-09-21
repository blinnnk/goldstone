package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model

import java.io.Serializable

/**
 * @date: 2018/9/21.
 * @author: yanglihai
 * @description:
 */
class RAMMarketHeaderModel(
	var trendPercent: String = "",
	var currentPrice: String = "0.11226540",
	var open: String = "",
	var high: String = "",
	var low: String = "",
	var ramMax: String = "",
	var ramTotalReversed: String = ""
): Serializable