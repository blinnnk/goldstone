package io.goldstone.blockchain.crypto.eos.accountregister

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
	val executedStatus: Boolean
) : Serializable {
	constructor(transactionID: String, receipt: JSONObject) : this(
		transactionID,
		receipt.safeGet("cpu_usage_us").toBigIntegerOrZero(),
		receipt.safeGet("net_usage_words").toBigIntegerOrZero() * BigInteger.valueOf(8),
		receipt.safeGet("status").equals("executed", true)
	)
}