package io.goldstone.blockchain.common.language

import io.goldstone.blockchain.common.sharedpreference.SharedWallet

/**
 * @date 2018/5/24 12:45 AM
 * @author KaySaith
 */
enum class HoneyLanguage(
	val code: Int,
	val language: String,
	val symbol: String
) {

	English(0, "English", "EN"),
	Chinese(1, "简体中文", "ZH"),
	Japanese(2, "日本語", "JA"),
	Russian(3, "Русский язык", "RU"),
	Korean(4, "한국어", "KO"),
	TraditionalChinese(5, "繁體中文", "TC");

	companion object {

		fun currentLanguageIsSupported(): Boolean {
			return SharedWallet.getCurrentLanguageCode() in 0 .. 5
		}

		fun getPluralLanguageCode(): ArrayList<Int> {
			return arrayListOf(
				English.code
			)
		}

		fun getLanguageCode(language: String): Int {
			return when (language) {
				English.language -> English.code
				Chinese.language -> Chinese.code
				Japanese.language -> Japanese.code
				Russian.language -> Russian.code
				Korean.language -> Korean.code
				TraditionalChinese.language -> TraditionalChinese.code
				else -> 100
			}
		}

		fun getLanguageByCode(code: Int): String {
			return when (code) {
				English.code -> English.language
				Chinese.code -> Chinese.language
				Japanese.code -> Japanese.language
				Russian.code -> Russian.language
				Korean.code -> Korean.language
				TraditionalChinese.code -> TraditionalChinese.language
				else -> ""
			}
		}

		fun getLanguageSymbol(code: Int): String {
			return when (code) {
				English.code -> English.symbol
				Chinese.code -> Chinese.symbol
				Japanese.code -> Japanese.symbol
				Russian.code -> Russian.symbol
				Korean.code -> Korean.symbol
				TraditionalChinese.code -> TraditionalChinese.symbol
				else -> ""
			}
		}

		fun getCodeBySymbol(symbol: String): Int {
			return when (symbol.toUpperCase()) {
				English.symbol -> English.code
				Chinese.symbol -> Chinese.code
				Japanese.symbol -> Japanese.code
				Russian.symbol -> Russian.code
				Korean.symbol -> Korean.code
				TraditionalChinese.symbol -> TraditionalChinese.code
				else -> 100
			}
		}

		val bitcoinPrefix: () -> String = {
			when (currentLanguage) {
				HoneyLanguage.English.code -> "Bitcoin"
				HoneyLanguage.Chinese.code -> "比特币"
				else -> "Bitcoin"
			}
		}
	}
}
