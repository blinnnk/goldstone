package io.goldstone.blinnnk.kernel.commontable.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2018/6/25 6:34 PM
 * @author KaySaith
 * 这个model适用于测试网，因为测试网的 'timeStamp'  s 是大写
 */
data class ETCMainNetTransactionModel(
	@SerializedName("hash")
	var hash: String,
	@SerializedName("nonce")
	var nonce: String,
	@SerializedName("chainId")
	var chainId: String,
	@SerializedName("from")
	var from: String,
	@SerializedName("to")
	var to: String,
	@SerializedName("blockHash")
	var blockHash: String,
	@SerializedName("timeStamp")
	var timeStamp: String,
	@SerializedName("gas")
	var gas: String,
	@SerializedName("gasPrice")
	var gasPrice: String,
	@SerializedName("value")
	var value: String,
	@SerializedName("blockNumber")
	var blockNumber: String,
	@SerializedName("input")
	var input: String,
	@SerializedName("transactionIndex")
	var transactionIndex: String,
	var isFee: Boolean = false
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
		false
	)
}