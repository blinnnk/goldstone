package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.crypto.CryptoSymbol

/**
 * @date 2018/5/25 8:14 PM
 * @author KaySaith
 */
enum class ChainID(val id: String) {

	Main("1"),
	Ropsten("3"),
	Rinkeby("4"),
	Kovan("42"),
	ETCMain("61"),
	ETCTest("62"),
	BTCMain("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"),
	BTCTest("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"),
	BCHMain("00000000000000000019f112ec0a9982926f1258cdcc558dd7c3b7e5dc7fa148"),
	BCHTest("00000000000e38fef93ed9582a7df43815d5c2ba9fd37ef70c9a0ea4a285b8f5"),
	LTCMain("12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2"),
	LTCTest("4966625a4b2851d9fdee139e56211a0d88575f59ed816ff5e6a63deb4e3e29a0");

	companion object {

		fun getChainIDBySymbol(symbol: String): String {
			return when {
				symbol.equals(CryptoSymbol.btc(), true) -> {
					if (Config.isTestEnvironment()) ChainID.BTCTest.id
					else ChainID.BTCMain.id
				}

				symbol.equals(CryptoSymbol.etc, true) ->
					Config.getETCCurrentChain()
				symbol.equals(CryptoSymbol.ltc, true) ->
					Config.getLTCCurrentChain()
				symbol.equals(CryptoSymbol.bch, true) ->
					Config.getBCHCurrentChain()
				else -> Config.getCurrentChain()
			}
		}

		fun getTestChains(): ArrayList<String> {
			return arrayListOf(
				ChainID.ETCTest.id,
				ChainID.BTCTest.id,
				ChainID.Ropsten.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id,
				ChainID.LTCTest.id,
				ChainID.BCHTest.id
			)
		}

		fun getAllChainID(): ArrayList<String> {
			return arrayListOf(
				ChainID.Main.id,
				ChainID.Ropsten.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id,
				ChainID.ETCTest.id,
				ChainID.ETCMain.id,
				ChainID.BTCMain.id,
				ChainID.BTCTest.id,
				ChainID.LTCMain.id,
				ChainID.BCHMain.id
			)
		}

		fun getAllETCChainID(): ArrayList<String> {
			return arrayListOf(
				ChainID.ETCTest.id,
				ChainID.ETCMain.id
			)
		}

		fun getAllEthereumChainID(): List<String> {
			return listOf(
				ChainID.Main.id,
				ChainID.Ropsten.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id
			)
		}

		fun getChainNameByID(chainID: String): String {
			return when (chainID) {
				// Third Party Nodes
				Kovan.id -> ChainText.infuraKovan
				Ropsten.id -> ChainText.infuraRopsten
				Main.id -> ChainText.infuraMain
				Rinkeby.id -> ChainText.infuraRinkeby
				ETCTest.id -> ChainText.etcMorden
				ETCMain.id -> ChainText.etcMainGasTracker
				BTCMain.id -> ChainText.btcMain
				LTCMain.id -> ChainText.ltcMain
				BCHMain.id -> ChainText.bchMain
				// GoldStone Nodes
				Main.id -> ChainText.goldStoneMain
				Ropsten.id -> ChainText.ropsten
				Kovan.id -> ChainText.kovan
				Rinkeby.id -> ChainText.rinkeby
				ETCMain.id -> ChainText.goldStoneEtcMain
				ETCTest.id -> ChainText.goldStoneEtcMordenTest
				BTCTest.id -> ChainText.btcTest
				LTCTest.id -> ChainText.ltcTest
				BCHTest.id -> ChainText.bchTest
				else -> ChainText.goldStoneMain
			}
		}

		fun getChainIDByName(name: String): String {
			return when (name) {
				// GoldStone ERC Node
				ChainText.goldStoneMain -> Main.id
				ChainText.ropsten -> Ropsten.id
				ChainText.kovan -> Kovan.id
				ChainText.rinkeby -> Rinkeby.id
				// Infura ERC Node
				ChainText.infuraMain -> Main.id
				ChainText.infuraRopsten -> Ropsten.id
				ChainText.infuraKovan -> Kovan.id
				ChainText.infuraRinkeby -> Rinkeby.id
				// ETC Node
				ChainText.etcMorden -> ETCTest.id
				ChainText.goldStoneEtcMain -> ETCMain.id
				ChainText.goldStoneEtcMordenTest -> ETCTest.id
				ChainText.etcMainGasTracker -> ETCMain.id
				// BTC Node
				ChainText.btcMain -> BTCMain.id
				ChainText.btcTest -> BTCTest.id
				// LTC Node
				ChainText.ltcMain -> LTCMain.id
				ChainText.ltcTest -> LTCTest.id
				// BCH Node
				ChainText.bchMain -> BCHMain.id
				ChainText.bchTest -> BCHTest.id
				else -> Main.id
			}
		}
	}
}

// `ChainName ID` 这个值是用来通过国际化的 `Name` 找回对应的 `ID` 的值
enum class ChainNameID(val id: Int) {
	GoldStoneETHMain(0),
	GoldStoneRopsten(1),
	GoldStoneRinkeby(2),
	GoldStoneKovan(3),
	InfuraETHMain(4),
	InfuraRopsten(5),
	InfuraRinkeby(6),
	InfuraKovan(7),
	GoldStoneETCMain(8),
	GoldStoneETCMorden(9),
	GasTrackerETCMain(10),
	GasTrackerETCMorden(11),
	GoldStoneBTCMain(12),
	GoldStoneBTCTest(13),
	GoldStoneLTC(14),
	GoldStoneLTCTest(15),
	GoldStoneBCHMain(16),
	GoldStoneBCHTest(17);

	companion object {
		fun getChainNameByID(chainNameID: Int): String {
			return when (chainNameID) {
				0 -> ChainText.goldStoneMain
				1 -> ChainText.ropsten
				2 -> ChainText.rinkeby
				3 -> ChainText.kovan
				4 -> ChainText.infuraMain
				5 -> ChainText.infuraRopsten
				6 -> ChainText.infuraRinkeby
				7 -> ChainText.infuraKovan
				8 -> ChainText.goldStoneEtcMain
				9 -> ChainText.goldStoneEtcMordenTest
				10 -> ChainText.etcMainGasTracker
				11 -> ChainText.etcMorden
				12 -> ChainText.btcMain
				13 -> ChainText.btcTest
				14 -> ChainText.ltcMain
				15 -> ChainText.ltcTest
				16 -> ChainText.bchMain
				17 -> ChainText.bchTest
				else -> ChainText.bchTest
			}
		}

		fun getChainNameIDByName(chainName: String): Int {
			return when (chainName) {
				ChainText.goldStoneMain -> 0
				ChainText.ropsten -> 1
				ChainText.rinkeby -> 2
				ChainText.kovan -> 3
				ChainText.infuraMain -> 4
				ChainText.infuraRopsten -> 5
				ChainText.infuraRinkeby -> 6
				ChainText.infuraKovan -> 7
				ChainText.goldStoneEtcMain -> 8
				ChainText.goldStoneEtcMordenTest -> 9
				ChainText.etcMainGasTracker -> 10
				ChainText.etcMorden -> 11
				ChainText.btcMain -> 12
				ChainText.btcTest -> 13
				ChainText.ltcMain -> 14
				ChainText.ltcTest -> 15
				ChainText.bchMain -> 16
				ChainText.bchTest -> 17
				else -> 17
			}
		}
	}
}