package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.presenter

import android.graphics.Color
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.contract.RAMDistributedContract
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMTradePercentPresenter(private val gsView: RAMDistributedContract.GSView)
	: RAMDistributedContract.GSPresenter {
	
	override fun start() {
		showLocalData()
		getTradeData()
	}
	
	private fun showLocalData() {
		try {
			val jsonData = GoldStoneAPI.context.getStringFromSharedPreferences(tradePercentKey)
			val type = object : TypeToken<ArrayList<Float>>() {}.type
			tradeDistributeList.addAll(
				Gson().fromJson(jsonData, type)
			)
		} catch (error: Exception) {
			LogUtil.error("RAMTradePercentPresenter", error)
		}
	}
	
	private val tradeDistributeList = arrayListOf<Float>()
	
	private val tradePercentKey = "eosRAMTradeDistribute"
	
	private val buyColors = arrayOf(
		Spectrum.green,
		Color.parseColor("#801CC881"),
		Color.parseColor("#FF55F6B6")
	)
	
	private val saleColors = arrayOf(
		Color.parseColor("#E14848"),
		Spectrum.lightRed,
		Color.parseColor("#FFAAAA")
	)
	
	
	fun onFragmentDestroy() {
		if (tradeDistributeList.isNotEmpty()) GoldStoneAPI.context.saveDataToSharedPreferences(
			tradePercentKey,
			Gson().toJson(tradeDistributeList)
		)
		
	}
	
	override fun getTradeData() {
		GoldStoneAPI.getEOSRAMTradeDistributed {data, error ->
			if (error.isNone()){
				data?.let {
					if (it.size == 6) {
						tradeDistributeList.clear()
						tradeDistributeList.addAll(it.toArrayList())
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
		gsView.updatePieChartData(
			tradeDistributeList.map {
				PieEntry(it, "")
			}.toArrayList(),
			(buyColors + saleColors).toList()
		)
		updateChartUI()
	}
	
	
	// 柱状图
	private fun updateChartUI() {
		val maxValue = tradeDistributeList.max()
		gsView.updateChartData(
			maxValue!!,
				arrayOf(
					tradeDistributeList[0],
					tradeDistributeList[1],
					tradeDistributeList[2]
				),
				buyColors,
				arrayOf(
					tradeDistributeList[3],
					tradeDistributeList[4],
					tradeDistributeList[5]
				),
				saleColors)
		
	}
	
}










