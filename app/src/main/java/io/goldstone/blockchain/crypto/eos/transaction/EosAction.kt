package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import java.io.Serializable

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
	val methodName: EOSTransactionMethod,
	val authorizationObjects: List<EOSAuthorization>
) : Serializable, EOSModel {
	override fun createObject(): String {
		return "{\"account\":\"${code.value}\",\"authorization\":${authorizationObjects.map { it.createObject() }},\"data\":\"$cryptoData\",\"name\":\"$${methodName.value}\"}"
	}

	override fun serialize(): String {
		return ""
	}

	companion object {
		fun createMultiActionObjects(vararg actions: EOSAction): String {
			val actionObjects = actions.joinToString(",") { it.createObject() }
			return "[$actionObjects]"
		}
	}
}