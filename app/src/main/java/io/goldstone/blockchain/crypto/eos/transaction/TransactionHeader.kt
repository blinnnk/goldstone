package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import java.io.Serializable

data class TransactionHeader(
	val refBlockNumber: Int,
	val refBlockPrefix: Int,
	val maxNetUsageWords: Int,
	val maxKCpuUsage: Int
) : Serializable {
	constructor(
		refBlockNumber: Int,
		refBlockPrefix: Int
	) : this(
		refBlockNumber,
		refBlockPrefix,
		0,
		0
	)

	constructor(data: EOSChainInfo) : this(
		EOSUtils.getRefBlockNumber(data.headBlockId),
		EOSUtils.getRefBlockPrefix(data.headBlockId),
		0,
		0
	)

	companion object {
		fun serialize(data: TransactionHeader): String {
			val serializedRefBlockNumber = EOSUtils.getRefBlockNumberCode(data.refBlockNumber)
			val serializeRefBlockPrefix = EOSUtils.getRefBlockPrefixCode(data.refBlockPrefix)
			val serializableMaxNetUsageWords = EOSUtils.getVariableUInt(data.maxNetUsageWords)
			val serializableMaxKCpuUsage = EOSUtils.getVariableUInt(data.maxKCpuUsage)
			return serializedRefBlockNumber + serializeRefBlockPrefix + serializableMaxNetUsageWords + serializableMaxKCpuUsage
		}
	}
}