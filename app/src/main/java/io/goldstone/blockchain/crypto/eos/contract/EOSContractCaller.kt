package io.goldstone.blockchain.crypto.eos.contract

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.transaction.EOSAction
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionUtils
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.contract.EOSTransactionInterface
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/11/30
 */
class EOSContractCaller(
	private val authorization: EOSAuthorization,
	private val code: EOSCodeName,
	private val method: EOSTransactionMethod,
	private val chainID: ChainID,
	private val data: String
) : EOSTransactionInterface() {
	constructor(data: JSONObject) : this(
		EOSAuthorization(data.safeGet("actor"), EOSActor.Active),
		EOSCodeName(data.safeGet("code")),
		EOSTransactionMethod(data.safeGet("method")),
		ChainID(data.safeGet("chainID")),
		data.safeGet("data")
	)

	override fun serialized(
		@WorkerThread hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		if (!chainID.isEOS()) {
			hold(null, GoldStoneError("Wrong Chain ID"))
			return
		}
		EOSAPI.getTransactionHeader(ExpirationType.FiveMinutes) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				val dataCode = EOSUtils.convertMemoToCode(data)
				val authorizationObject = EOSAuthorization.createMultiAuthorizationObjects(authorization)
				val action = EOSAction(
					code,
					dataCode,
					method,
					authorizationObject
				)
				val serialization = EOSTransactionUtils.serialize(
					chainID,
					header,
					listOf(action),
					listOf(authorization),
					dataCode
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}

}