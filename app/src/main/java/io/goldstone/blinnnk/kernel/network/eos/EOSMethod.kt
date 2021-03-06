package io.goldstone.blinnnk.kernel.network.eos

enum class EOSMethod(val method: String, val disPlay: String) {
	GetKeyAccountName("get_key_accounts", "GetKeyAccountName"),
	GetCurrencyBalance("get_currency_balance ", "GetCurrencyBalance"),
	GetTransactionHistory("get_actions ", "GetTransactionHistory"),
	GetInfo("get_info ", "GetInfo"),
	PushTransaction("push_transaction", "PushTransaction"),
	GetAccount("get_account", "GetAccount"),
	GetTransaction("get_transaction", "GetTransaction"),
	GetTableRows("get_table_rows", "GetTableRows"),
	GetBlock("get_block", "GetBlock"),
}