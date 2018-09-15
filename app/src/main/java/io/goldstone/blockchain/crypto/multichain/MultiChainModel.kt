package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.value.Config

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

		private fun isBTCMainnetAddressOrEmpty(symbol: String, address: String): String {
			return if (
				CoinSymbol.pureBTCSymbol.equals(symbol, true) &&
				!Config.isTestEnvironment()
			) address
			else ""
		}

		private fun isBTCSeriesTestnetAddressOrEmpty(symbol: String, address: String): String {
			return if (CoinSymbol(symbol).isBTCSeries() && Config.isTestEnvironment()) address else ""
		}
	}
}