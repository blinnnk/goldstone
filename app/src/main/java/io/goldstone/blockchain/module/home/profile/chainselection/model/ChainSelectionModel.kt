package io.goldstone.blockchain.module.home.profile.chainselection.model

/**
 * @date 2018/5/11 4:28 PM
 * @author KaySaith
 */

data class ChainSelectionModel(
	val chainName: String = "",
	val isUsed: Boolean = false,
	val color: Int = 0,
	val chainID: String = ""
)