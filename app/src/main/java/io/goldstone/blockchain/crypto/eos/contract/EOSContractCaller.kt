package io.goldstone.blockchain.crypto.eos.contract

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
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
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger


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

	// JSON Object 结构
	//  {"account":"prochaintech","name":"click","authorization":[{"actor":"beautifulleo","permission":"active"}],"data":{"clickRequest":{"account":"beautifulleo","candyId":10,"ref":""}}}
	constructor(data: JSONObject, chainID: ChainID) : this(
		EOSAuthorization(
			JSONArray(data.safeGet("authorization")).toJSONObjectList()[0].safeGet("actor"),
			EOSActor.getActorByValue(JSONArray(data.safeGet("authorization")).toJSONObjectList()[0].safeGet("permission"))!!
		),
		EOSCodeName(data.safeGet("account")),
		EOSTransactionMethod(data.safeGet("name")),
		chainID,
		data.safeGet("data")
	)

	constructor(data: JSONObject) : this(
		EOSAuthorization(data.safeGet("actor"), EOSActor.Active),
		EOSCodeName(data.safeGet("code")),
		EOSTransactionMethod(data.safeGet("method")),
		ChainID(data.safeGet("chainID")),
		data.safeGet("data")
	)

	override fun serialized(
		@WorkerThread hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		if (!chainID.isEOSSeries()) {
			hold(null, GoldStoneError("Wrong Chain ID"))
			return
		}
		EOSAPI.getTransactionHeader(ExpirationType.FiveMinutes) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				// data 和 memo 不同, 对 data 数据签名还要对对象的具体类型做判断
				// 如果 data 没有对象化, 那么就做简单的编码处理
				val dataCode: String
				if(data.contains("{")) {
					val dataObject = JSONObject(data)
					val coreObject =
						if (dataObject.names().length() == 1 && data.substringAfter(":").contains("{"))
							dataObject.getTargetObject(dataObject.names()[0].toString())
						else dataObject
					dataCode =
						coreObject.names().toList().map {
							val value = coreObject.get(it)
							when {
								value is Int -> EOSUtils.convertAmountToCode(BigInteger.valueOf(value.toLong()))
								value.toString().contains("ref", true) ->
									EOSUtils.convertMemoToCode(value.toString())
								else -> EOSUtils.getLittleEndianCode(value.toString())
							}
						}.joinToString("") { it }
				} else {
					dataCode = EOSUtils.getLittleEndianCode(data)
				}
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