package io.goldstone.blockchain.module.home.profile.chainselection.model

/**
 * @date 2018/5/11 4:28 PM
 * @author KaySaith
 */

data class ChainSelectionModel(
	val title: String = "",
	val description: String = "",
	val icon: Int = 0,
	val isUsed: Boolean = false
)