package io.goldstone.blockchain.crypto.eos.eosram

import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/20
 */

data class EOSSellRamModel(
	val authorizations: List<EOSAuthorization>,
	val payerName: String,
	val ramByte: BigInteger
) : Serializable, EOSModel {
	@Throws
	override fun createObject(): String {
		var authorizationObjects = ""
		authorizations.forEach {
			authorizationObjects += it.createObject() + ","
		}
		authorizationObjects = authorizationObjects.substringBeforeLast(",")
		val method = StakeType.SellRam.value
		return "{\"account\":\"eosio\",\"name\":\"$method\",\"authorization\":[$authorizationObjects],\"data\":{\"account\":\"$payerName\",\"bytes\":$ramByte}}"
	}

	override fun serialize(): String {
		val method = StakeType.SellRam.value
		val serializedAccount = EOSUtils.getLittleEndianCode(EOSCodeName.EOSIO.value)
		val serializedMethodName = EOSUtils.getLittleEndianCode(method)
		val serializedAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		var serializedAuthorizations = ""
		authorizations.forEach {
			serializedAuthorizations += it.serialize()
		}
		val serializedPayerName = EOSUtils.getLittleEndianCode(payerName)
		val serializedRamByte = EOSUtils.convertAmountToCode(ramByte)
		val serializedSellData = serializedPayerName + serializedRamByte
		val hexDataLength = EOSUtils.getHexDataByteLengthCode(serializedSellData)
		return serializedAccount + serializedMethodName + serializedAuthorizationSize +
			serializedAuthorizations + hexDataLength + serializedSellData
	}
}