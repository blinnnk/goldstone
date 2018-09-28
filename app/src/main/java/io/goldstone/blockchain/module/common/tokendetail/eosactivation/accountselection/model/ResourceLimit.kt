package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toBigIntegerOrZero
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/12
 */

/**
 * @example
 * {"available":840178,"max":858312,"used":18134}
 */
data class ResourceLimit(
	val used: BigInteger,
	val available: BigInteger,
	val max: BigInteger
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("used").toBigIntegerOrZero(),
		data.safeGet("available").toBigIntegerOrZero(),
		data.safeGet("max").toBigIntegerOrZero()
	)
}

class ResourceLimitConverter {
	@TypeConverter
	fun revertJSONObject(content: String): ResourceLimit {
		val resourceData = JSONObject(content)
		return ResourceLimit(
			resourceData.safeGet("used").toBigIntegerOrZero(),
			resourceData.safeGet("available").toBigIntegerOrZero(),
			resourceData.safeGet("max").toBigIntegerOrZero()
		)
	}

	@TypeConverter
	fun convertToString(resourceLimit: ResourceLimit): String {
		return "{\"available\":${resourceLimit.available},\"max\":${resourceLimit.max},\"used\":${resourceLimit.used}}"
	}
}