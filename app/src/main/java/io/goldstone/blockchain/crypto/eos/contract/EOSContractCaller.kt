package io.goldstone.blockchain.crypto.eos.contract

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.transaction.*
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
	constructor(action: JSONObject, chainID: ChainID) : this(
		EOSAuthorization(
			JSONArray(action.safeGet("authorization")).toJSONObjectList()[0].safeGet("actor"),
			EOSActor.getActorByValue(JSONArray(action.safeGet("authorization")).toJSONObjectList()[0].safeGet("permission"))!!
		),
		EOSCodeName(action.safeGet("account")),
		EOSTransactionMethod(action.safeGet("name")),
		chainID,
		action.safeGet("data")
	)

	constructor(action: JSONObject) : this(
		EOSAuthorization(action.safeGet("actor"), EOSActor.Active),
		EOSCodeName(action.safeGet("code")),
		EOSTransactionMethod(action.safeGet("method")),
		ChainID(action.safeGet("chainID")),
		action.safeGet("data")
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
				if (data.contains("{")) {
					val dataObject = JSONObject(data)
					val coreObject =
						if (dataObject.names().length() == 1 && data.substringAfter(":").contains("{"))
							dataObject.getTargetObject(dataObject.names()[0].toString())
						else dataObject
					// For `BetDice` 的 `Lottery` 编码要把 `ExtendedSymbol` 的编码放到最后面
					val allNames = coreObject.names().toList().toArrayList()
					if (allNames.contains("extendedSymbol")) {
						allNames.remove("username")
						allNames.add("username")
					}
					dataCode =
						allNames.map { name ->
							val value = coreObject.get(name)
							when  {
								value is Int -> {
									val code = EOSUtils.convertAmountToCode(BigInteger.valueOf(value.toLong()))
									code.completeZero(16 - code.length)
								}
								// `BetDice` 的领取彩票需要对这个他们自定义的字段做特殊变异处理, 这里写了定制的枚举方法.
								name.equals("extendedSymbol", true) -> {
									EOSUtils.toLittleEndian(BigInteger(value.toString()).toString(16))
								}
								else -> EOSUtils.getLittleEndianCode(value.toString())
							}
						}.joinToString("") { it }
				} else {
					dataCode = EOSUtils.getLittleEndianCode(data)
				}
				val action = EOSAction(
					code,
					dataCode,
					method,
					listOf(authorization)
				)
				val serialization = EOSTransactionUtils.serialize(
					chainID,
					header,
					listOf(action)
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}

}