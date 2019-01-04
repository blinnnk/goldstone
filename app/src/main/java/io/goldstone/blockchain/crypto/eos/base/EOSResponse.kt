package io.goldstone.blockchain.crypto.eos.base

import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toBigIntegerOrZero
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/05
 */
data class EOSResponse(
	val transactionID: String,
	val cupUsageByte: BigInteger,
	val netUsageByte: BigInteger,
	val executedStatus: Boolean,
	val result: String
) : Serializable {
	// Scatter Dapp 需要接收非对象化的链返回的数据, 故此增加 result 字段用于存储全量返回的结果
	constructor(transactionID: String, receipt: JSONObject, result: String) : this(
		transactionID,
		receipt.safeGet("cpu_usage_us").toBigIntegerOrZero(),
		// 从 response 拉取的是 `NetUsageWords` 转换成 `Byte` 需要额外乘 `8`
		receipt.safeGet("net_usage_words").toBigIntegerOrZero() * BigInteger.valueOf(8),
		receipt.safeGet("status").equals("executed", true),
		result
	)
}