package io.goldstone.blockchain.common.language

import com.blinnnk.util.HoneyDateUtil

/**
 * @date 2018/8/8 2:25 AM
 * @author KaySaith
 */

object DateAndTimeText {
	@JvmField
	val hour = when (currentLanguage) {
		HoneyLanguage.English.code -> "hour"
		HoneyLanguage.Chinese.code -> "小时"
		HoneyLanguage.Japanese.code -> "時間"
		HoneyLanguage.Korean.code -> "시간"
		HoneyLanguage.Russian.code -> " ЧАС"
		HoneyLanguage.TraditionalChinese.code -> "小時"
		else -> ""
	}
	@JvmField
	val day = when (currentLanguage) {
		HoneyLanguage.English.code -> "day"
		HoneyLanguage.Chinese.code -> "日"
		HoneyLanguage.Japanese.code -> "日"
		HoneyLanguage.Korean.code -> "주간"
		HoneyLanguage.Russian.code -> "ДЕНЬ"
		HoneyLanguage.TraditionalChinese.code -> "日"
		else -> ""
	}
	@JvmField
	val week = when (currentLanguage) {
		HoneyLanguage.English.code -> "week"
		HoneyLanguage.Chinese.code -> "周"
		HoneyLanguage.Japanese.code -> "週間"
		HoneyLanguage.Korean.code -> "주"
		HoneyLanguage.Russian.code -> "ЧЖОУ"
		HoneyLanguage.TraditionalChinese.code -> "周"
		else -> ""
	}
	@JvmField
	val month = when (currentLanguage) {
		HoneyLanguage.English.code -> "month"
		HoneyLanguage.Chinese.code -> "月"
		HoneyLanguage.Japanese.code -> "ヶ月"
		HoneyLanguage.Korean.code -> "달"
		HoneyLanguage.Russian.code -> "МЕСЯЦ"
		HoneyLanguage.TraditionalChinese.code -> "月"
		else -> ""
	}
	@JvmField
	val second = when (currentLanguage) {
		HoneyLanguage.English.code -> "second"
		HoneyLanguage.Chinese.code -> "秒"
		HoneyLanguage.Japanese.code -> "秒"
		HoneyLanguage.Korean.code -> "초"
		HoneyLanguage.Russian.code -> " секунды"
		HoneyLanguage.TraditionalChinese.code -> "秒"
		else -> ""
	}
	@JvmField
	val minute = when (currentLanguage) {
		HoneyLanguage.English.code -> "minute"
		HoneyLanguage.Chinese.code -> "分钟"
		HoneyLanguage.Japanese.code -> "分"
		HoneyLanguage.Korean.code -> "분"
		HoneyLanguage.Russian.code -> " минуты"
		HoneyLanguage.TraditionalChinese.code -> "分鐘"
		else -> ""
	}
	@JvmField
	val hours = when (currentLanguage) {
		HoneyLanguage.English.code -> "24 Hours"
		HoneyLanguage.Chinese.code -> "24 小时"
		HoneyLanguage.Japanese.code -> "24 時間"
		HoneyLanguage.Korean.code -> "24 시간"
		HoneyLanguage.Russian.code -> "24 часа "
		HoneyLanguage.TraditionalChinese.code -> "24 小時"
		else -> ""
	}
	@JvmField
	val total = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total"
		HoneyLanguage.Chinese.code -> "全部"
		HoneyLanguage.Japanese.code -> "すべて"
		HoneyLanguage.Korean.code -> "모두"
		HoneyLanguage.Russian.code -> "полный"
		HoneyLanguage.TraditionalChinese.code -> "全部"
		else -> ""
	}
	@JvmField
	val ago = when (currentLanguage) {
		HoneyLanguage.English.code -> "ago"
		HoneyLanguage.Chinese.code -> "前"
		HoneyLanguage.Japanese.code -> "前"
		HoneyLanguage.Korean.code -> "전에"
		HoneyLanguage.Russian.code -> " назад"
		HoneyLanguage.TraditionalChinese.code -> "前"
		else -> ""
	}

	@JvmField
	val later = when (currentLanguage) {
		HoneyLanguage.English.code -> "later"
		HoneyLanguage.Chinese.code -> "later"
		HoneyLanguage.Japanese.code -> "later"
		HoneyLanguage.Korean.code -> "later"
		HoneyLanguage.Russian.code -> " later"
		HoneyLanguage.TraditionalChinese.code -> "later"
		else -> ""
	}

	fun getDateText(): HoneyDateUtil.DataText {
		return HoneyDateUtil.DataText(
			month,
			week,
			day,
			hour,
			minute,
			second,
			ago,
			later,
			HoneyLanguage.getPluralLanguageCode().any { it == currentLanguage }
		)
	}
}

