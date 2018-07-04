package io.goldstone.blockchain.common.value

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
	Korean(4, "한국어", "KR"),
	TraditionalChinese(5, "繁體中文", "TC");
	
	companion object {
		
		fun currentLanguageIsSupported(): Boolean {
			return Config.getCurrentLanguageCode() in 0 .. 5
		}
		
		fun getPluralLanguageCode(): ArrayList<Int> {
			return arrayListOf(
				HoneyLanguage.English.code
			)
		}
		
		fun getLanguageCode(language: String): Int {
			return when (language) {
				HoneyLanguage.English.language -> HoneyLanguage.English.code
				HoneyLanguage.Chinese.language -> HoneyLanguage.Chinese.code
				HoneyLanguage.Japanese.language -> HoneyLanguage.Japanese.code
				HoneyLanguage.Russian.language -> HoneyLanguage.Russian.code
				HoneyLanguage.Korean.language -> HoneyLanguage.Korean.code
				HoneyLanguage.TraditionalChinese.language -> HoneyLanguage.TraditionalChinese.code
				else -> 100
			}
		}
		
		fun getLanguageByCode(code: Int): String {
			return when (code) {
				HoneyLanguage.English.code -> HoneyLanguage.English.language
				HoneyLanguage.Chinese.code -> HoneyLanguage.Chinese.language
				HoneyLanguage.Japanese.code -> HoneyLanguage.Japanese.language
				HoneyLanguage.Russian.code -> HoneyLanguage.Russian.language
				HoneyLanguage.Korean.code -> HoneyLanguage.Korean.language
				HoneyLanguage.TraditionalChinese.code -> HoneyLanguage.TraditionalChinese.language
				else -> ""
			}
		}
		
		fun getLanguageSymbol(code: Int): String {
			return when (code) {
				HoneyLanguage.English.code -> HoneyLanguage.English.symbol
				HoneyLanguage.Chinese.code -> HoneyLanguage.Chinese.symbol
				HoneyLanguage.Japanese.code -> HoneyLanguage.Japanese.symbol
				HoneyLanguage.Russian.code -> HoneyLanguage.Russian.symbol
				HoneyLanguage.Korean.code -> HoneyLanguage.Korean.symbol
				HoneyLanguage.TraditionalChinese.code -> HoneyLanguage.TraditionalChinese.symbol
				else -> ""
			}
		}
		
		fun getCodeBySymbol(symbol: String): Int {
			return when (symbol.toUpperCase()) {
				HoneyLanguage.English.symbol -> HoneyLanguage.English.code
				HoneyLanguage.Chinese.symbol -> HoneyLanguage.Chinese.code
				HoneyLanguage.Japanese.symbol -> HoneyLanguage.Japanese.code
				HoneyLanguage.Russian.symbol -> HoneyLanguage.Russian.code
				HoneyLanguage.Korean.symbol -> HoneyLanguage.Korean.code
				HoneyLanguage.TraditionalChinese.symbol -> HoneyLanguage.TraditionalChinese.code
				else -> 100
			}
		}
		
		fun getCurrentSymbol(): String {
			return HoneyLanguage.getLanguageSymbol(Config.getCurrentLanguageCode())
		}
	}
}
