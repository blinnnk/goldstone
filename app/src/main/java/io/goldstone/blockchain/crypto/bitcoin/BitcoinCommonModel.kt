package io.goldstone.blockchain.crypto.bitcoin

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoSymbol

/**
 * @date 2018/8/6 1:57 AM
 * @author KaySaith
 */
data class MultiChainPath(
	val ethPath: String,
	val etcPath: String,
	val btcPath: String,
	val testPath: String,
	val ltcPath: String,
	val bchPath: String,
	val eosPath: String
) {

	constructor() : this(
		"",
		"",
		"",
		"",
		"",
		"",
		""
	)
}

data class MultiChainAddresses(
	var ethAddress: String,
	var etcAddress: String,
	var btcAddress: String,
	var btcSeriesTestAddress: String,
	var ltcAddress: String,
	var bchAddress: String,
	var eosAddress: String
) {

	constructor() : this(
		"",
		"",
		"",
		"",
		"",
		"",
		""
	)

	constructor(address: String, symbol: String) : this(
		CryptoSymbol.eth.sameAs(symbol, address),
		CryptoSymbol.etc.sameAs(symbol, address),
		isBTCMainnetAddressOrEmpty(symbol, address),
		isBTCSeriesTestnetAddressOrEmpty(symbol, address),
		CryptoSymbol.ltc.sameAs(symbol, address),
		CryptoSymbol.bch.sameAs(symbol, address),
		CryptoSymbol.eos.sameAs(symbol, address)
		)

	// Ethereum Series
	constructor(address: String) : this(
		address,
		address,
		"",
		"",
		"",
		"",
		""
	)

	companion object {
		private fun String.sameAs(symbol: String, address: String): String {
			return if (symbol.equals(this, true)) address
			else ""
		}

		private fun isBTCMainnetAddressOrEmpty(symbol: String, address: String): String {
			return if (
				CryptoSymbol.pureBTCSymbol.equals(symbol, true) &&
				!Config.isTestEnvironment()
			) address
			else ""
		}

		private fun isBTCSeriesTestnetAddressOrEmpty(symbol: String, address: String): String {
			return if (CryptoSymbol.isBTCSeriesSymbol(symbol) && Config.isTestEnvironment()) address else ""
		}
	}
}