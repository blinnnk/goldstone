package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.value.WebUrl
import java.io.Serializable

@Suppress("DUPLICATE_LABEL_IN_WHEN")
/**
 * @date 2018/5/25 8:14 PM
 * @author KaySaith
 */
class ChainID(val id: String) : Serializable {
	fun isETHMain(): Boolean = ethMain.equals(id, true)
	fun isRopsten(): Boolean = ropsten.equals(id, true)
	fun isRinkeby(): Boolean = rinkeby.equals(id, true)
	fun isKovan(): Boolean = kovan.equals(id, true)
	fun isETCMain(): Boolean = etcMain.equals(id, true)
	fun isETCTest(): Boolean = etcTest.equals(id, true)
	fun isBTCMain(): Boolean = btcMain.equals(id, true)
	fun isBTCTest(): Boolean = btcTest.equals(id, true)
	fun isBCHMain(): Boolean = bchMain.equals(id, true)
	fun isBCHTest(): Boolean = bchTest.equals(id, true)
	fun isLTCMain(): Boolean = ltcMain.equals(id, true)
	fun isLTCTest(): Boolean = ltcTest.equals(id, true)
	fun isEOSMain(): Boolean = eosMain.equals(id, true)
	fun isEOSTest(): Boolean = eosTest.equals(id, true)

	fun isCurrent(): Boolean {
		return when (id) {
			btcMain, btcTest -> id.equals(SharedChain.getBTCCurrent().id, true)
			ltcMain, ltcTest -> id.equals(SharedChain.getLTCCurrent().id, true)
			bchMain, bchTest -> id.equals(SharedChain.getBCHCurrent().id, true)
			ethMain, ropsten, rinkeby, kovan -> id.equals(SharedChain.getCurrentETH().id, true)
			etcMain, etcTest -> id.equals(SharedChain.getETCCurrent().id, true)
			eosMain, eosTest -> id.equals(SharedChain.getEOSCurrent().id, true)
			else -> false
		}
	}

	fun getContract(): String? {
		return when (id) {
			etcMain, etcTest -> TokenContract.etcContract
			btcTest, btcMain -> TokenContract.btcContract
			ltcMain, ltcTest -> TokenContract.ltcContract
			bchMain, bchTest -> TokenContract.bchContract
			eosMain, eosTest -> TokenContract.eosContract
			else -> null
		}
	}

	fun getCurrentAddress(): String {
		return when (id) {
			etcMain, etcTest -> SharedAddress.getCurrentETC()
			btcTest, ltcTest, bchTest -> SharedAddress.getCurrentBTCSeriesTest()
			btcMain -> SharedAddress.getCurrentBTC()
			ltcMain -> SharedAddress.getCurrentLTC()
			bchMain -> SharedAddress.getCurrentBCH()
			eosMain, eosTest -> SharedAddress.getCurrentEOS()
			else -> SharedAddress.getCurrentETC()
		}
	}

	fun getChainName(): String {
		return when (id) {
			// Third Party Nodes
			kovan -> ChainText.infuraKovan
			ropsten -> ChainText.infuraRopsten
			ethMain -> ChainText.infuraMain
			rinkeby -> ChainText.infuraRinkeby
			etcTest -> ChainText.etcMorden
			etcMain -> ChainText.etcMainGasTracker
			btcMain -> ChainText.btcMain
			ltcMain -> ChainText.ltcMain
			bchMain -> ChainText.bchMain
			eosMain -> ChainText.eosMain
			eosTest -> ChainText.eosTest
			// GoldStone Nodes
			ethMain -> ChainText.goldStoneMain
			ropsten -> ChainText.ropsten
			kovan -> ChainText.kovan
			rinkeby -> ChainText.rinkeby
			etcMain -> ChainText.goldStoneEtcMain
			etcTest -> ChainText.goldStoneEtcMordenTest
			btcTest -> ChainText.btcTest
			ltcTest -> ChainText.ltcTest
			bchTest -> ChainText.bchTest
			else -> ChainText.goldStoneMain
		}
	}

