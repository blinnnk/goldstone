package io.goldstone.blockchain.kernel.network.eos

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.network.ChainURL

object EOSUrl {
	private val currentURL: () -> String = {
		if (Config.isTestEnvironment()) ChainURL.eosTest else ChainURL.eosMain
	}
	val getKeyAccount = "${currentURL()}/v1/history/${EOSMethod.GetKeyAccountName.method}"
	val getKeyAccountInTargetNet: (targetNet: String) -> String = {
		"$it/v1/history/${EOSMethod.GetKeyAccountName.method}"
	}
	val getAccountInfo = "${currentURL()}/v1/chain/${EOSMethod.GetAccount.method}"
	val getAccountInfoInTargetNet: (targetNet: String) -> String = {
		"$it/v1/chain/${EOSMethod.GetAccount.method}"
	}
	val getAccountEOSBalance = "${currentURL()}/v1/chain/${EOSMethod.GetCurrencyBalance.method}"
	val getTransactionHistory = "${currentURL()}/v1/history/${EOSMethod.GetTransactionHistory.method}"
	val getTransaction = "${currentURL()}/v1/history/${EOSMethod.GetTransaction.method}"
	val getInfo = "${currentURL()}/v1/chain/${EOSMethod.GetInfo.method}"
	val pushTransaction = "${currentURL()}/v1/chain/${EOSMethod.PushTransaction.method}"
}