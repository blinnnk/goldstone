package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/03
 */

data class EOSAuthorization(
	val actor: String,
	val permission: EOSActor
) : Serializable, EOSModel {
	override fun createObject(): String {
		return "{\"actor\":\"$actor\",\"permission\":\"${permission.value}\"}"
	}

	override fun serialize(): String {
		return "${EOSUtils.getLittleEndianCode(actor)}${EOSUtils.getLittleEndianCode(permission.value)}"
	}

	companion object {
		fun createMultiAuthorizationObjects(vararg authorizations: EOSAuthorization): String {
			var objects = ""
			authorizations.forEach {
				objects += it.createObject() + ","
			}
			return "[${objects.substringBeforeLast(",")}]"
		}
	}
}