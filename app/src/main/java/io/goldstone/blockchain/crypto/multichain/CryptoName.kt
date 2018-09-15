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

	fun getBTCSeriesChainIDByName(name: String): String? {
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

enum class ChainType(val id: Int) {
	BTC(0),
	AllTest(1),
	LTC(2),
	BCH(145),
	EOS(194),
	ETH(60),
	ETC(61),
	ERC(100); // 需要调大不然可能会和自然 `Type` 冲突

	companion object {
		private fun getAllBTCSeriesType(): List<Int> =
			listOf(LTC.id, AllTest.id, BTC.id, BCH.id)

		fun isBTCSeriesChainType(id: Int): Boolean =
			getAllBTCSeriesType().any { it == id }

		fun isSamePrivateKeyRule(id: Int): Boolean =
			listOf(BCH.id, BTC.id, AllTest.id).any { it == id }

		fun getChainTypeBySymbol(symbol: String?): Int = when (symbol) {
			CoinSymbol.btc() -> BTC.id
			CoinSymbol.ltc -> LTC.id
			CoinSymbol.eth -> ETH.id
			CoinSymbol.etc -> ETC.id
			CoinSymbol.bch -> BCH.id
			CoinSymbol.eos -> EOS.id
			else -> ETH.id
		}
	}
}