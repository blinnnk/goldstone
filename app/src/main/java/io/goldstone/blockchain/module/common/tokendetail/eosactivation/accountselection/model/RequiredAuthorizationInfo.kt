package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import com.blinnnk.extension.toList
import io.goldstone.blockchain.crypto.eos.accountregister.ActorKey
import org.json.JSONArray
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/12
 */
data class RequiredAuthorization(
	val accounts: List<String>,
	val publicKeys: List<String>,
	val threshold: Int,
	val waits: List<String>
) {
	constructor(data: JSONObject) : this(
		JSONArray(data.safeGet("accounts")).toList(),
		JSONArray(data.safeGet("keys")).toList(),
		data.safeGet("threshold").toIntOrZero(),
		JSONArray(data.safeGet("waits")).toList()
	)

	fun getKeys(): List<ActorKey> {
		return publicKeys.map { ActorKey(JSONObject(it)) }
	}

	fun getObject(): String {
		var accountNames = ""
		accounts.forEach {
			accountNames += "$it,"
		}
		accountNames = accountNames.substringBeforeLast(",")
		var keys = ""
		publicKeys.forEach {
			keys += "$it,"
		}
		keys = keys.substringBeforeLast(",")
		var waitsValue = ""
		waits.forEach {
			waitsValue += "$it,"
		}
		waitsValue = waitsValue.substringBeforeLast(",")
		return "{\"accounts\":[$accountNames],\"keys\":[$keys],\"threshold\":$threshold,\"waits\":[$waitsValue]}"
	}
}

class RequiredAuthorizationConverter {
	@TypeConverter
	fun revertJSONObject(content: String): RequiredAuthorization {
		val data = JSONObject(content)
		return RequiredAuthorization(
			JSONArray(data.safeGet("accounts")).toList(),
			JSONArray(data.safeGet("keys")).toList(),
			data.safeGet("threshold").toIntOrZero(),
			JSONArray(data.safeGet("waits")).toList()
		)
	}

	@TypeConverter
	fun convertToString(data: RequiredAuthorization): String {
		var accountNames = ""
		data.accounts.forEach {
			accountNames += "$it,"
		}
		accountNames = accountNames.substringBeforeLast(",")
		var keys = ""
		data.publicKeys.forEach {
			keys += "$it,"
		}
		keys = keys.substringBeforeLast(",")
		var waitsValue = ""
		data.waits.forEach {
			waitsValue += "$it,"
		}
		waitsValue = waitsValue.substringBeforeLast(",")
		return "{\"accounts\":[$accountNames],\"keys\":[$keys],\"threshold\":${data.threshold},\"waits\":[$waitsValue]}"
	}
}