package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
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
	fun isBCH(): Boolean = isBCHTest() || isBCHMain()
	fun isLTCMain(): Boolean = ltcMain.equals(id, true)
	fun isLTCTest(): Boolean = ltcTest.equals(id, true)
	fun isEOSMain(): Boolean = eosMain.equals(id, true)
	fun isEOSTest(): Boolean = eosTest.equals(id, true)
	fun isEOS(): Boolean = isEOSMain() || isEOSTest()


	fun isEOSSeries(): Boolean {
		return isEOS() || isEOSTest()
	}

	fun isETHSeries(): Boolean {
		return isETHMain() || isRopsten() || isRinkeby() || isKovan()
	}

	fun isETCSeries(): Boolean {
		return isETCTest() || isETCMain()
	}

	fun isBTCSeries(): Boolean {
		return isBTCMain() || isBTCTest() || isBCHTest() || isBCHMain() || isLTCTest() || isLTCMain()
	}

	fun isTestnet(): Boolean {
		return isRinkeby() || isRopsten() || isKovan() || isBTCTest() || isBCHTest() || isLTCTest() || isEOSTest() || isETCTest()
	}

	@Throws
	fun getContract(): TokenContract {
		return when (id) {
			etcMain, etcTest -> TokenContract.ETC
			btcTest, btcMain -> TokenContract.BTC
			ltcMain, ltcTest -> TokenContract.LTC
			bchMain, bchTest -> TokenContract.BCH
			eosMain, eosTest -> TokenContract.EOS
			ethMain, ropsten, rinkeby, kovan -> TokenContract.ETH
			else -> throw Throwable("Wrong Chain Token Contract")
		}
	}

	@Throws
	fun getChainType(): ChainType {
		return when (id) {
			etcMain, etcTest -> ChainType.ETC
			btcTest, btcMain -> ChainType.BTC
			ltcMain, ltcTest -> ChainType.LTC
			bchMain, bchTest -> ChainType.BCH
			eosMain, eosTest -> ChainType.EOS
			ethMain, ropsten, rinkeby, kovan -> ChainType.ETH
			else -> throw Throwable("Wrong Chain Type")
		}
	}

	@Throws
	fun getChainURL(): ChainURL {
		return when (id) {
			etcMain, etcTest -> SharedChain.getETCCurrent()
			btcTest, btcMain -> SharedChain.getBTCCurrent()
			ltcMain, ltcTest -> SharedChain.getLTCCurrent()
			bchMain, bchTest -> SharedChain.getBCHCurrent()
			eosMain, eosTest -> SharedChain.getEOSCurrent()
			ethMain, ropsten, rinkeby, kovan -> SharedChain.getCurrentETH()
			else -> throw Throwable("Wrong Chain URL")
		}
	}

	fun getSpecificChain(): ChainURL? {
		return if (isTestnet()) SharedChain.getAllUsedTestnetChains().find {
			it.chainID.id.equals(id, true)
		} else SharedChain.getAllUsedMainnetChains().find {
			it.chainID.id.equals(id, true)
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
	}
}