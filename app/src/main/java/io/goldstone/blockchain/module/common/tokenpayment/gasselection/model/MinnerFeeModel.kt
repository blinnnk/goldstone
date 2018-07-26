package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.common.value.PrepareTransferText

/**
 * @date 2018/5/25 2:05 AM
 * @author KaySaith
 */
enum class MinerFeeType(
	val content: String,
	var value: Long,
	var satoshi: Long
) {
	
	Recommend(PrepareTransferText.recommend, 30, 50),
	Cheap(PrepareTransferText.cheap, 1, 1),
	Fast(PrepareTransferText.fast, 100, 200),
	Custom(PrepareTransferText.customize, 0, 0)
}