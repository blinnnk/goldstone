package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toJSONObjectList
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/12
 */

/**
 * @example
 * {"cpu_weight":"18.5000 EOS","from":"hopehopehope","net_weight":"18.5000 EOS","to":"hopehopehope"}
 */
data class DelegateBandWidthInfo(
	@SerializedName("from")
	val fromName: String,
	@SerializedName("to")
	val toName: String,
	@SerializedName("net_weight")
	val netWeight: String,
	@SerializedName("cpu_weight")
	val cpuWeight: String
) : Serializable {
	constructor() : this(
		"",
		"",
		"",
		""
	)

	constructor(data: JSONObject) : this(
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("net_weight"),
		data.safeGet("cpu_weight")
	)

	fun generateObject(): String {
		return "{\"cpu_weight\":\"$cpuWeight\",\"from\":\"$fromName\",\"net_weight\":\"$netWeight\",\"to\":\"$toName\"}"
	}
}

class DelegateBandInfoConverter {
	@TypeConverter
	fun revertJSONObject(content: String): List<DelegateBandWidthInfo> {
		return JSONArray(content).toJSONObjectList().map { DelegateBandWidthInfo(it) }
	}

	@TypeConverter
	fun convertToString(delegateInfo: List<DelegateBandWidthInfo>): String {
		return delegateInfo.map { it.generateObject() }.toString()
	}
}