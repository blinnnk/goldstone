package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.sharedpreference.SharedWallet

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
	val privacy = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/privacy"
	@JvmStatic
	val terms = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/termAndConditions"
	@JvmStatic
	val support = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/support"
	@JvmStatic
	val helpCenter = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/helpCenter"
	@JvmStatic
	val aboutUs = "$header/${webLanguage(SharedWallet.getCurrentLanguageCode())}/aboutUs"
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
	const
	val backUpServer = "https://goldstone-api1.naonaola.com"
	const
	val backUpSocket = "wss://goldstone-api1.naonaola.com/ws"
	const val normalServer = "https://api1.goldstone.io"
	const val normalSocket = "wss://api1.goldstone.io/ws"

	// BTC Transaction Data API From `blockChain.info`
	const val btcMain = "https://insight.bitpay.com"
	const val btcTest = "https://test-insight.bitpay.com"
	const val backupBtcTest = "https://testnet.blockchain.info"
	const val backUpBtcMain = "https://www.blockchain.info"
	// LTC Transaction Data API From `insight`
	const val ltcMain = "https://insight.litecore.io"
	const val ltcTest = "https://testnet.litecore.io"
	// BCH Transaction Data API From `insight`
	const val bchMain = "https://bch-insight.bitpay.com"
	const val bchTest = "https://test-bch-insight.bitpay.com"
}