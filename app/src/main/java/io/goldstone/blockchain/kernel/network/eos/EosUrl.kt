package io.goldstone.blockchain.kernel.network.eos

import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.kernel.network.ChainURL

object EOSUrl {
	private var currentEOSTestUrl = ChainURL.eosTest // 网络出问题后会在 `Error` 中更改这个值
	private val currentURL: () -> String = {
		if (SharedValue.isTestEnvironment()) currentEOSTestUrl else ChainURL.eosMain
	}
	val getKeyAccount: () -> String = {
		"${currentURL()}/v1/history/${EOSMethod.GetKeyAccountName.method}"
	}
	val getKeyAccountInTargetNet: (targetNet: String) -> String = {
		"$it/v1/history/${EOSMethod.GetKeyAccountName.method}"
	}
	val getAccountInfo: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.GetAccount.method}"
	}
	val getAccountInfoInTargetNet: (targetNet: String) -> String = {
		"$it/v1/chain/${EOSMethod.GetAccount.method}"
	}
	val getAccountEOSBalance: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.GetCurrencyBalance.method}"
	}
	val getTableRows: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.GetTableRows.method}"
	}
	val getTransactionHistory: () -> String = {
		"${currentURL()}/v1/history/${EOSMethod.GetTransactionHistory.method}"
	}
	val getTransaction: () -> String = {
		"${currentURL()}/v1/history/${EOSMethod.GetTransaction.method}"
	}
	val getInfo: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.GetInfo.method}"
	}
	val pushTransaction: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.PushTransaction.method}"
	}
}