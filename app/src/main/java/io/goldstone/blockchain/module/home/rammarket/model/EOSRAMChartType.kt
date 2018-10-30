package io.goldstone.blockchain.module.home.rammarket.model

import io.goldstone.blockchain.common.language.DateAndTimeText

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
enum class EOSRAMChartType(
	val code: Int,
	val info: String,
	val display: String
) {
	
	Minute(0, "1min", DateAndTimeText.minute.toUpperCase()),
	Hour(1, "1hour", DateAndTimeText.hour.toUpperCase()),
	Day(2, "1day", DateAndTimeText.day.toUpperCase())
}