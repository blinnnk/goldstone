package io.goldstone.blockchain.module.home.rammarket.model

/**
 * @date: 2018/9/28.
 * @author: yanglihai
 * @description: ram市场头部的信息的model
 */
data class RAMInformationModel(
	var openPrice: Float?,
	var HighPrice: Float?,
	var lowPrice: Float?,
	var currentPrice: Float?,
	var pricePercent: Float?,
	var maxAmount: Float?,
	var occupyAmount: Float?,
	var ramAmountPercent: Float?
)