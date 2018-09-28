package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model

/**
 * @date: 2018/9/28.
 * @author: yanglihai
 * @description:
 */
data class RAMInformationModel(
	var openPrice: String?,
	var HighPrice: String?,
	var lowPrice: String?,
	var currentPrice: String?,
	var maxAmount: String?,
	var occupyAmount: String?,
	var ramAmountPercent: String?
) {

}