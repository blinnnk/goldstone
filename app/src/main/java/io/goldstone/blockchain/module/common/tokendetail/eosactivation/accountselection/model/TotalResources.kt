package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toLongOrZero
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/12
 */

/**
 * @example
 * {"cpu_weight":"23.5000 EOS","net_weight":"23.5000 EOS","owner":"hopehopehope","ram_bytes":87322}
 */
data class TotalResources(
	@SerializedName("net_weight")
	val netWeight: String,
	@SerializedName("cpu_weight")
	val cpuWeight: String,
	@SerializedName("ram_bytes")
	val ramBytes: Long,
	@SerializedName("owner")
	val owner: String
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("net_weight"),
		data.safeGet("cpu_weight"),
		data.safeGet("ram_bytes").toLongOrZero(),
		data.safeGet("owner")
	)
}

class TotalResourcesConverter {
	@TypeConverter
	fun revertJSONObject(content: String): TotalResources {
		val resourceData = JSONObject(content)
		return TotalResources(
			resourceData.safeGet("net_weight"),
			resourceData.safeGet("cpu_weight"),
			resourceData.safeGet("ram_bytes").toLongOrZero(),
			resourceData.safeGet("owner")
		)
	}

	@TypeConverter
	fun convertToString(data: TotalResources): String {
		return "{\"cpu_weight\":\"${data.cpuWeight}\",\"net_weight\":\"${data.netWeight}\",\"owner\":\"${data.owner}\",\"ram_bytes\":${data.ramBytes}}"
	}
}