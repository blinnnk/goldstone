package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

/**
 * @date 2018/5/25 2:05 AM
 * @author KaySaith
 */

enum class MinerFeeType(
	val content: String,
	var value: Long
) {
	Recommend("recommend", 30),
	Cheap("cheap", 1),
	Fast("fast", 100),
	Custom("custom", 0)
}