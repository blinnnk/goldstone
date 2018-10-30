package io.goldstone.blockchain.kernel.network

import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue

/**
 * @date 2018/6/21 10:36 AM
 * @author KaySaith
 */
object ChainURL {

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
			currentChain.chainID.isRopsten() -> ethRopstenWeb
			currentChain.chainID.isKovan() -> ethKovanWeb
			currentChain.chainID.isRinkeby() -> ethRinkebyWeb
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
}