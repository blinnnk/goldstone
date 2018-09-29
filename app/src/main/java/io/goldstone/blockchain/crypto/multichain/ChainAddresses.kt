package io.goldstone.blockchain.crypto.multichain

import java.io.Serializable

/**
 * @date 2018/8/6 1:57 AM
 * @author KaySaith
 */


data class ChainAddresses(
	var eth: String,
	var etc: String,
	var btc: String,
	var btcSeriesTest: String,
	var ltc: String,
	var bch: String,
	var eos: String
) : Serializable {

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
		@JvmStatic
		val isBTCSeries: (address: String) -> Boolean = {
			CryptoValue.isBitcoinAddressLength(it) || it.contains(":")
		}
	}
}