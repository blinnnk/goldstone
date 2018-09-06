package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.eos.base.EOSModel
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/03
 */

data class EOSAction(
	val account: String,
	val cryptoData: String,
	val methodName: String,
	val authorizationObjects: String
) : Serializable, EOSModel {
	override fun createObject(): String {
		return "{\"account\":\"$account\",\"authorization\":$authorizationObjects,\"data\":\"$cryptoData\",\"name\":\"$methodName\"}"
	}

	override fun serialize(): String {
		return ""
	}

	companion object {
		fun createMultiActionObjects(vararg actions: EOSAction): String {
			var actionObjects = ""
			actions.forEach {
				actionObjects += it.createObject() + ","
			}
			return "[${actionObjects.substringBeforeLast(",")}]"
		}
	}
}