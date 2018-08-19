package io.goldstone.blockchain.kernel.network.bitcoin

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 2018/7/19 1:50 AM
 * @author KaySaith
 */
object BitcoinUrl {

	var currentUrl: () -> String = {
		if (Config.isTestEnvironment()) WebUrl.btcTest
		else WebUrl.btcMain
	}

	val getBalance: (address: String) -> String = { address ->
		"${currentUrl()}/api/addr/$address/balance"
	}

	val getTransactionList: (address: String) -> String = {
		"${currentUrl()}/api/txs/?address=$it"
	}

	val getUnspentInfo: (address: String) -> String = { address ->
		"${currentUrl()}/api/addr/$address/utxo"
	}
	val getTransactionByHash: (hash: String) -> String = { hash ->
		"${currentUrl()}/api/tx/$hash"
	}
}