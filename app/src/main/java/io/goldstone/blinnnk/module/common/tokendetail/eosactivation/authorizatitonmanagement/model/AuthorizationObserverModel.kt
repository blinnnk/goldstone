package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.model

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/27
 */
data class AuthorizationObserverModel(
	val publickey: String,
	val isDelete: Boolean
): Serializable