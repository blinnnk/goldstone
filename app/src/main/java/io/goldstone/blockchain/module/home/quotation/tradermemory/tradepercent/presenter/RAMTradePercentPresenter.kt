package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.presenter

import android.graphics.Color
import com.blinnnk.extension.toArrayList
import com.github.mikephil.charting.data.PieEntry
import io.goldstone.blockchain.common.Language.EOSRAMText
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
	
	private val buyColors = arrayOf(
		Color.parseColor("#1874CD"),
		Color.parseColor("#1E90FF"),
		Color.parseColor("#00B2EE")
	)
	
	private val saleColors = arrayOf(
		
		Color.parseColor("#EE3B3B"),
		Color.parseColor("#EE6A50"),
		Color.parseColor("#EE6AA7")
	)
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		
		getTradeData()
	}
	
	private fun getTradeData() {
		GoldStoneAPI.getEOSRAMTradeData( {
			fragment.context.alert(it.toString())
		}) {
			GoldStoneAPI.context.runOnUiThread {
				fragment.pieChart.resetData(
					it.map {
						PieEntry(it, "")
					}.toArrayList(),
					(buyColors + saleColors).toList()
				)
				
				if (it.size == 6) {
					setChartData(it)
					setOrderDescriptions(it)
				}
			}
		}
	}
	
	private fun setChartData(dataRows: ArrayList<Float>) {
		val maxValue = dataRows.max()
		
		fragment.apply {
			ramPercentChartIn.setDataAndColors(
				arrayOf(
					dataRows[0],
					dataRows[1],
					dataRows[2]),
				buyColors,
				maxValue!!)
			ramPercentChartOut.setDataAndColors(
				arrayOf(
					dataRows[3],
					dataRows[4],
					dataRows[5]),
				saleColors,
				maxValue)
		}
	}
	
	private fun setOrderDescriptions(dataRows: ArrayList<Float>) {
		fragment.apply {
			val buyValue = dataRows[0] + dataRows[1] + dataRows[2]
			buying.text = EOSRAMText.buying(buyValue.toString())
			
			val saleValue = dataRows[3] + dataRows[4] + dataRows[5]
			saling.text = EOSRAMText.saling(saleValue.toString())
		}
	}
	
}










