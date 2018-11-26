package io.goldstone.blockchain.crypto.eos.delegate

import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.transaction.EOSAction
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionUtils
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.contract.EOSTransactionInterface
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/22
 */
class EOSDelegateTransaction(
	private val from: EOSAccount, // 发起赎回的人 , 有点绕, 官方这么定义的.
	private val receiver: EOSAccount,
	private val cpuAmount: BigInteger,
	private val netAmount: BigInteger,
	private val expirationType: ExpirationType
) : EOSTransactionInterface() {

	override fun serialized(hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		EOSAPI.getTransactionHeaderFromChain(expirationType) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				val chainID = SharedChain.getEOSCurrent().chainID
				val transaction = EOSBandwidthInfo(
					from,
					receiver,
					cpuAmount,
					netAmount
				)
				val transactionCode = transaction.serialize()
				val authorization = EOSAuthorization(from.name, EOSActor.Active)
				val authorizationObject = EOSAuthorization.createMultiAuthorizationObjects(authorization)
				val action = EOSAction(
					EOSCodeName.EOSIOToken,
					transactionCode,
					EOSTransactionMethod.Undelegatebw,
					authorizationObject
				)
				val serialization = EOSTransactionUtils.serialize(
					chainID,
					header,
					listOf(action),
					listOf(authorization),
					transactionCode
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}

}