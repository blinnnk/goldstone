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
	val privacy = "$header/${webLanguage(Config.getCurrentLanguage())}/privacy"
	@JvmStatic
	val terms = "$header/${webLanguage(Config.getCurrentLanguage())}/termAndConditions"
	@JvmStatic
	val support = "$header/${webLanguage(Config.getCurrentLanguage())}/support"
	@JvmStatic
	val aboutUs = "$header/${webLanguage(Config.getCurrentLanguage())}/aboutUs"
	@JvmStatic
	var whatIsKeystore = "$header/wiki/${webLanguage(Config.getCurrentLanguage())}/keystore"
	@JvmStatic
	val whatIsMnemonic = "$header/wiki/${webLanguage(Config.getCurrentLanguage())}/mnemonics"
	@JvmStatic
	val whatIsPrivatekey = "$header/wiki/${webLanguage(Config.getCurrentLanguage())}/privateKey"
	@JvmStatic
	val whatIsWatchOnly = "$header/wiki/${webLanguage(Config.getCurrentLanguage())}/watchOnlyWallet"
	@JvmStatic
	val whatIsGas = "$header/wiki/${webLanguage(Config.getCurrentLanguage())}/gas"
}