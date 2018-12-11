package io.goldstone.blockchain.kernel.network.eos.eosram

import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.eosram.EOSSellRamModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.eos.transaction.completeZero
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.contract.EOSTransactionInterface
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/20
 */
class EOSSellRamTransaction(
	private val chainID: ChainID,
	private val payerName: EOSAuthorization,
	private val ramByte: BigInteger,
	private val expirationType: ExpirationType
) : Serializable, EOSTransactionInterface() {
	override fun serialized(hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		val model = EOSSellRamModel(
			listOf(payerName),
			payerName.actor,
			ramByte
		)
		EOSAPI.getTransactionHeader(expirationType) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				// 准备 Action
				//  `contextFreeActions` 目前只有空的状态
				val contextFreeActions = listOf<String>()
				val serializedActionSize = "01" // 目前不支持批量给多账户购买所以 `ActionSize` 写死 `1`
				val serializedContextFreeActions = EOSUtils.getVariableUInt(contextFreeActions.size)
				val serializedTransactionExtension = "00"
				val packedTX = header.serialize() + serializedContextFreeActions + serializedActionSize + model.serialize() + serializedTransactionExtension
				val serializedCode = (chainID.id + packedTX).completeZero()
				hold(EOSTransactionSerialization(packedTX, serializedCode), error)
			} else hold(null, error)
		}
	}
}

