package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toList
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * @author KaySaith
 * @date  2018/09/12
 */
/**
 * @example
 * [{"parent":"owner","perm_name":"active","required_auth":{"accounts":[],"keys":[{"key":"EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr","weight":1}],"threshold":1,"waits":[]}}]
 */
data class PermissionsInfo(
	val parent: String,
	val permissionName: String,
	val requiredAuthorization: RequiredAuthorization
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("parent"),
		data.safeGet("perm_name"),
		RequiredAuthorization(JSONObject(data.safeGet("required_auth")))
	)

	fun generateObject(): String {
		return "{\"parent\":\"$parent\",\"perm_name\":\"$permissionName\",\"required_auth\":${requiredAuthorization.getObject()}}"
	}

	companion object {
		fun getPermissions(data: JSONArray): List<PermissionsInfo> {
			var permissions = listOf<PermissionsInfo>()
			data.toList().forEach {
				permissions += PermissionsInfo(JSONObject(it))
			}
			return permissions
		}
	}
}

class PermissionsInfoConverter {
	@TypeConverter
	fun revertJSONObject(content: String): List<PermissionsInfo> {
		val data = JSONArray(content)
		var permissions = listOf<PermissionsInfo>()
		(0 until data.length()).forEach {
			val permission = JSONObject(data.get(it).toString())
			permissions += PermissionsInfo(permission)
		}
		return permissions
	}

	@TypeConverter
	fun convertToString(permissions: List<PermissionsInfo>): String {
		return "[${permissions.joinToString(",") { it.generateObject() }}]"
	}
}