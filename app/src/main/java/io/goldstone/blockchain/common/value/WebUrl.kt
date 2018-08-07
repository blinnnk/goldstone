package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.common.language.HoneyLanguage

/**
 * @date 2018/5/15 4:44 PM
 * @author KaySaith
 */
object WebUrl {
	
	val webLanguage: (currentLanguageCode: Int) -> String = {
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
	val helpCenter = "$header/${webLanguage(Config.getCurrentLanguageCode())}/helpCenter"
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
	const
	val backUpServer = "https://goldstone-api1.naonaola.com"
	const
	val backUpSocket = "wss://goldstone-api1.naonaola.com/ws"
	const val normalServer = "https://api1.goldstone.io"
	const val normalSocket = "wss://api1.goldstone.io/ws"
	const val btcMain = "https://blockchain.info"
	const val btcTest = "https://testnet.blockchain.info"
}