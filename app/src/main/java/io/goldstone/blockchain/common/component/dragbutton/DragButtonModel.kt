package io.goldstone.blockchain.common.component.dragbutton


/**
 * @author KaySaith
 * @date  2018/12/14
 */
data class DragButtonModel(
	val icon: Int,
	val event: () -> Unit
)