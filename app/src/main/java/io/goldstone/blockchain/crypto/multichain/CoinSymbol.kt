package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.value.Config
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class CoinSymbol(val symbol: String?) : Serializable {
	fun isEOS(): Boolean {
		return symbol.equals(CoinSymbol.eos, true)
	}

	fun isETH(): Boolean {
		return symbol.equals(CoinSymbol.etc, true)
	}

	fun isBTC(): Boolean {
		return symbol.equals(CoinSymbol.btc(), true)
	}

	fun isLTC(): Boolean {
		return symbol.equals(CoinSymbol.ltc, true)
	}

	fun isBCH(): Boolean {
		return symbol.equals(CoinSymbol.bch, true)
	}

	fun isETC(): Boolean {
		return symbol.equals(CoinSymbol.etc, true)
	}

	fun isBTCSeries(): Boolean {
		return allBTCSeriesSymbol().any { it.equals(symbol, true) }
	}

	fun getAddress(isEOSAccountName: Boolean = true): String {
		return when {
			CoinSymbol(symbol).isBTC() ->
				AddressUtils.getCurrentBTCAddress()
			CoinSymbol(symbol).isLTC() ->
				AddressUtils.getCurrentLTCAddress()
			CoinSymbol(symbol).isBCH() ->
				AddressUtils.getCurrentBCHAddress()
			CoinSymbol(symbol).isETC() ->
				Config.getCurrentETCAddress()
			CoinSymbol(symbol).isEOS() ->
				if (isEOSAccountName) Config.getCurrentEOSName()
				else Config.getCurrentEOSAddress()
			else ->
				Config.getCurrentEthereumAddress()
		}
	}

	fun getChainID(): String {
		return when {
			symbol.equals(CoinSymbol.btc(), true) ->
				Config.getBTCCurrentChain()
			symbol.equals(CoinSymbol.etc, true) ->
				Config.getETCCurrentChain()
			symbol.equals(CoinSymbol.ltc, true) ->
				Config.getLTCCurrentChain()
			symbol.equals(CoinSymbol.bch, true) ->
				Config.getBCHCurrentChain()
			symbol.equals(CoinSymbol.eos, true) ->
				Config.getEOSCurrentChain()
			else -> Config.getCurrentChain()
		}
	}

	fun getCurrentChainName(): String {
		return when {
			CoinSymbol(symbol).isETH() -> Config.getCurrentChainName()
			CoinSymbol(symbol).isETC() -> Config.getETCCurrentChainName()
			CoinSymbol(symbol).isBTC() -> Config.getBTCCurrentChainName()
			CoinSymbol(symbol).isLTC() -> Config.getLTCCurrentChainName()
			CoinSymbol(symbol).isBCH() -> Config.getBCHCurrentChainName()
			CoinSymbol(symbol).isEOS() -> Config.getEOSCurrentChainName()
			else -> Config.getCurrentChainName()
		}
	}

	companion object {
		fun getETH(): CoinSymbol = CoinSymbol(eth)
		fun getETC(): CoinSymbol = CoinSymbol(etc)
		fun getBTC(): CoinSymbol = CoinSymbol(pureBTCSymbol)
		fun getLTC(): CoinSymbol = CoinSymbol(ltc)
		fun getBCH(): CoinSymbol = CoinSymbol(bch)
		fun getEOS(): CoinSymbol = CoinSymbol(eos)
		const val eth = "ETH"
		const val etc = "ETC"
		const val pureBTCSymbol = "BTC"
		const val ltc = "LTC"
		const val bch = "BCH"
		const val erc = "ERC"
		const val eos = "EOS"
		@JvmStatic
		val btc: () -> String = {
			if (Config.getYingYongBaoInReviewStatus()) "B.C." else "BTC"
		}
		@JvmStatic
		val allBTCSeriesSymbol: () -> List<String> = {
			listOf(ltc, btc(), bch)
		}

		fun updateSymbolIfInReview(symbol: String, isTest: Boolean = false): String {
			return if (
				symbol.contains(CoinSymbol.pureBTCSymbol, true) &&
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
}