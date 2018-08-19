package io.goldstone.blockchain.kernel.network.litecoin

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 2018/8/13 12:08 PM
 * @author KaySaith
 */

object LitecoinUrl {
	var currentUrl: () -> String = {
		if (Config.isTestEnvironment()) WebUrl.ltcTest else WebUrl.ltcMain
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

	val getTransactionByHash: (hash: String) -> String = { hash ->
		"${currentUrl()}/api/tx/$hash"
	}
}