package io.goldstone.blockchain.module.home.dapp.dappcenter.model

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/01
 */
data class DAPPModel(
	val id: String,
	val icon: String,
	val banner: String,
	val url: String,
	val title: String,
	val description: String,
	val tags: List<String>
): Serializable