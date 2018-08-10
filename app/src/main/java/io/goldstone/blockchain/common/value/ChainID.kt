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
	LTCMain("12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2");

	companion object {

		fun getChainIDBySymbol(symbol: String): String {
			return when {
				symbol.equals(CryptoSymbol.btc(), true) -> {
					if (Config.isTestEnvironment()) ChainID.BTCTest.id
					else ChainID.BTCMain.id
				}

				symbol.equals(CryptoSymbol.etc, true) ->
					Config.getETCCurrentChain()
				else -> Config.getCurrentChain()
			}
		}

		fun getTestChains(): ArrayList<String> {
			return arrayListOf(
				ChainID.ETCTest.id,
				ChainID.BTCTest.id,
				ChainID.Ropsten.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id
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
				ChainID.BTCTest.id
			)
		}

		fun getAllETCChainID(): ArrayList<String> {
			return arrayListOf(
				ChainID.ETCTest.id,
				ChainID.ETCMain.id
			)
		}

		fun getAllEthereumChainID(): ArrayList<String> {
			return arrayListOf(
				ChainID.Main.id,
				ChainID.Ropsten.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id
			)
		}

		fun getChainNameByID(chainID: String): String {
			return when (chainID) {
				// Third Pardy Nodes
				Kovan.id -> ChainText.infuraKovan
				Ropsten.id -> ChainText.infuraRopsten
				Main.id -> ChainText.infuraMain
				Rinkeby.id -> ChainText.infuraRinkeby
				ETCTest.id -> ChainText.etcMorden
				ETCMain.id -> ChainText.etcMainGasTracker
				BTCMain.id -> ChainText.btcMain
				// GoldStone Nodes
				Main.id -> ChainText.goldStoneMain
				Ropsten.id -> ChainText.ropsten
				Kovan.id -> ChainText.kovan
				Rinkeby.id -> ChainText.rinkeby
				ETCMain.id -> ChainText.goldStoneEtcMain
				ETCTest.id -> ChainText.goldStoneEtcMorderTest
				BTCTest.id -> ChainText.btcTest
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
				ChainText.goldStoneEtcMorderTest -> ETCTest.id
				ChainText.etcMainGasTracker -> ETCMain.id
				// BTC Node
				ChainText.btcMain -> BTCMain.id
				ChainText.btcTest -> BTCTest.id
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
	GoldStoneBTCTest(13);

	companion object {
		fun getChainNameByID(chainNameID: Int): String {
			return when (chainNameID) {
				0 -> ChainText.mainnet
				1 -> ChainText.ropsten
				2 -> ChainText.rinkeby
				3 -> ChainText.kovan
				4 -> ChainText.infuraMain
				5 -> ChainText.infuraRopsten
				6 -> ChainText.infuraRinkeby
				7 -> ChainText.infuraKovan
				8 -> ChainText.goldStoneEtcMain
				9 -> ChainText.goldStoneEtcMorderTest
				10 -> ChainText.etcMainGasTracker
				11 -> ChainText.etcMorden
				12 -> ChainText.btcMain
				else -> ChainText.btcTest
			}
		}

		fun getChainNameIDByName(chainName: String): Int {
			return when (chainName) {
				ChainText.mainnet -> 0
				ChainText.ropsten -> 1
				ChainText.rinkeby -> 2
				ChainText.kovan -> 3
				ChainText.infuraMain -> 4
				ChainText.infuraRopsten -> 5
				ChainText.infuraRinkeby -> 6
				ChainText.infuraKovan -> 7
				ChainText.goldStoneEtcMain -> 8
				ChainText.goldStoneEtcMorderTest -> 9
				ChainText.etcMainGasTracker -> 10
				ChainText.etcMorden -> 11
				ChainText.btcMain -> 12
				else -> 13
			}
		}
	}
}