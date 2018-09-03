package io.goldstone.blockchain.kernel.network.eos

enum class EOSMethod(val method: String, val disPlay: String) {
	GetKeyAccountName("get_key_accounts", "GetKeyAccountName"),
	GetCurrencyBalance("get_currency_balance ", "GetCurrencyBalance"),
	GetTransactionHistory("get_actions ", "GetTransactionHistory")
}