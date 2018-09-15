package io.goldstone.blockchain.crypto.eos.accountregister

import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toLongOrZero
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/05
 */
data class EOSResponse(
	val transactionID: String,
	val cupUsageByte: Long,
	val netUsageByte: Long,
	val executedStatus: Boolean
) : Serializable {
	constructor(transactionID: String, receipt: JSONObject) : this(
		transactionID,
		receipt.safeGet("cpu_usage_us").toLongOrZero(),
		receipt.safeGet("net_usage_words").toLongOrZero() * 8,
		receipt.safeGet("status").equals("executed", true)
	)
}