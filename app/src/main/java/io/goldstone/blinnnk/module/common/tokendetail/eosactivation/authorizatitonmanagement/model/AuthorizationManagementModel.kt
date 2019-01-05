package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.model

import io.goldstone.blinnnk.crypto.eos.accountregister.ActorKey
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/26
 */
data class AuthorizationManagementModel(
	val permission: String,
	val publicKey: String,
	val threshold: Int
) : Serializable {
	constructor(actor: ActorKey, permission: EOSActor) : this(
		permission.value,
		actor.publicKey,
		actor.weight
	)
}