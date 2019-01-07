package io.goldstone.blinnnk.kernel.network.eos

import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSTransactionMethod
import io.goldstone.blinnnk.crypto.eos.EOSTransactionSerialization
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.transaction.*
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.kernel.network.eos.contract.EOSTransactionInterface
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
			// 准备 Action
			val action = EOSAction(
				EOSCodeName(contract.contract),
				transactionInfoCode,
				EOSTransactionMethod.transfer(),
				listOf(authorization)
			)
			if (header.isNotNull() && error.isNone()) {
				val serialization = EOSTransactionUtils.serialize(
					SharedChain.getEOSCurrent().chainID,
					header,
					listOf(action)
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}
}