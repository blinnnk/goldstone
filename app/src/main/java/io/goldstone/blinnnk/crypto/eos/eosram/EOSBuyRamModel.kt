package io.goldstone.blinnnk.crypto.eos.eosram

import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSUtils
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.base.EOSModel
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blinnnk.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blinnnk.crypto.multichain.CoinSymbol
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date 2018/09/05
 */

data class EOSBuyRamModel(
	val authorizations: List<EOSAuthorization>,
	val payerName: String,
	val receiverName: String,
	val eosAmount: BigInteger
) : Serializable, EOSModel {
	@Throws
	override fun createObject(): String {
		var authorizationObjects = ""
		authorizations.forEach {
			authorizationObjects += it.createObject() + ","
		}
		authorizationObjects = authorizationObjects.substringBeforeLast(",")
		val eosCount = EOSUtils.convertAmountToValidFormat(eosAmount)
		val method = StakeType.BuyRam.value
		return "{\"account\":\"eosio\",\"name\":\"$method\",\"authorization\":[$authorizationObjects],\"data\":{\"payer\":\"$payerName\",\"receiver\":\"$receiverName\",\"quant\":\"$eosCount ${CoinSymbol.eos}\"},\"hex_data\":\"\"}"
	}

	override fun serialize(): String {
		val method = StakeType.BuyRam.value
		val serializedAccount = EOSUtils.getLittleEndianCode(EOSCodeName.EOSIO.value)
		val serializedMethodName = EOSUtils.getLittleEndianCode(method)
		val serializedAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		var serializedAuthorizations = ""
		authorizations.forEach {
			serializedAuthorizations += it.serialize()
		}
		val serializedBuyInfo = EOSTransactionInfo(
			EOSAccount(payerName),
			EOSAccount(receiverName),
			eosAmount
		).serialize()
		val hexDataLength = EOSUtils.getHexDataByteLengthCode(serializedBuyInfo)
		return serializedAccount + serializedMethodName + serializedAuthorizationSize +
			serializedAuthorizations + hexDataLength + serializedBuyInfo
	}
}