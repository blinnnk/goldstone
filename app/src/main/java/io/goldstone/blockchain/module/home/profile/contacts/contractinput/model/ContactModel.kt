package io.goldstone.blockchain.module.home.profile.contacts.contractinput.model

import java.io.Serializable

/**
 * @date 2018/8/22 9:35 PM
 * @author KaySaith
 */

data class ContactModel(
	val address: String,
	val symbol: String
): Serializable