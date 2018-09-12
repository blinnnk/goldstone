package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.kernel.databaseinterface.RoomModel
import org.json.JSONArray
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/13
 */

data class EOSAccountInfo(
	val name: String,
	val chainID: String
) : RoomModel {
	constructor(data: JSONObject) : this(
		data.safeGet("name"),
		data.safeGet("chainID")
	)

	override fun getObject(): String {
		return "{\"name\":\"$name\",\"chainID\":\"$chainID\"}"
	}
}

class EOSAccountInfoConverter {
	@TypeConverter
	fun convertToModel(content: String): List<EOSAccountInfo> {
		val data = JSONArray(content)
		var accounts = listOf<EOSAccountInfo>()
		(0 until data.length()).forEach {
			accounts += EOSAccountInfo(JSONObject(data[it].toString()))
		}
		return accounts
	}

	@TypeConverter
	fun convertToString(models: List<EOSAccountInfo>): String {
		var data = ""
		models.forEach {
			data += it.getObject() + ","
		}
		data = data.substringBeforeLast(",")
		return "[$data]"
	}
}