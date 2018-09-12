package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toLongOrZero
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/12
 */

/**
 * @example
 * {"available":840178,"max":858312,"used":18134}
 */
data class ResourceLimit(
	val used: Long,
	val available: Long,
	val max: Long
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("used").toLongOrZero(),
		data.safeGet("available").toLongOrZero(),
		data.safeGet("max").toLongOrZero()
	)
}

class ResourceLimitConverter {
	@TypeConverter
	fun revertJSONObject(content: String): ResourceLimit {
		val resourceData = JSONObject(content)
		return ResourceLimit(
			resourceData.safeGet("used").toLongOrZero(),
			resourceData.safeGet("available").toLongOrZero(),
			resourceData.safeGet("max").toLongOrZero()
		)
	}

	@TypeConverter
	fun convertToString(resourceLimit: ResourceLimit): String {
		return "{\"available\":${resourceLimit.available},\"max\":${resourceLimit.max},\"used\":${resourceLimit.used}}"
	}
}