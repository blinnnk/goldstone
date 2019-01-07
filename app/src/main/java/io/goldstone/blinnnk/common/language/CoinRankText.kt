package io.goldstone.blinnnk.common.language

/**
 * @date: 2018-12-20.
 * @author: yangLiHai
 * @description:
 */
object CoinRankText {

	@JvmField
	val marketRankPageTitle = when (currentLanguage) {
		HoneyLanguage.English.code->"Market Cap Rank"
		HoneyLanguage.Chinese.code->"市值排行"
		HoneyLanguage.Japanese.code->"時価総額のランキング"
		HoneyLanguage.Korean.code->"시가 총액 순위"
		HoneyLanguage.Russian.code->"Рыночная Кепка Ранг"
		HoneyLanguage.TraditionalChinese.code->"市值排行"
		else -> ""
	}

	@JvmField
	val marketCap = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Cap"
		HoneyLanguage.Chinese.code -> "市值"
		HoneyLanguage.Japanese.code -> "時価総額"
		HoneyLanguage.Korean.code -> "시가 총액"
		HoneyLanguage.Russian.code -> "Рыночная Кепка"
		HoneyLanguage.TraditionalChinese.code -> "市值"
		else -> ""
	}
	@JvmField
	val volume24h = when (currentLanguage) {
		HoneyLanguage.English.code->"24H Volume"
		HoneyLanguage.Chinese.code->"24 时成交量"
		HoneyLanguage.Japanese.code->"24 時量"
		HoneyLanguage.Korean.code->"24시 부피"
		HoneyLanguage.Russian.code->"24H Том"
		HoneyLanguage.TraditionalChinese.code->"24時成交量"
		else -> ""
	}
	@JvmField
	val btcDominance = when (currentLanguage) {
		HoneyLanguage.English.code->"BTC Dominance"
		HoneyLanguage.Chinese.code->"BTC 主导率"
		HoneyLanguage.Japanese.code->"BTCドミナンス"
		HoneyLanguage.Korean.code->"BTC 우세"
		HoneyLanguage.Russian.code->"BTC Доминирование"
		HoneyLanguage.TraditionalChinese.code->"BTC 主導率"
		else -> ""
	}
}