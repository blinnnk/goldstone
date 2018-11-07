package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import java.io.Serializable

/**
 * @date 2018/8/6 1:57 AM
 * @author KaySaith
 */


data class ChainAddresses(
	var eth: Bip44Address,
	var etc: Bip44Address,
	var btc: Bip44Address,
	var btcSeriesTest: Bip44Address,
	var ltc: Bip44Address,
	var bch: Bip44Address,
	var eos: Bip44Address
) : Serializable {

	constructor() : this(
		Bip44Address(),
		Bip44Address(),
		Bip44Address(),
		Bip44Address(),
		Bip44Address(),
		Bip44Address(),
		Bip44Address()
	)

	fun getAllAddresses(): List<String> {
		return listOf(eth.address, etc.address, btc.address, btcSeriesTest.address, ltc.address, bch.address, eos.address)
	}

	companion object {
		@JvmStatic
		val isBTCSeries: (address: String) -> Boolean = {
			CryptoValue.isBitcoinAddressLength(it) || it.contains(":")
		}
	}
}