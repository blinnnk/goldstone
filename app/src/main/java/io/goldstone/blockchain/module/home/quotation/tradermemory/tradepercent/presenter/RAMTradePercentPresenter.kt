package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.presenter

import com.blinnnk.extension.toArrayList
import com.github.mikephil.charting.data.PieEntry
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view.RAMTradePercentFragment
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMTradePercentPresenter(override val fragment: RAMTradePercentFragment)
	: BasePresenter<RAMTradePercentFragment>() {
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		
		getTradeData()
	}
	
	private fun getTradeData() {
		GoldStoneAPI.getEOSRAMTradeData( {
			fragment.context.alert(it.toString())
		}) {
			GoldStoneAPI.context.runOnUiThread {
				var totalValue = 0.toDouble()
				it.forEach {
					totalValue += it
				}
				fragment.pieChart.resetData(
					it.map {
						PieEntry((it / totalValue).toFloat(), "")
					}.toArrayList()
				)
			}
		}
	}
	
}