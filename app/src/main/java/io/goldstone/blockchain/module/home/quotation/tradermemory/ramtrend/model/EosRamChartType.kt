package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model

import io.goldstone.blockchain.common.language.DateAndTimeText

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
enum class EosRamChartType(
	val code: Int,
	val info: String,
	val display: String
) {
	
	MINUTE(0, "1min", DateAndTimeText.minute.toUpperCase()),
	Hour(1, "1hour", DateAndTimeText.hour.toUpperCase()),
	DAY(2, "1day", DateAndTimeText.day.toUpperCase())
}