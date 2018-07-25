package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.getRandom
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable

/**
 * @date 2018/6/21 10:36 AM
 * @author KaySaith
 */
object ChainURL {
	
	val currentChain: (currentChainName: String) -> String = {
		when (it) {
			ChainText.goldStoneMain -> ChainURL.main
			ChainText.ropsten -> ChainURL.ropsten
			ChainText.rinkeby -> ChainURL.rinkeyb
			ChainText.kovan -> ChainURL.kovan
			ChainText.infuraRopsten -> ChainURL.infuraRopsten
			ChainText.infuraKovan -> ChainURL.infuraKovan
			ChainText.infuraRinkeby -> ChainURL.infuraRinkeby
			ChainText.infuraMain -> ChainURL.infuraMain
			ChainText.btcTest -> ChainURL.btcTest
			ChainText.btcMain -> ChainURL.btcMain
			else -> ChainURL.main
		}
	}
	val currentETCChain: (currentChainName: String) -> String = {
		when (it) {
			ChainText.etcMorden -> ChainURL.etcMorderTest
			ChainText.etcMainGasTracker -> ChainURL.etcMain
			ChainText.goldStoneEtcMain -> ChainURL.etcGoldStoneMain
			ChainText.goldStoneEtcMorderTest -> ChainURL.etcGoldStoneMorderTest
			else -> ChainURL.etcMain
		}
	}
	val currentBTCChain: (currentChainName: String) -> String = {
		when (it) {
			ChainText.btcMain -> ChainURL.btcMain
			ChainText.btcTest -> ChainURL.btcTest
			else -> ChainURL.btcMain
		}
	}
	val uncryptChainName = listOf(
		ChainText.etcMorden,
		ChainText.etcMainGasTracker,
		ChainText.infuraKovan,
		ChainText.infuraMain,
		ChainText.infuraRinkeby,
		ChainText.infuraRopsten
	)
	val etcChainName =
		arrayListOf(
			ChainText.etcMorden,
			ChainText.etcMainGasTracker,
			ChainText.goldStoneEtcMain,
			ChainText.goldStoneEtcMorderTest
		)
	
	fun getChainNameByChainType(type: ChainType): String {
		return when (type) {
			ChainType.ETH -> Config.getCurrentChainName()
			ChainType.ETC -> Config.getETCCurrentChainName()
			ChainType.BTC -> Config.getBTCCurrentChain()
			else -> Config.getCurrentChainName()
		}
	}
	
	fun getChainNameBySymbol(symbol: String): String {
		return when {
			symbol.equals(CryptoSymbol.eth, true) -> Config.getCurrentChainName()
			symbol.equals(CryptoSymbol.etc, true) -> Config.getETCCurrentChainName()
			symbol.equals(CryptoSymbol.btc, true) -> Config.getBTCCurrentChainName()
			else -> Config.getCurrentChainName()
		}
	}
	
	fun getChainTypeBySymbol(symbol: String): ChainType {
		return when {
			symbol.equals(CryptoSymbol.eth, true) -> ChainType.ETH
			symbol.equals(CryptoSymbol.etc, true) -> ChainType.ETC
			symbol.equals(CryptoSymbol.btc, true) -> ChainType.BTC
			else -> ChainType.ETH
		}
	}
	
	fun getContractByTransaction(transaction: TransactionTable, chainName: String): String {
		return when {
			transaction.isERC20Token -> transaction.to
			ChainURL.etcChainName.any {
				it.equals(chainName, true)
			} -> CryptoValue.etcContract
			else -> CryptoValue.ethContract
		}
	}
	
	private val infuraKey: () -> String = {
		infuraKeys.getRandom()
	}
	/** Chain Address */
	const val main = "https://eth-node-mainnet.goldstone.io/eth"
	private const val ropsten = "https://eth-node-ropsten.goldstone.io/eth"
	private const val kovan = "https://eth-node-kovan.goldstone.io/eth"
	private const val rinkeyb = "https://eth-node-rinkeby.goldstone.io/eth"
	/** BTC Chain Address */
	private const val btcMain = "https://btc-node-mainnet.goldstone.io/btc"
	private const val btcTest = "https://btc-node-testnet.goldstone.io/btc"
	/** ETC Chain Address */
	private const val etcMain = "https://web3.gastracker.io"
	private const val etcMorderTest = "https://web3.gastracker.io/morden"
	private const val etcGoldStoneMain = "https://etc-node-mainnet.goldstone.io/eth"
	private const val etcGoldStoneMorderTest = "https://etc-node-testnet.goldstone.io/eth"
	/** Infura Chain Address */
	private val infuraMain = "https://mainnet.infura.io/${infuraKey()}"
	private val infuraRopsten = "https://ropsten.infura.io/${infuraKey()}"
	private val infuraKovan = "https://kovan.infura.io/${infuraKey()}"
	private val infuraRinkeby = "https://rinkeby.infura.io/${infuraKey()}"
	
	@JvmStatic
	fun getCurrentEncryptStatusByNodeName(name: String): Boolean {
		return !ChainURL.uncryptChainName.any { it.equals(name, true) }
	}
}

private val infuraKeys = arrayListOf(
	"CErKewMAewA4Lc6NmPxl",
	"jPzvxyWFhTq5wzuZiFQd",
	"L2QgZ6FJ2Grm5lQ8mct1",
	"dA4DvqC5RUVUbSE8MSpo",
	"1ILnsnbTcrFEYGWAAPL9",
	"0OepmeWlw4CIBvWZWuWY",
	"r2hQJMusknsehrH8xfMX",
	"rHRa4h4h8O3l8JxIPpcR",
	"8N0xGPz1mRIinHIumypH",
	"ZKlxAfozoDUkgPaEL6zy",
	"667gxtvC0PTg7V5Ni5VS",
	"ViGT9cS0B2kzx6xuK3Wu",
	"u89X6c7eWO5A4wJSE5zF",
	"pg1AO8ayuSUmsesvkdef",
	"tcamhJM7IsY4yChCI6qa",
	"L7sZaYr6J0Tr46kCqoYE",
	"hdBekqOmgjIZdX9kLTep",
	"fUgIFuOFJlihAOCqRSek",
	"hAjkHD4gOalzH1AkskYU",
	"1i9WePTNpqq1ggGOYYTs"
)