package io.goldstone.blockchain.kernel.network.litecoin

import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 2018/8/13 12:08 PM
 * @author KaySaith
 */

object LitecoinUrl {
	var currentUrl: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.ltcTestGoldStone else WebUrl.ltcGoldStone
	}
	val getBalance: (address: String) -> String = { address ->
		"${currentUrl()}/addr/$address/balance"
	}

	val getBalanceFromChainSo: (address: String) -> String = { address ->
		val param = if (SharedValue.isTestEnvironment()) "LTCTest" else "LTC"
		"https://chain.so/api/v2/get_address_balance/$param/$address"
	}

	val getUnspentInfo: (address: String) -> String = { address ->
		"${currentUrl()}/addr/$address/utxo"
	}

	val getTransactions: (address: String, from: Int, to: Int) -> String = { address, from, to ->
		"${currentUrl()}/addrs/$address/txs?from=$from&to=$to"
	}

	val getTransactionByHash: (header: String, hash: String) -> String = { header, hash ->
		"$header/tx/$hash"
	}
}