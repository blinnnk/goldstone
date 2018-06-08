package io.goldstone.blockchain.common.value

/**
 * @date 2018/5/15 4:44 PM
 * @author KaySaith
 */
object WebUrl {
	
	private val webLanguage: (currentLanguageCode: Int) -> String = {
		when (it) {
			HoneyLanguage.Chinese.code -> "zh-s"
			else -> "en"
		}
	}
	const val header = "https://www.goldstone.io"
	@JvmStatic
	val privacy = "$header/${webLanguage(Config.getCurrentLanguageCode())}/privacy"
	@JvmStatic
	val terms = "$header/${webLanguage(Config.getCurrentLanguageCode())}/termAndConditions"
	@JvmStatic
	val support = "$header/${webLanguage(Config.getCurrentLanguageCode())}/support"
	@JvmStatic
	val aboutUs = "$header/${webLanguage(Config.getCurrentLanguageCode())}/aboutUs"
	@JvmStatic
	var whatIsKeystore = "$header/wiki/${webLanguage(Config.getCurrentLanguageCode())}/keystore"
	@JvmStatic
	val whatIsMnemonic = "$header/wiki/${webLanguage(Config.getCurrentLanguageCode())}/mnemonics"
	@JvmStatic
	val whatIsPrivatekey = "$header/wiki/${webLanguage(Config.getCurrentLanguageCode())}/privateKey"
	@JvmStatic
	val whatIsWatchOnly =
		"$header/wiki/${webLanguage(Config.getCurrentLanguageCode())}/watchOnlyWallet"
	@JvmStatic
	val whatIsGas = "$header/wiki/${webLanguage(Config.getCurrentLanguageCode())}/gas"
}