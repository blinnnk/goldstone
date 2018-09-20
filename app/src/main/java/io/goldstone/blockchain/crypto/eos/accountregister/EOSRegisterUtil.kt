package io.goldstone.blockchain.crypto.eos.accountregister

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.eosram.EOSBuyRamModel
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.netcpumodel.BandWidthModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSChain

/**
 * @author KaySaith
 * @date 2018/09/05
 */

object EOSRegisterUtil {
	fun getRegisterSerializedCode(
		chainID: EOSChain,
		header: TransactionHeader,
		newAccountModel: EOSNewAccountModel,
		ramModel: EOSBuyRamModel,
		netCPUModel: BandWidthModel,
		isPackedData: Boolean
	): String {
		val contextFreeAction = "00"
		val actions = listOf(newAccountModel, ramModel, netCPUModel)
		val serializedActionSize = EOSUtils.getVariableUInt(actions.size)
		val serializedTransactionExtensions = "00"
		val netAndCpuSerializedData = netCPUModel.serialize()
		val packedData = header.serialize() + contextFreeAction + serializedActionSize +
			newAccountModel.serialize() + ramModel.serialize() + netAndCpuSerializedData + serializedTransactionExtensions
		return if (isPackedData) packedData
		else chainID.id + packedData + EOSUtils.completeZero(BandWidthModel.totalSerializedCount - netAndCpuSerializedData.length)
	}
}