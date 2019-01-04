package io.goldstone.blinnnk.module.home.profile.chain.chainselection.model

/**
 * @date 2018/5/11 4:28 PM
 * @author KaySaith
 */

data class ChainSelectionModel(
	val title: String = "",
	val description: String = "",
	val icon: Int = 0,
	val isMainnet: Boolean = false
)