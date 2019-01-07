package io.goldstone.blinnnk.module.home.quotation.markettokendetail.model

import io.goldstone.blinnnk.common.language.DateAndTimeText
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model.SocialMediaModel

/**
 * @date 2018/5/11 2:03 PM
 * @author KaySaith
 */
data class TokenInformationModel(
	val rankValue: String = "",
	val availableSupply: String = "",
	val marketCap: String = "",
	val socialMedia: List<SocialMediaModel> = listOf(),
	val website: String = "",
	val exchange: String = "",
	val startDate: String = "",
	val whitePaper: String = "",
	val description: String = ""
) {
	
	constructor(
		data: DefaultTokenTable,
		symbol: String
	) : this(
		data.rank,
		"${data.totalSupply} $symbol",
		data.marketCap,
		data.socialMedia,
		data.website,
		data.exchange,
		data.startDate,
		data.whitePaper,
		data.description
	)
}

enum class MarketTokenDetailChartType(
	val code: Int,
	val info: String,
	val display: String
) {
	
	Hour(0, "1hour", DateAndTimeText.hour.toUpperCase()),
	DAY(1, "1day", DateAndTimeText.day.toUpperCase()),
	WEEK(2, "1week", DateAndTimeText.week.toUpperCase()),
	MONTH(3, "1month", DateAndTimeText.month.toUpperCase())
}