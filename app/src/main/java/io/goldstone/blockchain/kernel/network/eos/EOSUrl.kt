package io.goldstone.blockchain.kernel.network.eos

import io.goldstone.blockchain.common.sharedpreference.SharedChain

object EOSUrl {

	val getKeyAccount: () -> String = {
		"${SharedChain.getEOSCurrent().getURL()}/v1/history/${EOSMethod.GetKeyAccountName.method}"
	}
	val getKeyAccountInTargetNet: (targetNet: String) -> String = {
		"$it/v1/history/${EOSMethod.GetKeyAccountName.method}"
	}
	val getAccountInfo: () -> String = {
		"${SharedChain.getEOSCurrent().getURL()}/v1/chain/${EOSMethod.GetAccount.method}"
	}

	val getBlock: () -> String = {
		"${SharedChain.getEOSCurrent().getURL()}/v1/chain/${EOSMethod.GetBlock.method}"
	}
	val getAccountInfoInTargetNet: (targetNet: String) -> String = {
		"$it/v1/chain/${EOSMethod.GetAccount.method}"
	}
	val getAccountEOSBalance: () -> String = {
		"${SharedChain.getEOSCurrent().getURL()}/v1/chain/${EOSMethod.GetCurrencyBalance.method}"
	}
	val getTableRows: () -> String = {
		"${SharedChain.getEOSCurrent().getURL()}/v1/chain/${EOSMethod.GetTableRows.method}"
	}

	val getTransaction: (header: String) -> String = {
		"$it/v1/history/${EOSMethod.GetTransaction.method}"
	}
	val getInfo: () -> String = {
		"${SharedChain.getEOSCurrent().getURL()}/v1/chain/${EOSMethod.GetInfo.method}"
	}

	fun pushTransaction(header: String): String {
		return "$header/v1/chain/${EOSMethod.PushTransaction.method}"
	}

	val getPairsFromNewDex: () -> String = {
		"https://api.newdex.io/v1/common/symbols"
	}

	val getTokenPriceInEOS: (pair: String) -> String = {
		"https://api.newdex.io/v1/ticker/price?symbol=$it"
	}
}