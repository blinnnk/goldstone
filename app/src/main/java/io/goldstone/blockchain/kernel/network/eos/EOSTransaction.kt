package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.UiThread
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSResponse
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
import io.goldstone.blockchain.crypto.eos.transaction.*
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.runOnUiThread
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
	private val symbol: String = CoinSymbol.eos
) {

	fun send(
		privateKey: EOSPrivateKey,
		errorCallback: (Throwable) -> Unit,
		@UiThread hold: (EOSResponse) -> Unit
	) {
		serialized(errorCallback) { data ->
			val signature = privateKey.sign(Sha256.from(Hex.decode(data.serialized))).toString()
			EOSAPI.pushTransaction(
				listOf(signature),
				data.packedTX,
				{
					LogUtil.error("EOSTransaction, Send", it)
				}
			) {
				GoldStoneAPI.context.runOnUiThread { hold(it) }
			}
		}
	}

	fun getSignHash(
		privateKey: String,
		errorCallback: (Throwable) -> Unit,
		hold: (signedHash: String) -> String
	) {
		serialized(errorCallback) {
			hold(EOSPrivateKey(privateKey).sign(Sha256.from(Hex.decode(it.serialized))).toString())
		}
	}

	private fun serialized(
		errorCallback: (Throwable) -> Unit,
		@UiThread hold: (EOSTransactionSerialization) -> Unit
	) {
		val transactionInfo = EOSTransactionInfo(
			fromAccount.actor,
			toAccountName,
			amount,
			memo,
			symbol
		)
		val transactionInfoCode = transactionInfo.serialize()
		EOSAPI.getTransactionHeaderFromChain(expirationType, errorCallback) { header ->
			val authorization = fromAccount
			val authorizationObjects = EOSAuthorization.createMultiAuthorizationObjects(authorization)
			// 准备 Action
			val action = EOSAction(
				EOSCodeName.EOSIOToken,
				transactionInfoCode,
				EOSTransactionMethod.Transfer,
				authorizationObjects
			)
			EOSTransactionUtils.serialize(
				EOSChain.Test,
				header,
				listOf(action),
				listOf(authorization),
				transactionInfoCode
			).let(hold)
		}
	}
}