package io.goldstone.blinnnk.crypto.eos.delegate

import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.error.TransferError
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSTransactionMethod
import io.goldstone.blinnnk.crypto.eos.EOSTransactionSerialization
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAction
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blinnnk.crypto.eos.transaction.EOSTransactionUtils
import io.goldstone.blinnnk.crypto.eos.transaction.ExpirationType
import io.goldstone.blinnnk.kernel.network.eos.EOSAPI
import io.goldstone.blinnnk.kernel.network.eos.contract.EOSTransactionInterface
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
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
		EOSAPI.getTransactionHeader(expirationType) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				val chainID = SharedChain.getEOSCurrent().chainID
				val permission = EOSAccountTable.getValidPermission(from, chainID)
				if (permission.isNull()) hold(null, TransferError.WrongPermission)
				else {
					val transaction = EOSBandwidthInfo(
						from,
						receiver,
						cpuAmount,
						netAmount
					)
					val transactionCode = transaction.serialize()
					val authorization = EOSAuthorization(from.name, permission)
					val action = EOSAction(
						EOSCodeName.EOSIO,
						transactionCode,
						EOSTransactionMethod.undelegatebw(),
						listOf(authorization)
					)
					val serialization = EOSTransactionUtils.serialize(
						chainID,
						header,
						listOf(action)
					)
					hold(serialization, error)
				}
			} else hold(null, error)
		}
	}

}