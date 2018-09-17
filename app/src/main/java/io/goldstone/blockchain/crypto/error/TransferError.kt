package io.goldstone.blockchain.crypto.error


/**
 * @author KaySaith
 * @date  2018/09/14
 */

enum class TransferError(val content: String) {
	BalanceIsNotEnough("this account doesn't have enough balance"),
	IncorrectDecimal("this input count's decimal is wrong with its own decimal value"),
	GetWrongFeeFromChain("there is error when get fee from chain"),
	GetChainInfoError("get chain info error, please check your net environment"),
	None("No errors")
}