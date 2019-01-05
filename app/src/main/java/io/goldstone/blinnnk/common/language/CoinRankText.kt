package io.goldstone.blinnnk.common.language

/**
 * @date: 2018-12-20.
 * @author: yangLiHai
 * @description:
 */
object CoinRankText {
	@JvmField
	val marketCap = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Cap"
		HoneyLanguage.Chinese.code -> "Market Cap"
		HoneyLanguage.Japanese.code -> "Market Cap"
		HoneyLanguage.Korean.code -> "Market Cap"
		HoneyLanguage.Russian.code -> "Market Cap"
		HoneyLanguage.TraditionalChinese.code -> "Market Cap"
		else -> ""
	}
	@JvmField
	val volume24h = when (currentLanguage) {
		HoneyLanguage.English.code -> "Volume 24h"
		HoneyLanguage.Chinese.code -> "Volume 24h"
		HoneyLanguage.Japanese.code -> "Volume 24h"
		HoneyLanguage.Korean.code -> "Volume 24h"
		HoneyLanguage.Russian.code -> "Volume 24h"
		HoneyLanguage.TraditionalChinese.code -> "Volume 24h"
		else -> ""
	}
	@JvmField
	val btcDominance = when (currentLanguage) {
		HoneyLanguage.English.code -> "BTC Dominance"
		HoneyLanguage.Chinese.code -> "BTC Dominance"
		HoneyLanguage.Japanese.code -> "BTC Dominance"
		HoneyLanguage.Korean.code -> "BTC Dominance"
		HoneyLanguage.Russian.code -> "BTC Dominance"
		HoneyLanguage.TraditionalChinese.code -> "BTC Dominance"
		else -> ""
	}
}