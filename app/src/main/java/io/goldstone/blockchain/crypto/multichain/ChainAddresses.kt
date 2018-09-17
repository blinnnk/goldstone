package io.goldstone.blockchain.crypto.multichain

import java.io.Serializable

/**
 * @date 2018/8/6 1:57 AM
 * @author KaySaith
 */


data class ChainAddresses(
	var ethAddress: String,
	var etcAddress: String,
	var btcAddress: String,
	var btcSeriesTestAddress: String,
	var ltcAddress: String,
	var bchAddress: String,
	var eosAddress: String
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
		val isBTCSeriesAddress: (address: String) -> Boolean = {
			CryptoValue.isBitcoinAddressLength(it) || it.contains(":")
		}
	}
}