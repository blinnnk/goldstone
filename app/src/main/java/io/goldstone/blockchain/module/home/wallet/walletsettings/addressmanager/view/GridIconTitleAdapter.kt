package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.model.GridIconTitleModel
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/11/18
 */
class GridIconTitleAdapter(
	override val dataSet: ArrayList<GridIconTitleModel>,
	private val clickEvent: (GridIconTitleModel) -> Unit
) : HoneyBaseAdapter<GridIconTitleModel, GridIconTitleCell>() {
	override fun generateCell(context: Context) = GridIconTitleCell(context)

	override fun GridIconTitleCell.bindCell(data: GridIconTitleModel, position: Int) {
		model = data
		onClick {
			clickEvent(data)
			preventDuplicateClicks()
		}
	}

}