package io.goldstone.blinnnk.crypto.eos.contract

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSTransactionMethod
import io.goldstone.blinnnk.crypto.eos.EOSTransactionSerialization
import io.goldstone.blinnnk.crypto.eos.EOSUtils
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAction
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blinnnk.crypto.eos.transaction.EOSTransactionUtils
import io.goldstone.blinnnk.crypto.eos.transaction.ExpirationType
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.crypto.utils.toCryptHexString
import io.goldstone.blinnnk.kernel.network.eos.EOSAPI
import io.goldstone.blinnnk.kernel.network.eos.contract.EOSTransactionInterface
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
			EOSActor.getActorByValue(JSONArray(action.safeGet("authorization")).toJSONObjectList()[0].safeGet("permission"))
		),
		EOSCodeName(action.safeGet("account")),
		EOSTransactionMethod(action.safeGet("name")),
		chainID,
		action.safeGet("data")
	)

	constructor(action: JSONObject) : this(
		EOSAuthorization(action.safeGet("actor"), EOSActor.getActorByValue(action.safeGet("permission"))),
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
					dataCode = allNames.map { name ->
						val value = coreObject.get(name)
						when {
							value is Int ->
								EOSUtils.convertAmountToCode(BigInteger.valueOf(value.toLong()))
							// `BetDice` 的领取彩票需要对这个他们自定义的字段做特殊变异处理, 这里写了定制的枚举方法.
							name.equals("extendedSymbol", true) ->
								EOSUtils.toLittleEndian(BigInteger(value.toString()).toString(16))
							else -> {
								// 如果是 `AccountName` 的话用 特殊的编码方式, 否则只是 `16` 进制 `Hex`
								if (EOSAccount(value.toString()).isValid(false))
									EOSUtils.getLittleEndianCode(value.toString())
								else value.toString().toCryptHexString()
							}
						}
					}.joinToString("") { it }
				} else {
					// 如果是 `AccountName` 的话用 特殊的编码方式, 否则只是 `16` 进制 `Hex`
					dataCode = if (EOSAccount(data).isValid(false))
						EOSUtils.getLittleEndianCode(data)
					else EOSUtils.convertMemoToCode(data)
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