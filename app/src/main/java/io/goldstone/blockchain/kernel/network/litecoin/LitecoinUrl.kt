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

	val getBalanceFromChainSo: (address: String) -> String = { address ->
		val param = if (Config.isTestEnvironment()) "LTCTest" else "LTC"
		"https://chain.so/api/v2/get_address_balance/$param/$address"
	}

	val getUnspentInfo: (address: String) -> String = { address ->
		"${currentUrl()}/api/addr/$address/utxo"
	}

	val getTransactions: (address: String, from: Int, to: Int) -> String = { address, from, to ->
		"${currentUrl()}/api/addrs/$address/txs?from=$from&to=$to"
	}

	val getTransactionByHash: (header: String, hash: String) -> String = { header, hash ->
		"$header/api/tx/$hash"
	}
}