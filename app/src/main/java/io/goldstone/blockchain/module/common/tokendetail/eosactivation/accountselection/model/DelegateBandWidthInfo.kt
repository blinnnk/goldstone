package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
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
	constructor(data: JSONObject) : this(
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("net_weight"),
		data.safeGet("cpu_weight")
	)
}

class DelegateInfoConverter {
	@TypeConverter
	fun revertJSONObject(content: String): DelegateBandWidthInfo {
		val data = JSONObject(content)
		return DelegateBandWidthInfo(
			data.safeGet("from"),
			data.safeGet("to"),
			data.safeGet("net_weight"),
			data.safeGet("cpu_weight")
		)
	}

	@TypeConverter
	fun convertToString(delegateInfo: DelegateBandWidthInfo): String {
		return "{\"cpu_weight\":\"${delegateInfo.cpuWeight}\",\"from\":\"${delegateInfo.fromName}\",\"net_weight\":\"${delegateInfo.netWeight}\",\"to\":\"${delegateInfo.toName}\"}"
	}
}