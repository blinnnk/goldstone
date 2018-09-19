package io.goldstone.blockchain.crypto.eos.netcpumodel

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date 2018/09/05
 */

data class BandWidthModel(
	val authorizations: List<EOSAuthorization>,
	val fromAccountName: String,
	val receiverAccountName: String,
	val stakeNetEOSAmount: BigInteger, // 含有精度的 40000 (4 EOS)
	val stakeCPUEOSAmount: BigInteger,
	val stakeType: StakeType,
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
		val transferObject = if (stakeType == StakeType.Delegate) ",\"transfer\":$transferTinyNumber" else ""
		return "{\"account\":\"eosio\",\"name\":\"delegatebw\",\"authorization\":[$authorizationObjects],\"data\":{\"from\":\"$fromAccountName\",\"receiver\":\"$receiverAccountName\",\"stake_net_quantity\":\"$netEOSCount ${CoinSymbol.eos}\",\"stake_cpu_quantity\":\"$cpuEOSCount ${CoinSymbol.eos}\"$transferObject},\"hex_data\":\"\"}"
	}

	override fun serialize(): String {
		val serializedAccount = EOSUtils.getLittleEndianCode("eosio")
		val serializedMethodName = EOSUtils.getLittleEndianCode(stakeType.value)
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
		// 如果是 `Refund Resource` 的操作, 是没有 `Transfer` 的参数的
		val serializedIsTransfer = if (stakeType == StakeType.Refund) "" else EOSUtils.getVariableUInt(transferTinyNumber)

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