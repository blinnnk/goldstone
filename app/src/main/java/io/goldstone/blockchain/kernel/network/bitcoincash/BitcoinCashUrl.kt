package io.goldstone.blockchain.kernel.network.bitcoincash

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 2018/8/15 1:36 PM
 * @author KaySaith
 */

object BitcoinCashUrl {
	var currentUrl: () -> String = {
		if (Config.isTestEnvironment()) WebUrl.bchTest else WebUrl.bchMain
	}
	val getBalance: (address: String) -> String = { address ->
		"${currentUrl()}/api/addr/$address/balance"
	}

	val getUnspentInfo: (address: String) -> String = { address ->
		"${currentUrl()}/api/addr/$address/utxo"
	}

	val getTransactions: (address: String) -> String = { address ->
		"${currentUrl()}/api/txs/?address=$address"
	}

	val getTransactionByHash: (header: String, hash: String) -> String = { header, hash ->
		"$header/api/tx/$hash"
	}
}