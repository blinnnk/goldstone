package io.goldstone.blockchain.crypto.multichain

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class CoinSymbol(val symbol: String) : Serializable {

	companion object {
		const val eth = "ETH"
		const val etc = "ETC"
		const val btc = "BTC"
		const val ltc = "LTC"
		const val bch = "BCH"
		const val erc = "ERC"
		const val eos = "EOS"
		@JvmStatic
		val ETH: CoinSymbol = CoinSymbol(eth)
		@JvmStatic
		val ETC: CoinSymbol = CoinSymbol(etc)
		@JvmStatic
		val BTC: CoinSymbol = CoinSymbol(btc)
		@JvmStatic
		val LTC: CoinSymbol = CoinSymbol(ltc)
		@JvmStatic
		val BCH: CoinSymbol = CoinSymbol(bch)
		@JvmStatic
		val EOS: CoinSymbol = CoinSymbol(eos)
		@JvmStatic
		val allBTCSeriesSymbol: () -> List<String> = {
			listOf(ltc, btc, bch)
		}
	}
}

fun CoinSymbol?.isEOS() = this?.symbol.equals(CoinSymbol.eos, true)
fun CoinSymbol?.isETH() = this?.symbol.equals(CoinSymbol.eth, true)
fun CoinSymbol?.isBTC() = this?.symbol.equals(CoinSymbol.btc, true)
fun CoinSymbol?.isLTC() = this?.symbol.equals(CoinSymbol.ltc, true)
fun CoinSymbol?.isBCH() = this?.symbol.equals(CoinSymbol.bch, true)
fun CoinSymbol?.isETC() = this?.symbol.equals(CoinSymbol.etc, true)
fun CoinSymbol?.isBTCSeries() = CoinSymbol.allBTCSeriesSymbol().any { it.equals(this?.symbol, true) }

fun CoinSymbol?.getContract(): TokenContract? {
	return when {
		isBTC() -> TokenContract.BTC
		isLTC() -> TokenContract.LTC
		isBCH() -> TokenContract.BCH
		isETC() -> TokenContract.ETC
		isETH() -> TokenContract.ETH
		isEOS() -> TokenContract.EOS
		else -> null // ERC20 Token 返回 `null` OR EOS Token
	}
}