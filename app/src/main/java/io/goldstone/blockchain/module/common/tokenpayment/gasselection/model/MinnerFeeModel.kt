package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.common.language.PrepareTransferText

/**
 * @date 2018/5/25 2:05 AM
 * @author KaySaith
 */
enum class MinerFeeType(
	val content: String,
	var value: Long,
	var satoshi: Long
) {
	
	Recommend(PrepareTransferText.recommend, 30, 100),
	Cheap(PrepareTransferText.cheap, 1, 10),
	Fast(PrepareTransferText.fast, 100, 500),
	Custom(PrepareTransferText.customize, 0, 0)
}