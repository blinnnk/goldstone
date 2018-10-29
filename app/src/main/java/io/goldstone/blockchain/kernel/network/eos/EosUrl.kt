package io.goldstone.blockchain.kernel.network.eos

import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.kernel.network.ChainURL

object EOSUrl {
	private val currentURL: () -> String = {
		if (SharedValue.isTestEnvironment()) ChainURL.eosJungleHistory else ChainURL.eosMain
	}
	private val currentHistoryURL: () -> String = {
		if (SharedValue.isTestEnvironment()) ChainURL.eosJungleHistory else ChainURL.eosMain
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

	val getBlock: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.GetBlock.method}"
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

	val getTransaction: () -> String = {
		"${currentHistoryURL()}/v1/history/${EOSMethod.GetTransaction.method}"
	}
	val getInfo: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.GetInfo.method}"
	}
	val pushTransaction: () -> String = {
		"${currentURL()}/v1/chain/${EOSMethod.PushTransaction.method}"
	}

	val getPairsFromNewDex: () -> String = {
		"https://api.newdex.io/v1/common/symbols"
	}

	val getTokenPriceInEOS: (pair: String) -> String = {
		"https://api.newdex.io/v1/ticker/price?symbol=$it"
	}
}