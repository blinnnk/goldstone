package io.goldstone.blockchain.crypto.eos.eosram

import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/05
 */


data class EOSRamModel(
	val authorizations: List<EOSAuthorization>,
	val payerName: String,
	val receiverName: String,
	val eosAmount: Long
) : Serializable, EOSModel {
	@Throws
	override fun createObject(): String {
		var authorizationObjects = ""
		authorizations.forEach {
			authorizationObjects += it.createObject() + ","
		}
		authorizationObjects = authorizationObjects.substringBeforeLast(",")
		val eosCount = EOSUtils.convertAmountToValidFormat(eosAmount)
		return "{\"account\":\"eosio\",\"name\":\"buyram\",\"authorization\":[$authorizationObjects],\"data\":{\"payer\":\"$payerName\",\"receiver\":\"$receiverName\",\"quant\":\"$eosCount ${CryptoSymbol.eos}\"},\"hex_data\":\"\"}"
	}

	override fun serialize(): String {
		val serializedAccount = EOSUtils.getLittleEndianCode("eosio")
		val serializedMethodName = EOSUtils.getLittleEndianCode("buyram")
		val serializedAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		var serializedAuthorizations = ""
		authorizations.forEach {
			serializedAuthorizations += it.serialize()
		}
		val serializedBuyInfo = EOSTransactionInfo(
			payerName,
			receiverName,
			eosAmount
		).serialize()
		val hexDataLength = EOSUtils.getHexDataByteLengthCode(serializedBuyInfo)
		return serializedAccount + serializedMethodName + serializedAuthorizationSize +
			serializedAuthorizations + hexDataLength + serializedBuyInfo
	}
}