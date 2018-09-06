package io.goldstone.blockchain.crypto.eos.accountregister

import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/05
 */
data class EOSrResponse(
	val transactionID: String,
	val cupUsageByte: Int,
	val netUsageByte: Int,
	val executedStatus: Boolean
) : Serializable {
	constructor(transactionID: String, receipt: JSONObject) : this(
		transactionID,
		receipt.safeGet("cpu_usage_us").toIntOrZero(),
		receipt.safeGet("net_usage_words").toIntOrZero() * 8,
		receipt.safeGet("status").equals("executed", true)
	)
}