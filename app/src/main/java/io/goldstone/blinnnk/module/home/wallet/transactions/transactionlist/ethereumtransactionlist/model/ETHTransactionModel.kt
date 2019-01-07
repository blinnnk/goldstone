package io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/06
 */
data class ETHTransactionModel(
	@SerializedName("blockNumber")
	val blockNumber: String,
	@SerializedName("timeStamp")
	val timeStamp: String,
	@SerializedName("hash")
	val transactionHash: String,
	@SerializedName("nonce")
	val nonce: String,
	@SerializedName("blockHash")
	val blockHash: String,
	@SerializedName("transactionIndex")
	val transactionIndex: String,
	@SerializedName("from")
	var from: String,
	@SerializedName("to")
	var to: String,
	@SerializedName("value")
	val value: String,
	@SerializedName("gas")
	val gas: String,
	@SerializedName("gasPrice")
	val gasPrice: String,
	@SerializedName("isError")
	val isError: String,
	@SerializedName("txreceipt_status")
	val txReceiptStatus: String,
	@SerializedName("input")
	var input: String,
	@SerializedName("contractAddress")
	var contractAddress: String,
	@SerializedName("cumulativeGasUsed")
	val cumulativeGasUsed: String,
	@SerializedName("gasUsed")
	val gasUsed: String,
	@SerializedName("confirmations")
	var confirmations: String
): Serializable {
	constructor() : this(
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		""
	)
}