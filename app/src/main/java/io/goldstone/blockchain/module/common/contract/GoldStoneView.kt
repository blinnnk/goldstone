package io.goldstone.blockchain.module.common.contract


/**
 * @author KaySaith
 * @date  2018/11/07
 */
interface GoldStoneView<out T : GoldStonePresenter> {

	val presenter: T

}