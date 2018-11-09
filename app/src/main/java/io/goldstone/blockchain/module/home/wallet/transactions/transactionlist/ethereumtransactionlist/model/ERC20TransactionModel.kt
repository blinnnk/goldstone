package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model

import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import java.io.Serializable

/**
 * @date 15/04/2018 1:13 AM
 * @author KaySaith
 */

data class ERC20TransactionModel(
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
	@SerializedName("from")
	var from: String,
	@SerializedName("contractAddress")
	val contract: String,
	@SerializedName("to")
	var to: String,
	@SerializedName("value")
	val value: String,
	@SerializedName("tokenName")
	var tokenName: String,
	@SerializedName("tokenSymbol")
	var tokenSymbol: String,
	@SerializedName("tokenDecimal")
	var tokenDecimal: String,
	@SerializedName("transactionIndex")
	val transactionIndex: String,
	@SerializedName("gas")
	val gas: String,
	@SerializedName("gasPrice")
	val gasPrice: String,
	@SerializedName("gasUsed")
	val gasUsed: String,
	@SerializedName("cumulativeGasUsed")
	val cumulativeGasUsed: String,
	@SerializedName("input")
	var input: String,
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
		"",
		""
	)
}