package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.crypto.multichain.ChainType
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/30
 */
data class Bip44Address(
	val address: String,
	val index: Int,
	val chainType: Int
) : Serializable {
	constructor() : this(
		"",
		-1, // -1 means no address exist or it isn't a bip44 wallet,
		-1
	)

	constructor(address: String, chainType: Int) : this(address, -1, chainType)


	constructor(data: JSONObject) : this(
		data.safeGet("address"),
		data.safeGet("index").toInt(),
		data.safeGet("chainType").toInt()
	)

	fun getChainType(): ChainType = ChainType(chainType)

	fun generateObject(): String {
		return "{\"address\":\"$address\",\"index\":\"$index\",\"chainType\":\"$chainType\"}"
	}

	fun isEmpty() = address.isEmpty()
	fun isNotEmpty() = address.isNotEmpty()
}

class ListBip44AddressConverter {
	@TypeConverter
	fun revertJSONObject(content: String): List<Bip44Address> {
		val data = JSONArray(content)
		var bip44Address = listOf<Bip44Address>()
		(0 until data.length()).forEach {
			bip44Address += Bip44Address(JSONObject(data[it].toString()))
		}
		return bip44Address
	}

	@TypeConverter
	fun convertToString(models: List<Bip44Address>): String {
		var data = ""
		models.forEach {
			data += it.generateObject() + ","
		}
		data = data.substringBeforeLast(",")
		return "[$data]"
	}
}