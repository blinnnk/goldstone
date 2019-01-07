package io.goldstone.blinnnk.module.common.contract


/**
 * @author KaySaith
 * @date  2018/11/07
 */
interface GoldStoneView<out T : GoldStonePresenter> {

	val presenter: T
	fun showError(error: Throwable)

}