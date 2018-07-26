package io.goldstone.blockchain.kernel.network.bitcoin.value

/**
 * @date 2018/7/24 2:47 PM
 * @author KaySaith
 */
enum class BitcoinMethod(
	val method: String,
	val display: String = ""
) {
	
	EstimatesmartFee("estimatesmartfee", "EstimatesmartFee"),
	CreateRawTransaction("createrawtransaction", "CreateRawTransaction"),
	SendRawtTansaction("sendrawtransaction", "SendRawtTansaction"),
	Getblockcount("getblockcount", "getblockcount")
}