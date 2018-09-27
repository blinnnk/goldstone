package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.common.language.PrepareTransferText

/**
 * @date 2018/5/25 2:05 AM
 * @author KaySaith
 */
enum class MinerFeeType(
	val type: String,
	var value: Long,
	var satoshi: Long
) {

	Recommend(PrepareTransferText.recommend, 30, 100),
	Cheap(PrepareTransferText.cheap, 1, 10),
	Fast(PrepareTransferText.fast, 100, 500),
	Custom(PrepareTransferText.customize, 0, 0);

	fun isFast(): Boolean = type.equals(PrepareTransferText.fast, true)
	fun isCheap(): Boolean = type.equals(PrepareTransferText.cheap, true)
	fun isRecommend(): Boolean = type.equals(PrepareTransferText.recommend, true)
	fun isCustom(): Boolean = type.equals(PrepareTransferText.customize, true)

	companion object {
		fun getTypeByValue(value: String) = when {
			value.equals(PrepareTransferText.fast, true) -> MinerFeeType.Fast
			value.equals(PrepareTransferText.cheap, true) -> MinerFeeType.Cheap
			value.equals(PrepareTransferText.recommend, true) -> MinerFeeType.Recommend
			else -> MinerFeeType.Custom
		}
	}
}