package io.goldstone.blockchain.crypto.eos.accountregister

import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.account.EosPublicKey
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.completeZero
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import org.json.JSONObject
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/05
 * @important
 * Serialized Rules Attention
 * 1. `HexDateSize` 的测量是测量转码后的 `HexData` 转 `ByteArray` 后的长度
 * 2. `Keys Array Size` 的测量长度是使用 `4` 位 的 `Byte` 值记录. 不足 `4` 位需要补 `0`
 * 3. 整体拼接规则  Method Header + HexDataSize + HexData 的各自序列化
 */

data class EOSNewAccountModel(
	val authorizations: List<EOSAuthorization>,
	val creator: String,
	val newAccountName: String,
	val ownerThreshold: Int,
	val owners: List<ActorKey>,
	val ownerAccounts: List<AccountActor>,
	val activeThreshold: Int,
	val actives: List<ActorKey>,
	val activeAccounts: List<AccountActor>
) : Serializable, EOSModel {
	override fun createObject(): String {
		var authorizationObjects = ""
		authorizations.forEach {
			authorizationObjects += it.createObject() + ","
		}
		authorizationObjects = authorizationObjects.substringBeforeLast(",")
		var ownerObjects = ""
		owners.forEach {
			ownerObjects += it.createObject() + ","
		}
		ownerObjects = ownerObjects.substringBeforeLast(",")
		var ownerAccountsObjects = ""
		ownerAccounts.forEach {
			ownerAccountsObjects += it.createObject() + ","
		}
		ownerAccountsObjects = ownerAccountsObjects.substringBeforeLast(",")
		var activeObjects = ""
		actives.forEach {
			activeObjects += it.createObject() + ","
		}
		activeObjects = activeObjects.substringBeforeLast(",")
		var activeAccountObjects = ""
		activeAccounts.forEach {
			activeAccountObjects += it.createObject() + ","
		}
		activeAccountObjects = activeAccountObjects.substringBeforeLast(",")
		return "{\"account\":\"eosio\",\"name\":\"newaccount\",\"authorization\":[$authorizationObjects],\"data\":{\"creator\":\"$creator\",\"name\":\"$newAccountName\",\"owner\":{\"threshold\":$ownerThreshold,\"keys\":[$ownerObjects],\"accounts\":[$ownerAccountsObjects],\"waits\":[]},\"active\":{\"threshold\":$activeThreshold,\"keys\":[$activeObjects],\"accounts\":[$activeAccountObjects],\"waits\":[]}},\"hex_data\":\"\"}"
	}

	override fun serialize(): String {
		val serializedAccount = EOSUtils.getLittleEndianCode("eosio")
		val serializedMethodName = EOSUtils.getLittleEndianCode("newaccount")
		val serializedAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		var serializedAuthorizations = ""
		authorizations.forEach {
			serializedAuthorizations += it.serialize()
		}
		val serializedCreator = EOSUtils.getLittleEndianCode(creator)
		val serializedNewAccountName = EOSUtils.getLittleEndianCode(newAccountName)
		val serializedOwnerThreshold = EOSUtils.getVariableUInt(ownerThreshold)
		val variableUIntOwnerSize = EOSUtils.getVariableUInt(owners.size)
		val serializedOwnerSize = variableUIntOwnerSize + EOSUtils.completeZero(4 - variableUIntOwnerSize.length)
		/** 序列化 Owners */
		var serializedOwners = ""
		val accounts = "00" // TODO Account 业务逻辑还没做处理, 这里暂用空默认值
		val waits = "00"
		owners.forEach {
			// 反解析的时候, 发现多 Actor 的时候每个后面都要额外补上 Account, Waits. 不知道原因
			serializedOwners += it.serialize() + accounts + waits
		}
		/** 序列化 Actives */
		val serializedActiveThreshold = EOSUtils.getVariableUInt(activeThreshold)
		val variableUIntActiveSize = EOSUtils.getVariableUInt(owners.size)
		val serializedActiveSize = variableUIntActiveSize + EOSUtils.completeZero(4 - variableUIntActiveSize.length)
		var serializedActives = ""
		actives.forEach {
			serializedActives += it.serialize() + accounts + waits
		}
		val methodHeader = serializedAccount + serializedMethodName + serializedAuthorizationSize + serializedAuthorizations
		val serializedNewAccountModel = serializedCreator + serializedNewAccountName +
			serializedOwnerThreshold.completeZero(8 - serializedOwnerThreshold.length) + serializedOwnerSize + serializedOwners +
			serializedActiveThreshold.completeZero(8 - serializedActiveThreshold.length) + serializedActiveSize + serializedActives
		val hexDataByteArrayLength = EOSUtils.getHexDataByteLengthCode(serializedNewAccountModel)
		return methodHeader + hexDataByteArrayLength + serializedNewAccountModel
	}
}

data class ActorKey(
	val publicKey: String,
	val weight: Int
) : Serializable, EOSModel {

	constructor(data: JSONObject) : this(
		data.safeGet("key"),
		data.safeGet("weight").toIntOrZero()
	)

	override fun createObject(): String {
		return "{\"key\":\"$publicKey\",\"weight\":$weight}"
	}

	override fun serialize(): String {
		val serializedWeight = EOSUtils.getVariableUInt(weight)
		return "${EosPublicKey(publicKey).bytes.toNoPrefixHexString()}${serializedWeight.completeZero(4 - serializedWeight.length)}"
	}
}

data class AccountActor(
	val name: String,
	val permission: EOSActor,
	val weight: Int
) : Serializable, EOSModel {

	override fun createObject(): String {
		return "{\"permission\":{\"actor\":\"$name\",\"permission\":\"$permission\"},\"weight\":$weight}"
	}

	override fun serialize(): String {
		return ""
	}
}

enum class EOSActor(val value: String) {
	Empty(""),
	Owner("owner"),
	Active("active");

	fun isActive(): Boolean {
		return value.equals(Active.value, true)
	}

	fun isOwner(): Boolean {
		return value.equals(Owner.value, true)
	}

	companion object {
		fun getActorByValue(value: String): EOSActor {
			return when (value) {
				Owner.value -> Owner
				Active.value -> Active
				Empty.value, "Default" -> Empty // 服务空值和空的 SharedValue 的时候做的判断
				else -> throw Throwable("unknown permission")
			}
		}
	}
}