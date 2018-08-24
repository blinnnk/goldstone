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

	val getTransactions: (address: String, from: Int, to: Int) -> String = { address, from, to ->
		"${currentUrl()}/api/addrs/$address/txs?from=$from&to=$to"
	}

	val getUnspentInfo: (address: String) -> String = { address ->
		"${currentUrl()}/api/addr/$address/utxo"
	}
	val getTransactionByHash: (header: String, hash: String) -> String = { header, hash ->
		"$header/api/tx/$hash"
	}
}