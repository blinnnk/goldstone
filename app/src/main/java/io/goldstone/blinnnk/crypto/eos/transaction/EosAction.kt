package io.goldstone.blinnnk.crypto.eos.transaction

import com.blinnnk.extension.getTargetObject
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toJSONObjectList
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSTransactionMethod
import io.goldstone.blinnnk.crypto.eos.EOSUtils
import io.goldstone.blinnnk.crypto.eos.base.EOSModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date 2018/09/03
 * @description
 * 这里的 Account 传入的有时候是合约的 CodeName 例如,  转账的时候这里需要传递
 * `eosio.token` 这里说明, 防止被 Account 误导. (还没想到更好的命名)
 */

data class EOSAction(
	val code: EOSCodeName,
	val cryptoData: String,
	val method: EOSTransactionMethod,
	val authorizations: List<EOSAuthorization>
) : Serializable, EOSModel {

	constructor(data: JSONObject) : this(
		EOSCodeName(data.safeGet("account")),
		serializeData(data),
		EOSTransactionMethod(data.safeGet("name")),
		JSONArray(data.safeGet("authorization")).toJSONObjectList().map { EOSAuthorization(it) }
	)

	override fun createObject(): String {
		return "{\"account\":\"${code.value}\",\"authorization\":${authorizations.map { it.createObject() }},\"data\":\"$cryptoData\",\"name\":\"$${method.value}\"}"
	}

	override fun serialize(): String {
		return EOSUtils.getLittleEndianCode(code.value) +
			EOSUtils.getLittleEndianCode(method.value) +
			EOSUtils.getVariableUInt(authorizations.size) +
			authorizations.map {
				EOSUtils.getLittleEndianCode(it.actor) + EOSUtils.getLittleEndianCode(it.permission.value)
			}.joinToString("") { it } +
			EOSUtils.getVariableUInt(Hex.decode(cryptoData).size) + // Crypto Data Length
			cryptoData
	}

	companion object {
		fun createMultiActionObjects(vararg actions: EOSAction): String {
			val actionObjects = actions.joinToString(",") { it.createObject() }
			return "[$actionObjects]"
		}

		fun serializeData(action: JSONObject): String {
			val method = EOSTransactionMethod(action.safeGet("name"))
			return if (method.isTransfer()) {
				EOSTransactionInfo(action, action.getTargetObject("data").safeGet("memo")).serialize()
			} else {
				val data = action.getTargetObject("data")
				val allNames = data.names()
				var serializationData = ""
				(0 until allNames.length()).forEach {
					val value = data.safeGet(allNames[it].toString())
					serializationData += if (value.toLongOrNull().isNull()) {
						EOSUtils.getLittleEndianCode(value)
					} else {
						EOSUtils.convertAmountToCode(BigInteger(value))
					}
				}
				serializationData
			}
		}
	}
}