package io.goldstone.blinnnk.module.home.profile.chain.chainselection.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.module.home.profile.chain.chainselection.model.ChainSelectionModel

/**
 * @date 2018/5/11 4:27 PM
 * @author KaySaith
 */

class ChainSelectionAdapter(
	override val dataSet: ArrayList<ChainSelectionModel>,
	private val holdClickAction: ChainSelectionCell.() -> Unit
	) : HoneyBaseAdapter<ChainSelectionModel, ChainSelectionCell>() {
	override fun generateCell(context: Context) = ChainSelectionCell(context)

	override fun ChainSelectionCell.bindCell(
		data: ChainSelectionModel,
		position: Int
	) {
		model = data
		click(holdClickAction)
	}

}