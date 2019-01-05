package io.goldstone.blinnnk.kernel.network.bitcoin.value

/**
 * @date 2018/7/24 2:47 PM
 * @author KaySaith
 */
enum class BitcoinMethod(
	val method: String,
	val display: String = ""
) {
	EstimatesmartFee("estimatesmartfee", "EstimatesmartFee"),
	EstimateFee("estimatefee", "EstimateFee"),
	CreateRawTransaction("createrawtransaction", "CreateRawTransaction"),
	SendRawTansaction("sendrawtransaction", "SendRawtTansaction"),
	Getblockcount("getblockcount", "getblockcount"),
	GetRawTransaction("getrawtransaction", "GetRawTransaction")
}