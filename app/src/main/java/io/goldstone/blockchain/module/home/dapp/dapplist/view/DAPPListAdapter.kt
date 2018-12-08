package io.goldstone.blockchain.module.home.dapp.dapplist.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist.DAPPCell


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPListAdapter(
	override val dataSet: ArrayList<DAPPTable>
) : HoneyBaseAdapterWithHeaderAndFooter<DAPPTable, View, DAPPCell, BottomLoadingView>() {

	override fun generateCell(context: Context) = DAPPCell(context)
	override fun generateFooter(context: Context) = BottomLoadingView(context)
	override fun generateHeader(context: Context) = View(context)

	override fun DAPPCell.bindCell(data: DAPPTable, position: Int) {
		model = data
	}

}