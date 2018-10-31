package io.goldstone.blockchain.crypto.multichain.node

import com.blinnnk.extension.getRandom
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toList
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/10/29
 */
class ChainURL(
	val isEncrypt: Boolean,
	private val url: String,
	private val keyList: List<String>,
	val chainID: ChainID,
	val chainType: ChainType
) : Serializable {

	fun getURL(): String {
		val apiKey = if (keyList.isEmpty()) ""
		else "/${keyList.getRandom()}"
		return url + apiKey
	}

	constructor(data: JSONObject) : this(
		data.safeGet("encrypt_status").toInt() == 1,
		data.safeGet("chain_url"),
		JSONArray(data.safeGet("key_list")).toList(),
		ChainID(data.safeGet("chain_id")),
		ChainType(data.safeGet("chain_type").toInt())
	)

	constructor(data: ChainNodeTable) : this(
		data.isEncrypt == 1,
		data.url,
		data.keyList,
		ChainID(data.chainID),
		ChainType(data.chainType)
	)

	fun generateObject(): String {
		return "{\"encrypt_status\":\"${if (isEncrypt) 1 else 0}\",\"chain_url\":\"$url\",\"key_list\":\"$keyList\",\"chain_id\":\"${chainID.id}\",\"chain_type\":\"${chainType.id}\"}"
	}
}