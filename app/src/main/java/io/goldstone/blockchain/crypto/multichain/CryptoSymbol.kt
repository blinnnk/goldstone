package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.value.Config


/**
 * @author KaySaith
 * @date  2018/09/07
 */
object CryptoSymbol {
	const val eth = "ETH"
	const val etc = "ETC"
	const val pureBTCSymbol = "BTC"
	const val ltc = "LTC"
	const val bch = "BCH"
	const val erc = "ERC"
	const val eos = "EOS"
	val btc: () -> String = {
		if (Config.getYingYongBaoInReviewStatus()) "B.C." else "BTC"
	}

	val allBTCSeriesSymbol: () -> List<String> = {
		listOf(ltc, btc(), bch)
	}

	fun isBTCSeriesSymbol(symbol: String?): Boolean {
		return allBTCSeriesSymbol().any { it.equals(symbol, true) }
	}

	fun updateSymbolIfInReview(symbol: String, isTest: Boolean = false): String {
		return if (
			symbol.contains(pureBTCSymbol, true) &&
			Config.getYingYongBaoInReviewStatus()
		) "B.C." + if (isTest) " Test" else ""
		else symbol
	}

	fun updateNameIfInReview(name: String): String {
		return if (
			name.contains("Bitcoin", true) &&
			Config.getYingYongBaoInReviewStatus()
		) "Bitc."
		else name
	}
}