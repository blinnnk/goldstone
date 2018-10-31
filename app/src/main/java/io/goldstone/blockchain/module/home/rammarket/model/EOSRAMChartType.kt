package io.goldstone.blockchain.module.home.rammarket.model

import android.text.format.DateUtils
import io.goldstone.blockchain.common.language.DateAndTimeText

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description: 蜡烛图的时间类型
 */
enum class EOSRAMChartType(
	val code: Int,
	val info: String,
	val display: String,
	val dateType: Int
) {
	
	Minute(0, "1min", DateAndTimeText.minute.toUpperCase(), DateUtils.FORMAT_SHOW_TIME),
	Hour(1, "1hour", DateAndTimeText.hour.toUpperCase(), DateUtils.FORMAT_SHOW_TIME),
	Day(2, "1day", DateAndTimeText.day.toUpperCase(), DateUtils.FORMAT_SHOW_DATE)
}