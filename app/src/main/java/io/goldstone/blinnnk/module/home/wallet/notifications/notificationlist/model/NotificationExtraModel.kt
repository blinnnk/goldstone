package io.goldstone.blinnnk.module.home.wallet.notifications.notificationlist.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toDoubleOrZero
import com.blinnnk.util.TinyNumber
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/10
 */

data class NotificationExtraModel(
	val isReceive: Boolean,
	val chainID: String,
	val fromAddress: String,
	val toAddress: String,
	val symbol: String,
	val value: Double
) : Serializable {

	constructor(data: JSONObject) : this(
		data.safeGet("from_or_to").toIntOrNull() == TinyNumber.True.value,
		data.safeGet("chainid"),
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("symbol"),
		data.safeGet("value").toDoubleOrZero()
	)

	fun generateObject(): String {
		val fromValue = if (fromAddress.contains("[")) fromAddress else "\"$fromAddress\""
		val toValue = if (toAddress.contains("[")) toAddress else "\"$toAddress\""
		val fromOrTo = if (isReceive) 1 else 0
		return "{\"from_or_to\":$fromOrTo,\"chainid\":\"$chainID\",\"from\":$fromValue,\"symbol\":\"$symbol\",\"value\":\"$value\",\"to\":$toValue}"
	}
}

/**
 * 转换的 `JSON` 格式
 * {"from_or_to":1,"chainid":"3","from":"0x43414b6061373613cfd888c8af5aa105a270d965","symbol":"ETH","value":"3","to":"0xac3a6cd0c086eb604a89d86dd01c6e1fb8a9fab3"}
 * {"from_or_to":0,"chainid":"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943","from":"[{"value":"0.492254","address":"mtLujvsriGN8Yj2dFKhSchZvrEsf3mwg2G"}]","symbol":"BTC","value":"0.01","to":"[{"value":"0.01","address":"n2AceL6NnXF13TcDz574szaujTonZuc6fC"},{"value":"0.479994","address":"mtLujvsriGN8Yj2dFKhSchZvrEsf3mwg2G"}]"}
 */
class NotificationExtraTypeConverter {
	@TypeConverter
	fun revertString(content: String): NotificationExtraModel {
		val data = try {
			JSONObject(content)
		} catch (error: Exception) {
			JSONObject("{}")
		}
		return NotificationExtraModel(data)
	}

	@TypeConverter
	fun convertToString(content: NotificationExtraModel): String {
		return content.generateObject()
	}
}