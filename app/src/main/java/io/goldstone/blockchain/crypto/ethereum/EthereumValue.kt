package io.goldstone.blockchain.crypto.ethereum

/**
 * @date 2018/6/17 6:17 PM
 * @author KaySaith
 */
enum class EthereumMethod(
	val method: String,
	val code: String = "",
	val display: String = ""
) {

	EthCall("eth_call", SolidityCode.ethCall, "EthCall"),
	GetSymbol("eth_call", SolidityCode.ethCall, "GetSymbol"),
	GetTokenBalance("eth_call", SolidityCode.getTokenBalance, "GetTokenBalance"),
	GetBalance("eth_getBalance", "", "GetBalance"),
	GetTotalSupply("eth_call", SolidityCode.getTotalSupply, "GetTotalSupply"),
	GetTokenDecimal("eth_call", SolidityCode.getDecimal, "GetTokenDecimal"),
	GetTokenName("eth_call", SolidityCode.getTokenName, "GetTokenName"),
	SendRawTransaction("eth_sendRawTransaction", SolidityCode.getTokenName, "SendRawTransaction"),
	GetTransactionByHash("eth_getTransactionByHash", SolidityCode.ethCall, "GetTransactionByHash"),
	GetTransactionReceiptByHash("eth_getTransactionReceipt", SolidityCode.ethCall, "GetTransactionReceiptByHash"),
	GetEstimateGas("eth_estimateGas", SolidityCode.ethCall, "GetEstimateGas"),
	GetBlockByHash("eth_getBlockByHash", SolidityCode.ethCall, "GetBlockByHash"),
	GetBlockNumber("eth_blockNumber", SolidityCode.ethCall, "GetBlockNumber"),
	GetTransactionCount("eth_getTransactionCount", SolidityCode.ethCall, "GetTransactionCount"),
}