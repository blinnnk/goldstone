package io.goldstone.blockchain.kernel.network.eos

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.network.ChainURL

object EosUrl {
	private val currentURL: () -> String = {
		if (Config.isTestEnvironment()) ChainURL.eosTest else ChainURL.eosMain
	}
	val getKeyAccount = "${currentURL()}/v1/history/${EOSMethod.GetKeyAccountName.method}"
	val getAccountEOSBalance = "${currentURL()}/v1/chain/${EOSMethod.GetCurrencyBalance.method}"
	val getTransactionHistory = "${currentURL()}/v1/history/${EOSMethod.GetTransactionHistory.method}"
	val getInfo = "${currentURL()}/v1/chain/${EOSMethod.GetInfo.method}"
}