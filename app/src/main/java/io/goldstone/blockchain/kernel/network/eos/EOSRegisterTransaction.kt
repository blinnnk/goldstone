package io.goldstone.blockchain.kernel.network.eos

import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.accountregister.ActorKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSNewAccountModel
import io.goldstone.blockchain.crypto.eos.accountregister.EOSRegisterUtil
import io.goldstone.blockchain.crypto.eos.eosram.EOSBuyRamModel
import io.goldstone.blockchain.crypto.eos.netcpumodel.BandWidthModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.network.eos.contract.EOSTransactionInterface
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/21
 */
class EOSRegisterTransaction(
	private val chainID: ChainID,
	private val creator: EOSAuthorization,
	private val newAccountName: String,
	private val publicKey: String,
	private val ramAmount: BigInteger,
	private val cpuEOSAmount: BigInteger,
	private val netEOSAmount: BigInteger
) : Serializable, EOSTransactionInterface() {

	override fun serialized(
		hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit
	) {
		EOSAPI.getTransactionHeader(ExpirationType.FiveMinutes) { header, error ->
			val authorizations = listOf(creator)
			val newAccountModel = EOSNewAccountModel(
				authorizations,
				creator.actor,
				newAccountName,
				1,
				listOf(ActorKey(publicKey, 1)),
				listOf(),
				1,
				listOf(ActorKey(publicKey, 1)),
				listOf()
			)
			val ramModel = EOSBuyRamModel(
				authorizations,
				creator.actor,
				newAccountName,
				ramAmount
			)
			val bandWidth = BandWidthModel(
				authorizations,
				creator.actor,
				newAccountName,
				netEOSAmount,
				cpuEOSAmount,
				StakeType.Delegate,
				true
			)
			if (header.isNotNull() && error.isNone()) {
				val serialization = EOSRegisterUtil.getRegisterSerializedCode(
					chainID,
					header,
					newAccountModel,
					ramModel,
					bandWidth
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}

}