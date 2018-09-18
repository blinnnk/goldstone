package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.value.Config


/**
 * @author KaySaith
 * @date  2018/09/07
 */

object CryptoName {
	const val eth = "Ethereum"
	const val etc = "Ethereum Classic"
	const val btc = "Bitcoin"
	const val ltc = "Litecoin"
	const val bch = "Bitcoin Cash"
	const val eos = "Eos.io"
	val allChainName = listOf(
		etc.replace(" ", ""),
		eth,
		btc,
		ltc,
		bch.replace(" ", ""),
		eos
	)

	fun getChainNameBySymbol(symbol: String?): String {
		return when (symbol) {
			CoinSymbol.eos -> eos
			CoinSymbol.ltc -> ltc
			CoinSymbol.etc -> etc
			CoinSymbol.bch -> bch
			CoinSymbol.btc() -> btc
			else -> eth
		}
	}

	fun getBTCSeriesChainIDByName(name: String): ChainID? {
		return listOf(
			Pair(ltc, Config.getLTCCurrentChain()),
			Pair(bch, Config.getBCHCurrentChain()),
			Pair(btc, Config.getBTCCurrentChain())
		).firstOrNull {
			it.first
				.replace(" ", "")
				.equals(name, true)
		}?.second
	}

	fun getBTCSeriesContractByChainName(name: String): String? {
		return listOf(
			Pair(ltc, TokenContract.ltcContract),
			Pair(bch, TokenContract.bchContract),
			Pair(btc, TokenContract.btcContract)
		).firstOrNull {
			it.first
				.replace(" ", "")
				.equals(name, true)
		}?.second
	}
}