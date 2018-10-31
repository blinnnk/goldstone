package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.sharedpreference.SharedChain


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
	const val eos = "EOS"
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

	fun getChainNameByContract(contract: TokenContract?): String {
		return when {
			contract.isEOSSeries() -> eos
			contract.isLTC() -> ltc
			contract.isETC() -> etc
			contract.isBCH() -> bch
			contract.isBTC() -> btc
			else -> eth
		}
	}

	fun getBTCSeriesChainIDByName(name: String): ChainID? {
		return listOf(
			Pair(ltc, SharedChain.getLTCCurrent().chainID),
			Pair(bch, SharedChain.getBCHCurrent().chainID),
			Pair(btc, SharedChain.getBTCCurrent().chainID)
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