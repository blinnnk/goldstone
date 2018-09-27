package io.goldstone.blockchain.kernel.network.bitcoin

import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 2018/7/19 1:50 AM
 * @author KaySaith
 */
object BitcoinUrl {

	var currentUrl: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.btcTest
		else WebUrl.btcMain
	}

	val backUpUrl: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.backupBtcTest
		else WebUrl.backUpBtcMain
	}

	val getBalanceFromBlockInfo: (address: String) -> String = {
		"${backUpUrl()}/balance?active=$it"
	}

	val getUnspentInfoFromBlockInfo: (address: String) -> String = {
		"${backUpUrl()}/unspent?active=$it"
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