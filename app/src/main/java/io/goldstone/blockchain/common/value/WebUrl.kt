package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.GoldStoneApp

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
	val privacy = "$header/${webLanguage(GoldStoneApp.getCurrentLanguage())}/privacy"
	@JvmStatic
	val terms = "$header/${webLanguage(GoldStoneApp.getCurrentLanguage())}/termAndConditions"
	@JvmStatic
	val support = "$header/${webLanguage(GoldStoneApp.getCurrentLanguage())}/support"
	@JvmStatic
	val aboutUs = "$header/${webLanguage(GoldStoneApp.getCurrentLanguage())}/aboutUs"
	@JvmStatic
	var whatIsKeystore = "$header/wiki/${webLanguage(GoldStoneApp.getCurrentLanguage())}/keystore"
	@JvmStatic
	val whatIsMnemonic = "$header/wiki/${webLanguage(GoldStoneApp.getCurrentLanguage())}/mnemonics"
	@JvmStatic
	val whatIsPrivatekey = "$header/wiki/${webLanguage(GoldStoneApp.getCurrentLanguage())}/privateKey"
	@JvmStatic
	val whatIsWatchOnly = "$header/wiki/${webLanguage(GoldStoneApp.getCurrentLanguage())}/watchOnlyWallet"
	@JvmStatic
	val whatIsGas = "$header/wiki/${webLanguage(GoldStoneApp.getCurrentLanguage())}/gas"
}