package io.goldstone.blockchain.crypto.eos.netcpumodel

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/05
 */

data class EOSNetCPUModel(
	val authorizations: List<EOSAuthorization>,
	val fromAccountName: String,
	val receiverAccountName: String,
	val stakeNetEOSAmount: Long,
	val stakeCPUEOSAmount: Long,
	val isTransfer: Boolean
) : Serializable, EOSModel {
	override fun createObject(): String {
		var authorizationObjects = ""
		authorizations.forEach {
			authorizationObjects += it.createObject() + ","
		}
		authorizationObjects = authorizationObjects.substringBeforeLast(",")
		val netEOSCount = EOSUtils.convertAmountToValidFormat(stakeNetEOSAmount)
		val cpuEOSCount = EOSUtils.convertAmountToValidFormat(stakeCPUEOSAmount)
		val transferTinyNumber = if (isTransfer) 1 else 0
		return "{\"account\":\"eosio\",\"name\":\"delegatebw\",\"authorization\":[$authorizationObjects],\"data\":{\"from\":\"$fromAccountName\",\"receiver\":\"$receiverAccountName\",\"stake_net_quantity\":\"$netEOSCount ${CoinSymbol.eos}\",\"stake_cpu_quantity\":\"$cpuEOSCount ${CoinSymbol.eos}\",\"transfer\":$transferTinyNumber},\"hex_data\":\"\"}"
	}

	override fun serialize(): String {
		val serializedAccount = EOSUtils.getLittleEndianCode("eosio")
		val serializedMethodName = EOSUtils.getLittleEndianCode("delegatebw")
		val serializedAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		var serializedAuthorizations = ""
		authorizations.forEach {
			serializedAuthorizations += it.serialize()
		}
		val serializedFromName = EOSUtils.getLittleEndianCode(fromAccountName)
		val serializedReceiverName = EOSUtils.getLittleEndianCode(receiverAccountName)
		val serializedStakeNetQuantity = EOSTransactionInfo.serializedEOSAmount(stakeNetEOSAmount)
		val serializedStakeCPUQuantity = EOSTransactionInfo.serializedEOSAmount(stakeCPUEOSAmount)
		val transferTinyNumber = if (isTransfer) 1 else 0
		val serializedIsTransfer = EOSUtils.getVariableUInt(transferTinyNumber)

		val serializedNetCPUModel = serializedFromName + serializedReceiverName +
			serializedStakeNetQuantity + serializedStakeCPUQuantity + serializedIsTransfer
		val hexDataLength = EOSUtils.getHexDataByteLengthCode(serializedNetCPUModel)
		return serializedAccount + serializedMethodName + serializedAuthorizationSize +
			serializedAuthorizations + hexDataLength + serializedNetCPUModel
	}

	companion object {
		const val totalSerializedCount = 230
	}

}