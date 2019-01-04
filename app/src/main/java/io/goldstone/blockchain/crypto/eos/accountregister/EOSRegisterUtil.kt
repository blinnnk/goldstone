package io.goldstone.blockchain.crypto.eos.accountregister

import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.eosram.EOSBuyRamModel
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.netcpumodel.BandWidthModel
import io.goldstone.blockchain.crypto.eos.transaction.completeZero
import io.goldstone.blockchain.crypto.multichain.ChainID

/**
 * @author KaySaith
 * @date 2018/09/05
 */

object EOSRegisterUtil {
	fun getRegisterSerializedCode(
		chainID: ChainID,
		header: TransactionHeader,
		newAccountModel: EOSNewAccountModel,
		ramModel: EOSBuyRamModel,
		bandWidthModel: BandWidthModel
	): EOSTransactionSerialization {
		val contextFreeAction = "00"
		val actions = listOf(newAccountModel, ramModel, bandWidthModel)
		val serializedActionSize = EOSUtils.getVariableUInt(actions.size)
		val serializedTransactionExtensions = "00"
		val netAndCpuSerializedData = bandWidthModel.serialize()
		val packedData = header.serialize() + contextFreeAction + serializedActionSize +
			newAccountModel.serialize() + ramModel.serialize() + netAndCpuSerializedData + serializedTransactionExtensions
		val serializedCode = chainID.id + packedData
		return EOSTransactionSerialization(packedData, serializedCode.completeZero())
	}
}