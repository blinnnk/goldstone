package io.goldstone.blinnnk.module.common.walletimport.mnemonicimport.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter


/**
 * @author KaySaith
 * @date  2018/11/17
 */

data class PathModel(val chainName: String, val pathHeader: String, var defaultPath: String)

class PathAdapter(
	override val dataSet: ArrayList<PathModel>
) : HoneyBaseAdapter<PathModel, PathCell>() {
	override fun generateCell(context: Context) = PathCell(context)
	override fun PathCell.bindCell(data: PathModel, position: Int) {
		model = data
	}
}