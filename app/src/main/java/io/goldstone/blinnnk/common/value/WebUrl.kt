package io.goldstone.blinnnk.common.value

import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet

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
	val privacy = "$header/#privacyPolicy"
	@JvmStatic
	val terms = "$header/#term"
	@JvmStatic
	val support = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/support"
	@JvmStatic
	val helpCenter = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/helpCenter"
	@JvmStatic
	val aboutUs = "$header/#aboutUs"
	@JvmStatic
	var whatIsKeystore = "$header/wiki/${webLanguage(SharedWallet.getCurrentLanguageCode())}/keystore"
	@JvmStatic
	val whatIsMnemonic = "$header/wiki/${webLanguage(SharedWallet.getCurrentLanguageCode())}/mnemonics"
	@JvmStatic
	val whatIsPrivatekey = "$header/wiki/${webLanguage(SharedWallet.getCurrentLanguageCode())}/privateKey"
	@JvmStatic
	val whatIsWatchOnly =
		"$header/wiki/${webLanguage(SharedWallet.getCurrentLanguageCode())}/watchOnlyWallet"
	@JvmStatic
	val whatIsGas = "$header/wiki/${webLanguage(SharedWallet.getCurrentLanguageCode())}/gas"
	const val backUpServer = "https://goldstone-api1.naonaola.com"
	const val backUpSocket = "wss://goldstone-api1.naonaola.com/ws"
	const val normalServer = "https://api3.goldstone.io"
	const val normalSocket = "wss://api3.goldstone.io/ws"
	// BTC Transaction Data API From `blockChain.info`
	const val backupBtcTest = "https://testnet.blockchain.info"
	const val backUpBtcMain = "https://www.blockchain.info"

	/** Third Party Insight API*/
	// LTC
	const val ltcMain = "https://insight.litecore.io/api"
	const val ltcTest = "https://testnet.litecore.io/api"
	// BCH
	const val bchMain = "https://bch-insight.bitpay.com/api"
	const val bchTest = "https://test-bch-insight.bitpay.com/api"
	// BTC
	const val btcMain = "https://insight.bitpay.com/api"
	const val btcTest = "https://test-insight.bitpay.com/api"
}