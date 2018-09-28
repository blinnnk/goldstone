package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.AddressUtils
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class CoinSymbol(val symbol: String?) : Serializable {
	fun getAddress(isEOSAccountName: Boolean = true): String {
		return when {
			CoinSymbol(symbol).isBTC() ->
				AddressUtils.getCurrentBTCAddress()
			CoinSymbol(symbol).isLTC() ->
				AddressUtils.getCurrentLTCAddress()
			CoinSymbol(symbol).isBCH() ->
				AddressUtils.getCurrentBCHAddress()
			CoinSymbol(symbol).isETC() ->
				SharedAddress.getCurrentETC()
			CoinSymbol(symbol).isEOS() ->
				if (isEOSAccountName) SharedAddress.getCurrentEOSAccount().accountName
				else SharedAddress.getCurrentEOS()
			else ->
				SharedAddress.getCurrentEthereum()
		}
	}

	fun getChainID(): ChainID {
		return when {
			symbol.equals(CoinSymbol.btc(), true) ->
				SharedChain.getBTCCurrent()
			symbol.equals(CoinSymbol.etc, true) ->
				SharedChain.getETCCurrent()
			symbol.equals(CoinSymbol.ltc, true) ->
				SharedChain.getLTCCurrent()
			symbol.equals(CoinSymbol.bch, true) ->
				SharedChain.getBCHCurrent()
			symbol.equals(CoinSymbol.eos, true) ->
				SharedChain.getEOSCurrent()
			else -> SharedChain.getCurrentETH()
		}
	}

	fun getCurrentChainName(): String {
		return when {
			isETH() -> SharedChain.getCurrentETHName()
			isETC() -> SharedChain.getETCCurrentName()
			isBTC() -> SharedChain.getBTCCurrentName()
			isLTC() -> SharedChain.getLTCCurrentName()
			isBCH() -> SharedChain.getBCHCurrentName()
			isEOS() -> SharedChain.getEOSCurrentName()
			else -> SharedChain.getCurrentETHName()
		}
	}

	companion object {
		const val eth = "ETH"
		const val etc = "ETC"
		const val pureBTCSymbol = "BTC"
		const val ltc = "LTC"
		const val bch = "BCH"
		const val erc = "ERC"
		const val eos = "EOS"
		@JvmStatic
		val ETH: CoinSymbol = CoinSymbol(eth)
		@JvmStatic
		val ETC: CoinSymbol = CoinSymbol(etc)
		@JvmStatic
		val BTC: CoinSymbol = CoinSymbol(pureBTCSymbol)
		@JvmStatic
		val LTC: CoinSymbol = CoinSymbol(ltc)
		@JvmStatic
		val BCH: CoinSymbol = CoinSymbol(bch)
		@JvmStatic
		val EOS: CoinSymbol = CoinSymbol(eos)
		@JvmStatic
		val btc: () -> String = {
			if (SharedWallet.getYingYongBaoInReviewStatus()) "B.C." else "BTC"
		}
		@JvmStatic
		val allBTCSeriesSymbol: () -> List<String> = {
			listOf(ltc, btc(), bch)
		}

		fun updateSymbolIfInReview(symbol: String, isTest: Boolean = false): String {
			return if (
				symbol.contains(CoinSymbol.pureBTCSymbol, true) &&
				SharedWallet.getYingYongBaoInReviewStatus()
			) "B.C." + if (isTest) " Test" else ""
			else symbol
		}

		fun updateNameIfInReview(name: String): String {
			return if (
				name.contains("Bitcoin", true) &&
				SharedWallet.getYingYongBaoInReviewStatus()
			) "Bitc."
			else name
		}
	}
}

fun CoinSymbol?.isEOS() = this?.symbol.equals(CoinSymbol.eos, true)
fun CoinSymbol?.isETH() = this?.symbol.equals(CoinSymbol.eth, true)
fun CoinSymbol?.isBTC() = this?.symbol.equals(CoinSymbol.btc(), true)
fun CoinSymbol?.isLTC() = this?.symbol.equals(CoinSymbol.ltc, true)
fun CoinSymbol?.isBCH() = this?.symbol.equals(CoinSymbol.bch, true)
fun CoinSymbol?.isETC() = this?.symbol.equals(CoinSymbol.etc, true)
fun CoinSymbol?.isBTCSeries() = CoinSymbol.allBTCSeriesSymbol().any { it.equals(this?.symbol, true) }

fun CoinSymbol?.getContract(): TokenContract? {
	return when {
		CoinSymbol(this?.symbol).isBTC() ->
			TokenContract.BTC
		CoinSymbol(this?.symbol).isLTC() ->
			TokenContract.LTC
		CoinSymbol(this?.symbol).isBCH() ->
			TokenContract.BCH
		CoinSymbol(this?.symbol).isETC() ->
			TokenContract.ETC
		CoinSymbol(this?.symbol).isETH() ->
			TokenContract.ETH
		CoinSymbol(this?.symbol).isEOS() ->
			TokenContract.EOS
		else -> null // ERC20 Token 返回 `null`
	}
}

fun CoinSymbol?.getChainSymbol(): CoinSymbol {
	return when {
		isETC() -> CoinSymbol.ETC
		isBTC() -> CoinSymbol.BTC
		isLTC() -> CoinSymbol.LTC
		isBCH() -> CoinSymbol.BCH
		isEOS() -> CoinSymbol.EOS
		else -> CoinSymbol.ETH
	}
}