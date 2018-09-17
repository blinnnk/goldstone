package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.value.Config
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/17
 */
class ChainType(val id: Int) : Serializable {

	fun isBTC() = id == MultiChainType.BTC.id
	fun isLTC() = id == MultiChainType.LTC.id
	fun isEOS() = id == MultiChainType.EOS.id
	fun isETH() = id == MultiChainType.ETH.id
	fun isETC() = id == MultiChainType.ETC.id
	fun isBCH() = id == MultiChainType.BCH.id

	fun getCurrentChainName(): String {
		return when (id) {
			MultiChainType.ETH.id -> Config.getCurrentChainName()
			MultiChainType.ETC.id -> Config.getETCCurrentChainName()
			MultiChainType.BTC.id -> Config.getBTCCurrentChainName()
			MultiChainType.LTC.id -> Config.getLTCCurrentChainName()
			MultiChainType.BCH.id -> Config.getBCHCurrentChainName()
			MultiChainType.EOS.id -> Config.getEOSCurrentChainName()
			else -> Config.getCurrentChainName()
		}
	}

	fun getMainnetChainName(): String {
		return when (id) {
			MultiChainType.ETH.id -> {
				if (Config.getCurrentChain() != ChainID.ethMain) ChainText.infuraMain
				else Config.getCurrentChainName()
			}

			MultiChainType.BTC.id -> ChainText.btcMain
			MultiChainType.LTC.id -> ChainText.ltcMain
			MultiChainType.BCH.id -> ChainText.bchMain
			MultiChainType.EOS.id -> ChainText.eosMain

			else -> {
				if (Config.getETCCurrentChain() != ChainID.ethMain) ChainText.etcMainGasTracker
				else Config.getETCCurrentChainName()
			}
		}
	}

	fun getTestnetChainName(): String {
		return when (id) {
			MultiChainType.ETH.id -> {
				if (Config.getCurrentChain() == ChainID.ethMain) ChainText.infuraRopsten
				else Config.getCurrentChainName()
			}
			MultiChainType.BTC.id -> ChainText.btcTest
			MultiChainType.LTC.id -> ChainText.ltcTest
			MultiChainType.BCH.id -> ChainText.bchTest
			MultiChainType.EOS.id -> ChainText.eosTest
			else -> {
				if (Config.getETCCurrentChain() == ChainID.etcMain) ChainText.etcMorden
				else Config.getETCCurrentChainName()
			}
		}
	}

	companion object {

		// 比特的 `Bip44` 的比特币测试地址的  `CoinType` 为 `1`
		val isBTCTest: (chainType: Int) -> Boolean = {
			it == MultiChainType.AllTest.id
		}

		private fun getAllBTCSeriesType(): List<Int> =
			listOf(MultiChainType.LTC.id, MultiChainType.AllTest.id, MultiChainType.BTC.id, MultiChainType.BCH.id)

		fun isBTCSeriesChainType(id: Int): Boolean =
			getAllBTCSeriesType().any { it == id }

		fun isSamePrivateKeyRule(id: Int): Boolean =
			listOf(MultiChainType.BCH.id, MultiChainType.BTC.id, MultiChainType.AllTest.id).any { it == id }

		fun getChainTypeBySymbol(symbol: String?): Int = when (symbol) {
			CoinSymbol.btc() -> MultiChainType.BTC.id
			CoinSymbol.ltc -> MultiChainType.LTC.id
			CoinSymbol.eth -> MultiChainType.ETH.id
			CoinSymbol.etc -> MultiChainType.ETC.id
			CoinSymbol.bch -> MultiChainType.BCH.id
			CoinSymbol.eos -> MultiChainType.EOS.id
			else -> MultiChainType.ETH.id
		}
	}
}

enum class MultiChainType(val id: Int) {
	BTC(0),
	AllTest(1),
	LTC(2),
	BCH(145),
	EOS(194),
	ETH(60),
	ETC(61),
	ERC(100); // 需要调大不然可能会和自然 `Type` 冲突
}