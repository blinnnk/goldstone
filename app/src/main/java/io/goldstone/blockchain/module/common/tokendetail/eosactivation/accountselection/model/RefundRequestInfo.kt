package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.suffix
import com.blinnnk.util.HoneyDateUtil
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.language.DateAndTimeText
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/12
 */
/**
 * @example
 * {"cpu_amount":"1.5000 EOS","net_amount":"1.5000 EOS","owner":"hopehopehope","request_time":"2018-09-12T06:20:58"}
 */
data class RefundRequestInfo(
	@SerializedName("cpu_amount")
	val cpuAmount: String,
	@SerializedName("net_amount")
	val netAmount: String,
	@SerializedName("owner")
	val owner: String, // owner
	@SerializedName("request_time")
	val requestTime: String // Formatted "2018-09-12T06:20:58"
) : Serializable {

	constructor() : this(
		"",
		"",
		"",
		""
	)

	constructor(data: JSONObject) : this(
		data.safeGet("cpu_amount"),
		data.safeGet("net_amount"),
		data.safeGet("owner"),
		data.safeGet("request_time")
	)

	private fun getTotalRefundEOSCount(): Double {
		val cpuCount = cpuAmount.substringBeforeLast(" ").toDoubleOrNull().orZero()
		val netCount = netAmount.substringBeforeLast(" ").toDoubleOrNull().orZero()
		return cpuCount + netCount
	}

	/**
	 * 释放的时间是首次操作 3 天后归还资源, 这里的计算就是当时的时间加上 3 天的毫秒数
	 * 另外这里需要注意的是 `UTC` 时间的换算
	 */
	fun getRefundDescription(): String {
		val totalRefundsEOS = getTotalRefundEOSCount().orZero()
		val expirationTimeStamp =
			if (totalRefundsEOS == 0.0) 0L
			else EOSUtils.getUTCTimeStamp(requestTime) + 86400000 * 3
		val expirationDate =
			if (expirationTimeStamp == 0L) ""
			else HoneyDateUtil.getSinceTime(expirationTimeStamp, DateAndTimeText.getDateText())
		return "$totalRefundsEOS" suffix CoinSymbol.eos + if (expirationDate.isNotEmpty()) " / $expirationDate" else ""
	}
}

class RefundInfoConverter {
	@TypeConverter
	fun revertJSONObject(content: String): RefundRequestInfo {
		val data = JSONObject(content)
		return RefundRequestInfo(
			data.safeGet("cpu_amount"),
			data.safeGet("net_amount"),
			data.safeGet("owner"),
			data.safeGet("request_time")
		)
	}

	@TypeConverter
	fun convertToString(refundInfo: RefundRequestInfo): String {
		return "{\"cpu_amount\":\"${refundInfo.cpuAmount}\",\"net_amount\":\"${refundInfo.netAmount}\",\"owner\":\"${refundInfo.owner}\",\"request_time\":\"${refundInfo.requestTime}\"}"
	}
}