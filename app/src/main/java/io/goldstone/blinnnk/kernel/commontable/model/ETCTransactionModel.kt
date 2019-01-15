package io.goldstone.blinnnk.kernel.commontable.model

import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

/**
 * @date 2018/6/25 6:34 PM
 * @author KaySaith
 */
data class ETCTransactionModel(
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
	
	// 次构造方法适用于测试网，因为测试网的 'timestamp' 是全小写
	constructor(jsonObject: JSONObject) : this(
		jsonObject.safeGet("hash"),
		jsonObject.safeGet("nonce"),
		jsonObject.safeGet("chainId"),
		jsonObject.safeGet("from"),
		jsonObject.safeGet("to"),
		jsonObject.safeGet("blockHash"),
		jsonObject.safeGet("timestamp"),
		jsonObject.safeGet("gas"),
		jsonObject.safeGet("gasPrice"),
		jsonObject.safeGet("value"),
		jsonObject.safeGet("blockNumber"),
		jsonObject.safeGet("input"),
		jsonObject.safeGet("transactionIndex"),
	false
	
	)
}