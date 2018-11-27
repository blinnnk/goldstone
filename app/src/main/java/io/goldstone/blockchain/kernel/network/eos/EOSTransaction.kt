package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.transaction.*
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.network.eos.contract.EOSTransactionInterface
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/09/14
 * @description
 *  因为 EOS 的转账之前需要查询 链上的 ChainInf 做为签名的一部分,
 *  所以这个类放到了 NetWork EOS 里面
 */

class EOSTransaction(
	/** "{\"actor\":\"fromAccountName\",\"permission\":\"active\"}" */
	private val fromAccount: EOSAuthorization,
	private val toAccountName: String,
	private val amount: BigInteger,
	private val memo: String,
	private val expirationType: ExpirationType,
	private val contract: TokenContract
) : Serializable, EOSTransactionInterface() {

	override fun serialized(@UiThread hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		val transactionInfo = EOSTransactionInfo(
			EOSAccount(fromAccount.actor),
			EOSAccount(toAccountName),
			amount,
			memo,
			contract
		)
		val transactionInfoCode = transactionInfo.serialize()
		EOSAPI.getTransactionHeader(expirationType) { header, error ->
			val authorization = fromAccount
			val authorizationObjects = EOSAuthorization.createMultiAuthorizationObjects(authorization)
			// 准备 Action
			val action = EOSAction(
				EOSCodeName(contract.contract),
				transactionInfoCode,
				EOSTransactionMethod.Transfer,
				authorizationObjects
			)
			if (header.isNotNull() && error.isNone()) {
				val serialization = EOSTransactionUtils.serialize(
					SharedChain.getEOSCurrent().chainID,
					header,
					listOf(action),
					listOf(authorization),
					transactionInfoCode
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}
}