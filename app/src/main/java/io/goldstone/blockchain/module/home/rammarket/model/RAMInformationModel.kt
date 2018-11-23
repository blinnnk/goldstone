package io.goldstone.blockchain.module.home.rammarket.model

import java.io.Serializable

/**
 * @date: 2018/9/28.
 * @author: yanglihai
 * @description: ram市场头部的信息的model
 */
data class RAMInformationModel(
	var openPrice: Double,
	var HighPrice: Double,
	var lowPrice: Double,
	var currentPrice: Double,
	var pricePercent: Double
): Serializable {
	constructor() : this(
		0.0,
		0.0,
		0.0,
		0.0,
		0.0
	)
}