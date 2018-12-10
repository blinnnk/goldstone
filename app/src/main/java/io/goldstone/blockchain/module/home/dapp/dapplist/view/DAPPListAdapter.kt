package io.goldstone.blockchain.module.home.dapp.dapplist.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist.DAPPCell
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPListAdapter(
	override val dataSet: ArrayList<DAPPTable>,
	private val hold: (BottomLoadingView) -> Unit,
	private val clickEvent: (url: String) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<DAPPTable, View, DAPPCell, BottomLoadingView>() {

	override fun generateCell(context: Context) = DAPPCell(context)
	override fun generateFooter(context: Context) = BottomLoadingView(context).apply(hold)
	override fun generateHeader(context: Context) = View(context)

	override fun DAPPCell.bindCell(data: DAPPTable, position: Int) {
		model = data
		onClick {
			clickEvent(data.url)
			preventDuplicateClicks()
		}
	}

}