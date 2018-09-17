package io.goldstone.blockchain.crypto.eos.transaction

import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader

/**
 * @author KaySaith
 * @date 2018/09/03
 * EOS Generate PackageTx Rules
 * 按先后顺序对以下数据进行指定格式的转换
 * 1. ExpirationDate
 *    * 最大过期时间是 3600s
 *    * 单位是 Second
 *    * 如果当前时间超过了签名中的声明的过期时间交易会发送失败
 * 2. RefBlockNumber
 *     * 16进制后 `LittleEndian` 转换
 * 3. RefBlockPrefix
 *  		* 16进制后 `LittleEndian` 转换
 * 4. MaxNetUsageWords
 *      * putVariableUInt
 * 5. MaxKCpuUsage
 *      * putVariableUInt
 * 6. DelaySecond
 *      * putVariableUInt
 * 7. ContextFreeActions
 * 			 * 这个官方要求的是首先对 `List` 的 `Size` 进行编码, 然后再对 `List` 的条目进行编码
 * 			 * 因为这个值一只是空的, 目前值编码了 `List Size = 0` 的值, 子条目编码还没研究
 * 8. ActionSize
 * 9. AccountName
 *       * 这个 `Account` 通常是 "eos.token"
 * 10. Method
 *       * 这个 `Method` 通常是 `transfer`
 * 11. AuthorizationSize
 * 12. ActorName
 * 			 * 这个是 `Action` 里面的 `ActorName` `NameToLong` 然后 `LittleEndian` 转换
 * 13. Permission
 * 			 * 这个是 `Permission` 里面的 `permission` `NameToLong` 然后 `LittleEndian` 转换
 * 14. DataByteLength
 *       * Data 的 Length 编码
 * 15. Action Data
 *       * 转换后的 Action Data
 * 16. TransactionExtension
 *       * 这个通常是空的就直接 '00' 还没研究过非空状态
 */

object EOSTransactionUtils {
	fun serialize(
		chainID: EOSChain,
		header: TransactionHeader,
		actions: List<EOSAction>,
		authorizations: List<EOSAuthorization>,
		transactionInfoCryptoData: String
	): EOSTransactionSerialization {
		val serializedHeader = header.serialize()
		//  `contextFreeActions` 目前只有空的状态
		val contextFreeActions = listOf<String>()
		val serializedContextFreeActions = EOSUtils.getVariableUInt(contextFreeActions.size)
		val serializedActionSize = EOSUtils.getVariableUInt(actions.size)
		// 一整个一整个的序列化 `Action` 的子值, 这里只考虑了单一 `Action Child` 的情况
		var serializedActions = serializedActionSize
		actions.forEach { action ->
			serializedActions += EOSUtils.getLittleEndianCode(action.account.value) + EOSUtils.getLittleEndianCode(action.methodName.value)
		}
		val serializedAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		var serializedAuthorizations = serializedAuthorizationSize
		authorizations.forEach { authorization ->
			serializedAuthorizations += EOSUtils.getLittleEndianCode(authorization.actor) + EOSUtils.getLittleEndianCode(authorization.permission.value)
		}
		val serializedDataByteLength = EOSUtils.getVariableUInt(Hex.decode(transactionInfoCryptoData).size)
		val serializedTransactionExtension = "00"
		val packedTX = serializedHeader +
			serializedContextFreeActions + serializedActions + serializedAuthorizations +
			serializedDataByteLength + transactionInfoCryptoData + serializedTransactionExtension
		val serializedData = chainID.id + packedTX
		return EOSTransactionSerialization(packedTX, serializedData.completeZero())
	}

	private fun String.completeZero(): String {
		// 在签名结尾补充 64 个占位的 0, 目前是通过多次实验找到的规律, 并未找到具体官方文档要求说明.
		return this + EOSUtils.completeZero(64)
	}
}