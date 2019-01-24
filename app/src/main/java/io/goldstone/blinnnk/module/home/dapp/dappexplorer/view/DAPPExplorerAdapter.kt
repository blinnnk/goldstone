package io.goldstone.blinnnk.module.home.dapp.dappexplorer.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blinnnk.module.home.dapp.dappcenter.view.applist.DAPPCell
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPExplorerAdapter(
	override val dataSet: ArrayList<DAPPTable>,
	private val holdHeader: DAPPExplorerHeader.() -> Unit,
	private val clickEvent: DAPPTable.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<DAPPTable, View, DAPPCell, View>() {
	override fun generateCell(context: Context) = DAPPCell(context)

	override fun generateFooter(context: Context) = View(context)

	override fun generateHeader(context: Context) = DAPPExplorerHeader(context).apply(holdHeader)

	override fun DAPPCell.bindCell(data: DAPPTable, position: Int) {
		model = data
		onClick {
			clickEvent(data)
			preventDuplicateClicks()
		}
	}

}