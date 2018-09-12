package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.toList
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

	companion object {
		fun getPermissions(data: JSONArray): List<PermissionsInfo> {
			var permissions = listOf<PermissionsInfo>()
			data.toList().forEach {
				permissions += PermissionsInfo(JSONObject(it))
			}
			return permissions
		}

		fun generateByJSON(data: JSONObject): PermissionsInfo {
			return PermissionsInfo(
				data.safeGet("parent"),
				data.safeGet("perm_name"),
				RequiredAuthorization(JSONObject(data.safeGet("required_auth")))
			)
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
			permissions += PermissionsInfo.generateByJSON(permission)
		}
		return permissions
	}

	@TypeConverter
	fun convertToString(permissions: List<PermissionsInfo>): String {
		var content = ""
		permissions.forEach {
			content += "{\"parent\":\"${it.parent}\",\"perm_name\":\"${it.permissionName}\",\"required_auth\":${it.requiredAuthorization.getObject()}}" + ","
		}
		content = content.substringBeforeLast(",")
		return "[$content]"
	}
}