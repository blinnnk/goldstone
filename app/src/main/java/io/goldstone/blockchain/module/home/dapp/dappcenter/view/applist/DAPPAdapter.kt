package io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/12/02
 */
class DAPPAdapter(
	override val dataSet: ArrayList<DAPPTable>,
	private val hold: DAPPTable.() -> Unit
) : HoneyBaseAdapter<DAPPTable, DAPPCell>() {
	override fun generateCell(context: Context) = DAPPCell(context)

	override fun DAPPCell.bindCell(data: DAPPTable, position: Int) {
		model = data
		onClick {
			hold(data)
			preventDuplicateClicks()
		}
	}

}