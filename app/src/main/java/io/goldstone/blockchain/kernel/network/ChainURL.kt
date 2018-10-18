package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.getRandom
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
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
			ChainText.rinkeby -> ChainURL.rinkeby
			ChainText.kovan -> ChainURL.kovan
			ChainText.infuraRopsten -> ChainURL.infuraRopsten
			ChainText.infuraKovan -> ChainURL.infuraKovan
			ChainText.infuraRinkeby -> ChainURL.infuraRinkeby
			ChainText.infuraMain -> ChainURL.infuraMain
			ChainText.btcTest -> ChainURL.btcTest
			ChainText.btcMain -> ChainURL.btcMain
			ChainText.ltcMain -> ChainURL.ltcMain
			ChainText.ltcTest -> ChainURL.ltcTest
			ChainText.bchMain -> ChainURL.bchMain
			ChainText.bchTest -> ChainURL.bchTest
			ChainText.eosMain -> ChainURL.eosMain
			ChainText.eosTest -> ChainURL.eosTest
			else -> ChainURL.main
		}
	}
	val currentETCChain: (currentChainName: String) -> String = {
		when (it) {
			ChainText.etcMorden -> ChainURL.etcMordenTest
			ChainText.etcMainGasTracker -> ChainURL.etcMain
			ChainText.goldStoneEtcMain -> ChainURL.etcGoldStoneMain
			ChainText.goldStoneEtcMordenTest -> ChainURL.etcGoldStoneMordenTest
			else -> ChainURL.etcMain
		}
	}

	val unencryptedChainName = listOf(
		ChainText.etcMorden,
		ChainText.etcMainGasTracker,
		ChainText.infuraKovan,
		ChainText.infuraMain,
		ChainText.infuraRinkeby,
		ChainText.infuraRopsten,
		ChainText.eosMain,
		ChainText.eosTest
	)
	val etcChainName =
		listOf(
			ChainText.etcMorden,
			ChainText.etcMainGasTracker,
			ChainText.goldStoneEtcMain,
			ChainText.goldStoneEtcMordenTest
		)

	val eosChainName = listOf(
		ChainText.eosMain,
		ChainText.eosTest
	)

	fun getContractByTransaction(transaction: TransactionTable, chainName: String): TokenContract {
		return when {
			transaction.isERC20Token -> TokenContract(transaction.to)
			ChainURL.etcChainName.any {
				it.equals(chainName, true)
			} -> TokenContract.ETH
			else -> TokenContract.ETH
		}
	}

	private val infuraKey: () -> String = {
		infuraKeys.getRandom()
	}
	/** Chain Address */
	const val main = "https://eth-node-mainnet.goldstone.io/eth"
	private const val ropsten = "https://eth-node-ropsten.goldstone.io/eth"
	private const val kovan = "https://eth-node-kovan.goldstone.io/eth"
	private const val rinkeby = "https://eth-node-rinkeby.goldstone.io/eth"
	/** BTC Chain Address */
	private const val btcMain = "https://btc-node-mainnet.goldstone.io/btc"
	private const val btcTest = "https://btc-node-testnet.goldstone.io/btc"
	/** LTC Chain Address */
	private const val ltcMain = "https://btc-node-mainnet.goldstone.io/ltc"
	private const val ltcTest = "https://btc-node-testnet.goldstone.io/ltc"
	/** BCH Chain Address */
	private const val bchMain = "https://btc-node-mainnet.goldstone.io/bch"
	private const val bchTest = "https://btc-node-testnet.goldstone.io/bch"
	/** EOS Chain Address */
	const val eosMain = "https://api1.eosasia.one"
	const val eosTest = "https://api.jungle.alohaeos.com"
	const val eosJungleHistory = "https://junglehistory.cryptolions.io"
	/** ETC Chain Address */
	private const val etcMain = "https://web3.gastracker.io"
	private const val etcMordenTest = "https://web3.gastracker.io/morden"
	private const val etcGoldStoneMain = "https://etc-node-mainnet.goldstone.io/eth"
	private const val etcGoldStoneMordenTest = "https://etc-node-testnet.goldstone.io/eth"
	/** Infura Chain Address */
	private val infuraMain = "https://mainnet.infura.io/${infuraKey()}"
	private val infuraRopsten = "https://ropsten.infura.io/${infuraKey()}"
	private val infuraKovan = "https://kovan.infura.io/${infuraKey()}"
	private val infuraRinkeby = "https://rinkeby.infura.io/${infuraKey()}"

	/** Transaction Third-Party Html View */
	// BCH
	private const val bchMainnetWeb = "https://www.blocktrail.com/BCC"
	private const val bchTestnetWeb = "https://www.blocktrail.com/tBCC"
	// BTC
	private const val btcMainnetWeb = "https://www.blocktrail.com/BTC"
	private const val btcTestnetWeb = "https://www.blocktrail.com/tBTC"
	// LTC
	private const val ltcMainnetWeb = "https://live.blockcypher.com/ltc"
	@JvmStatic
	private val ltcTestnetWeb: (method: String) -> String = {
		"https://chain.so/$it/LTCTEST/"
	}
	// ETC
	private const val etcMainnetWeb = "https://gastracker.io"
	private const val etcTestnetWeb = "http://mordenexplorer.ethernode.io"
	// ETH
	private const val ethMainnetWeb = "https://etherscan.io"
	private const val ethRopstenWeb = "https://ropsten.etherscan.io"
	private const val ethKovanWeb = "https://kovan.etherscan.io"
	private const val ethRinkebyWeb = "https://rinkeby.etherscan.io"
	// EOS
	private const val eosMainnetWeb = "https://bloks.io"
	private const val eosJungleWeb = "https://jungle.bloks.io"

	/** Address Detail URL*/
	val btcAddressDetail: (address: String) -> String = {
		val header = if (SharedValue.isTestEnvironment()) btcTestnetWeb
		else btcMainnetWeb
		"$header/address/$it"
	}
	val ethAddressDetail: (address: String) -> String = {
		val currentChain = SharedChain.getCurrentETH()
		val header = when {
			currentChain.isRopsten() -> ethRopstenWeb
			currentChain.isKovan() -> ethKovanWeb
			currentChain.isRinkeby() -> ethRinkebyWeb
			else -> ethMainnetWeb
		}
		"$header/address/$it"
	}
	val bchAddressDetail: (address: String) -> String = {
		val header = if (SharedValue.isTestEnvironment()) bchTestnetWeb
		else bchMainnetWeb
		"$header/address/$it"
	}

	val etcAddressDetail: (address: String) -> String = {
		val header = if (SharedValue.isTestEnvironment()) etcTestnetWeb
		else etcMainnetWeb
		"$header/addr/$it"
	}

	val ltcAddressDetail: (address: String) -> String = {
		if (SharedValue.isTestEnvironment()) ltcTestnetWeb("address") + it
		else "$ltcMainnetWeb/address/$it"
	}

	val eosAddressDetail: (address: String) -> String = {
		val header = if (SharedValue.isTestEnvironment()) eosJungleWeb
		else eosMainnetWeb
		"$header/account/$it"
	}

	/** Transaction Detail URL */
	val eosTransactionDetail: (txID: String) -> String = {
		val header = if (SharedValue.isTestEnvironment()) eosJungleWeb
		else eosMainnetWeb
		"$header/transaction/$it"
	}
	val etcWebHeader: () -> String = {
		val header = if (SharedValue.isTestEnvironment()) etcTestnetWeb
		else etcMainnetWeb
		"$header/tx/"
	}

	val bchWebHeader: () -> String = {
		val header = if (SharedValue.isTestEnvironment()) bchTestnetWeb
		else bchMainnetWeb
		"$header/tx/"
	}

	val ltcWebHeader: () -> String = {
		if (SharedValue.isTestEnvironment()) ltcTestnetWeb("tx")
		else "$ltcMainnetWeb/tx/"
	}

	val btcWebHeader: () -> String = {
		val header = if (SharedValue.isTestEnvironment()) btcTestnetWeb
		else btcMainnetWeb
		"$header/tx/"
	}

	@JvmStatic
	fun getCurrentEncryptStatusByNodeName(name: String): Boolean {
		return !ChainURL.unencryptedChainName.any { it.equals(name, true) }
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