	fun getThirdPartyURL(): String {
		return when (id) {
			btcMain -> WebUrl.btcMain
			btcTest -> WebUrl.btcTest
			ltcMain -> WebUrl.ltcMain
			ltcTest -> WebUrl.ltcTest
			bchMain -> WebUrl.bchMain
			bchTest -> WebUrl.bchTest
			else -> ""
		}
	}

	companion object {
		const val ethMain = "1"
		const val ropsten = "3"
		const val rinkeby = "4"
		const val kovan = "42"
		const val etcMain = "61"
		const val etcTest = "62"
		const val btcMain = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"
		const val btcTest = "000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"
		const val bchMain = "00000000000000000019f112ec0a9982926f1258cdcc558dd7c3b7e5dc7fa148"
		const val bchTest = "00000000000e38fef93ed9582a7df43815d5c2ba9fd37ef70c9a0ea4a285b8f5"
		const val ltcMain = "12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2"
		const val ltcTest = "4966625a4b2851d9fdee139e56211a0d88575f59ed816ff5e6a63deb4e3e29a0"
		const val eosMain = "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906"
		const val eosTest = "038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca" // Jungle Testnet

		val ETH = ChainID(ethMain)
		val Ropsten = ChainID(ropsten)
		val Rinkeby = ChainID(rinkeby)
		val Kovan = ChainID(kovan)
		val ETC = ChainID(etcMain)
		val ETCTest = ChainID(etcTest)
		val BTC = ChainID(btcMain)
		val BTCTest = ChainID(btcTest)
		val BCH = ChainID(bchMain)
		val BCHTest = ChainID(bchTest)
		val LTC = ChainID(ltcMain)
		val LTCTest = ChainID(ltcTest)
		val EOS = ChainID(eosMain)
		val EOSTest = ChainID(eosTest)

		fun getChainIDByName(chainName: String): String {
			return when (chainName) {
				// GoldStone ERC Node
				ChainText.goldStoneMain -> ethMain
				ChainText.ropsten -> ropsten
				ChainText.kovan -> kovan
				ChainText.rinkeby -> rinkeby
				// Infura ERC Node
				ChainText.infuraMain -> ethMain
				ChainText.infuraRopsten -> ropsten
				ChainText.infuraKovan -> kovan
				ChainText.infuraRinkeby -> rinkeby
				// ETC Node
				ChainText.etcMorden -> etcTest
				ChainText.goldStoneEtcMain -> etcMain
				ChainText.goldStoneEtcMordenTest -> etcTest
				ChainText.etcMainGasTracker -> etcMain
				// BTC Node
				ChainText.btcMain -> btcMain
				ChainText.btcTest -> btcTest
				// LTC Node
				ChainText.ltcMain -> ltcMain
				ChainText.ltcTest -> ltcTest
				// BCH Node
				ChainText.bchMain -> bchMain
				ChainText.bchTest -> bchTest
				// EOS Node
				ChainText.eosMain -> eosMain
				ChainText.eosTest -> eosTest
				else -> ethMain
			}
		}

		fun getTestChains(): List<String> {
			return listOf(
				etcTest,
				btcTest,
				ropsten,
				kovan,
				rinkeby,
				ltcTest,
				bchTest,
				eosTest
			)
		}

		fun getAllChainID(): List<String> {
			return listOf(
				ethMain,
				ropsten,
				kovan,
				rinkeby,
				etcTest,
				etcMain,
				btcMain,
				btcTest,
				ltcMain,
				ltcTest,
				bchMain,
				bchTest,
				eosMain,
				eosTest
			)
		}

		fun getAllETCChainID(): List<String> {
			return listOf(
				etcTest,
				etcMain
			)
		}

		fun getAllEOSChainID(): List<String> {
			return listOf(
				eosTest,
				eosMain
			)
		}

		fun getAllEthereumChainID(): List<String> {
			return listOf(
				ethMain,
				ropsten,
				kovan,
				rinkeby
			)
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
	GoldStoneBCHTest(17),
	GoldStoneEOSMain(18),
	GoldStoneEOSTest(19);

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
				18 -> ChainText.eosMain
				19 -> ChainText.eosTest
				else -> ChainText.eosTest
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
				ChainText.eosMain -> 18
				ChainText.eosTest -> 19
				else -> 19
			}
		}
	}
}