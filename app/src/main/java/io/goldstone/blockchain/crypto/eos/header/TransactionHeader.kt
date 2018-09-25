package io.goldstone.blockchain.crypto.eos.header

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/05
 */

data class TransactionHeader(
	val expiration: ExpirationType,
	val refBlockNumber: Int,
	val refBlockPrefix: Int,
	val maxNetUsageWords: Int,
	val maxKCpuUsage: Int,
	val delaySecond: Int
) : Serializable, EOSModel {
	constructor(
		expiration: ExpirationType,
		refBlockNumber: Int,
		refBlockPrefix: Int
	) : this(
		expiration,
		refBlockNumber,
		refBlockPrefix,
		0,
		0,
		10
	)

	constructor(data: EOSChainInfo, expiration: ExpirationType) : this(
		expiration,
		EOSUtils.getRefBlockNumber(data.headBlockId),
		EOSUtils.getRefBlockPrefix(data.headBlockId),
		0,
		0,
		0
	)

	override fun createObject(): String {
		return "\"expiration\":\"${expiration.getCodeFromDate()}\",\"ref_block_num\":$refBlockNumber,\"ref_block_prefix\":$refBlockPrefix,\"max_net_usage_words\":$maxNetUsageWords,\"max_cpu_usage_ms\":$maxKCpuUsage,\"delay_sec\":$delaySecond,\"context_free_actions\":[]"
	}

	override fun serialize(): String {
		val serializedExpirationDate = EOSUtils.getExpirationCode(expiration.generate())
		val serializedRefBlockNumber = EOSUtils.getRefBlockNumberCode(refBlockNumber)
		val serializeRefBlockPrefix = EOSUtils.getRefBlockPrefixCode(refBlockPrefix)
		val serializableMaxNetUsageWords = EOSUtils.getVariableUInt(maxNetUsageWords)
		val serializableMaxKCpuUsage = EOSUtils.getVariableUInt(maxKCpuUsage)
		val serializableDelaySecond = EOSUtils.getVariableUInt(delaySecond)
		return serializedExpirationDate + serializedRefBlockNumber + serializeRefBlockPrefix + serializableMaxNetUsageWords + serializableMaxKCpuUsage + serializableDelaySecond
	}
}