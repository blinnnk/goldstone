package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.presenter

import android.graphics.Color
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.chart.pie.PieChartView
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.RAMTradeRefreshEvent
import io.goldstone.blockchain.module.home.quotation.tradermemory.RefreshReceiver
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.model.EOSRAMRankModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view.RAMTradePercentFragment
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMTradePercentPresenter(override val fragment: RAMTradePercentFragment)
	: BasePresenter<RAMTradePercentFragment>(), RefreshReceiver {
	
	private val tradeDistributeList = arrayListOf<Float>()
	
	private val tradePercentKey = "eosRAMTradeDistribute"
	
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
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		RAMTradeRefreshEvent.register(this)
		fragment.context?.apply {
			try {
				val jsonData = getStringFromSharedPreferences(tradePercentKey)
				val type = object : TypeToken<ArrayList<Float>>() {}.type
				tradeDistributeList.addAll(Gson().fromJson(jsonData, type))
			} catch (error: Exception) {
				LogUtil.error("RAMTradePercentPresenter", error)
			}
		}
	}
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		getTradeData()
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		RAMTradeRefreshEvent.unRegister(this)
		fragment.context?.apply {
			if (tradeDistributeList.isNotEmpty())
				saveDataToSharedPreferences(tradePercentKey, Gson().toJson(tradeDistributeList))
		}
	}
	
	private fun getTradeData() {
		GoldStoneAPI.getEOSRAMTradeData {data, error ->
			if (error.isNone()){
				data?.let {
					if (it.size == 6) {
						tradeDistributeList.clear()
						tradeDistributeList.addAll(it)
					}
				}
			}
			GoldStoneAPI.context.runOnUiThread {
				updateUI()
			}
			
		}
	}
	
	private fun updateUI() {
		if (tradeDistributeList.isEmpty()) return
		fragment.pieChart.updatePieChartUI()
		updateChartUI()
		setOrderDescriptions()
	}
	
	
	// 饼状图
	private fun PieChartView.updatePieChartUI() {
		resetData(
			tradeDistributeList.map {
				PieEntry(it, "")
			}.toArrayList(),
			(buyColors + saleColors).toList()
		)
	}
	
	// 柱状图
	private fun updateChartUI() {
		val maxValue = tradeDistributeList.max()
		
		fragment.apply {
			ramPercentChartIn.setDataAndColors(
				arrayOf(
					tradeDistributeList[0],
					tradeDistributeList[1],
					tradeDistributeList[2]),
				buyColors,
				maxValue!!)
			ramPercentChartOut.setDataAndColors(
				arrayOf(
					tradeDistributeList[3],
					tradeDistributeList[4],
					tradeDistributeList[5]),
				saleColors,
				maxValue)
		}
	}
	
	// 下边的描述信息
	private fun setOrderDescriptions() {
		fragment.apply {
			tradeDistributeList.let {
				val buyValue = it[0] + it[1] + it[2]
				buying.text = EOSRAMText.buying(buyValue.toString())
				
				val saleValue = it[3] + it[4] + it[5]
				saling.text = EOSRAMText.saling(saleValue.toString())
			}
			
		}
	}
	
	override fun onReceive(any: Any) {
		if (NetworkUtil.hasNetwork(fragment.context)) {
			getTradeData()
		}
	}
	
}










