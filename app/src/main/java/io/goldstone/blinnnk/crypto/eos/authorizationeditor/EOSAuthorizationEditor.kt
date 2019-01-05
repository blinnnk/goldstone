package io.goldstone.blinnnk.crypto.eos.authorizationeditor

import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSTransactionMethod
import io.goldstone.blinnnk.crypto.eos.EOSTransactionSerialization
import io.goldstone.blinnnk.crypto.eos.EOSUtils
import io.goldstone.blinnnk.crypto.eos.accountregister.ActorKey
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAction
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blinnnk.crypto.eos.transaction.ExpirationType
import io.goldstone.blinnnk.crypto.eos.transaction.completeZero
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.kernel.network.eos.EOSAPI
import io.goldstone.blinnnk.kernel.network.eos.contract.EOSTransactionInterface
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/26
 */
data class EOSAuthorizationEditor(
	private val chainID: ChainID,
	private val newActors: List<ActorKey>,
	private val parent: EOSActor,
	private val newPermission: EOSActor,
	private val newThreshold: Int,
	private val authorization: EOSAuthorization
) : Serializable, EOSTransactionInterface() {

	fun generateObject(): String {
		return "{\"action\":\"${EOSTransactionMethod.updateAuth().value}\",\"code\":\"${EOSCodeName.EOSIO.value}\",\"args\":{\"auth\":{\"keys\":[{\"key\":\"${newActors[0].publicKey}\",\"weight\":${newActors[0].weight}}],\"threshold\":$newThreshold,\"accounts\":[],\"waits\":[]},\"parent\":\"${parent.value}\",\"account\":\"${authorization.actor}\",\"permission\":\"${newPermission.value}\"}}"
	}

	override fun serialized(hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		EOSAPI.getTransactionHeader(ExpirationType.FiveMinutes) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				val serializedHeader = header.serialize()
				//  `contextFreeActions` 目前只有空的状态
				val contextFreeActions = listOf<String>()
				val serializedContextFreeActions = EOSUtils.getVariableUInt(contextFreeActions.size)

				val serializedAccount = EOSUtils.getLittleEndianCode(authorization.actor)
				val serializedNewPermission = EOSUtils.getLittleEndianCode(newPermission.value)
				val serializedParentPermission = EOSUtils.getLittleEndianCode(parent.value)
				val serializedThreshold = EOSUtils.getVariableUInt(newThreshold)
				val completeSerializedThreshold = serializedThreshold.completeZero(8 - serializedThreshold.length)
				val serializedNewActorSize = EOSUtils.getVariableUInt(newActors.size)
				val completeSerializedNewActorSize = serializedNewActorSize.completeZero(4 - serializedNewActorSize.length)
				// 研究编码的时候发现, `Keys` 个数超过 `1个` 的时候衔接 `Key` 的序列化会多出 `两个0` 目前还不知道原因暂时写死
				val serializedNewActor = newActors.mapIndexed { index, actorKey ->
					actorKey.serialize() + if (newActors.size > 1 && index < newActors.lastIndex) "00" else ""
				}.joinToString("") { it }
				val accounts = "00" // TODO Account 业务逻辑还没做处理, 这里暂用空默认值
				val waits = "00"
				val allSerialization = serializedAccount + serializedNewPermission + serializedParentPermission +
					completeSerializedThreshold + completeSerializedNewActorSize + serializedNewActor + accounts + waits

				val actions = listOf(
					EOSAction(
						EOSCodeName.EOSIO,
						allSerialization,
						EOSTransactionMethod.updateAuth(),
						listOf(authorization)
					)
				)
				val serializedActionSize = EOSUtils.getVariableUInt(actions.size)
				// 一整个一整个的序列化 `Action` 的子值, 这里只考虑了单一 `Action Child` 的情况
				var serializedActions = serializedActionSize
				actions.forEach { action ->
					serializedActions += action.serialize()
				}
				val serializedTransactionExtension = "00"
				val packedTX = serializedHeader +
					serializedContextFreeActions + serializedActions + serializedTransactionExtension
				val serializedData = chainID.id + packedTX
				hold(EOSTransactionSerialization(packedTX, serializedData.completeZero()), error)
			} else hold(null, error)
		}
	}
}