package io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter

import android.annotation.SuppressLint
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.rammarket.model.RAMTradeRoomData
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.model.RAMPriceTable
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMPMarketDetailPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.math.BigDecimal

/**
 * @date: 2018/11/2.
 * @author: yanglihai
 * @description:
 */

fun RAMPMarketDetailPresenter.saveCandleDataToDatabase() {
	doAsync {
		val localData = GoldStoneDataBase.database.ramPriceDao().getData()
		val minuteJson = candleDataMap[EOSRAMChartType.Minute.info]?.let {
			Gson().toJson(it)
		}
		val hourJson = candleDataMap[EOSRAMChartType.Hour.info]?.let {
			Gson().toJson(it)
		}
		val dayJson = candleDataMap[EOSRAMChartType.Day.info]?.let {
			Gson().toJson(it)
		}
		if (localData.isNull()) {
			GoldStoneDataBase.database.ramPriceDao().insert(
				RAMPriceTable(
					0,
					minuteJson,
					hourJson,
					dayJson
				)
			)
		} else {
			localData!!.minuteData = minuteJson
			localData.hourData = hourJson
			localData.dayData = dayJson
			GoldStoneDataBase.database.ramPriceDao().update(localData)
		}
	}
}

fun RAMPMarketDetailPresenter.getChartDataFromDatebase(callback: () -> Unit) {
	val type = object : TypeToken<ArrayList<CandleChartModel>>() {}.type
	doAsync {
		GoldStoneDataBase.database.ramPriceDao().getData()?.apply {
			minuteData?.let { jsonString ->
				candleDataMap.put(
					EOSRAMChartType.Minute.info,
					Gson().fromJson(jsonString, type)
				)
			}
			hourData?.let { jsonString ->
				candleDataMap.put(
					EOSRAMChartType.Hour.info,
					Gson().fromJson(jsonString, type)
				)
			}
			dayData?.let { jsonString ->
				candleDataMap.put(
					EOSRAMChartType.Day.info,
					Gson().fromJson(jsonString, type)
				)
			}
		}
		callback()
	}
}

fun RAMPMarketDetailPresenter.updateRAMCandleData(ramChartType: EOSRAMChartType) {
	if (candleDataMap.isEmpty()) {
		getChartDataFromDatebase {
			updateChartDataFromNet(ramChartType)
		}
	} else {
		doAsync {
			updateChartDataFromNet(ramChartType)
		}
	}
	
}

fun RAMPMarketDetailPresenter.updateChartDataFromNet(ramChartType: EOSRAMChartType) {
	val period = ramChartType.info
	val dateType = ramChartType.dateType
	var size: Int = 0
	if (candleDataMap.containsKey(ramChartType.info)) {
		val ramTypeData = candleDataMap[ramChartType.info]
		ramTypeData!!.maxBy { it.time }?.apply {
			// 根据时间差计算出需要请求的数据条目数
			val timeDistance = System.currentTimeMillis() - this.time.toLong()
			size = when (ramChartType.code) {
				EOSRAMChartType.Minute.code -> (timeDistance / (1000 * 60)).toInt()
				EOSRAMChartType.Hour.code -> (timeDistance / (1000 * 60 * 60)).toInt()
				else -> (timeDistance / (1000 * 60 * 60 * 24)).toInt()
			}
			if (size > DataValue.candleChartCount)  {
				size = DataValue.candleChartCount
			}
		}
	} else {
		size = DataValue.candleChartCount
	}
	if (size == 0) {
		GoldStoneAPI.context.runOnUiThread {
			candleDataMap[period]?.apply {
				fragment.updateCandleChartUI(dateType, this)
			}
		}
	} else {
		GoldStoneAPI.getEOSRAMPriceTendcyCandle(period, size) { candleData, error ->
			if (error.isNone()) {
				// 更新 `UI` 界面
				candleData?.let {
					if (candleDataMap[period].isNull()) {
						candleDataMap[period] = it
					} else {
						candleDataMap[period]!!.apply {
							addAll(it)
							val resultList = subList(this.size - DataValue.candleChartCount, this.size).toArrayList()
							clear()
							addAll(resultList)
						}
					}
					GoldStoneAPI.context.runOnUiThread {
						candleDataMap[period]?.apply {
							fragment.updateCandleChartUI(dateType, this)
						}
					}
				}
			} else {
				GoldStoneAPI.context.runOnUiThread {
					// Show the error exception to user
					fragment.context.alert(error.toString().showAfterColonContent())
					candleDataMap[period]?.apply {
						fragment.updateCandleChartUI(dateType, this)
					}
				}
			}
		}
	}
}

fun RAMPMarketDetailPresenter.updateTodayPriceUI() {
	RAMTradeRoomData.ramInformationModel?.let {
		fragment.setTodayPrice(
			BigDecimal("${it.openPrice}").toPlainString(),
			BigDecimal("${it.HighPrice}").toPlainString(),
			BigDecimal("${it.lowPrice}").toPlainString()
		)
	}
	
}

fun RAMPMarketDetailPresenter.updateCurrentPriceUI() {
	RAMTradeRoomData.ramInformationModel?.let { ramInformationModel ->
		ramInformationModel.currentPrice?.let { current ->
			ramInformationModel.HighPrice?.let { high ->
				if (current > high) {
					ramInformationModel.HighPrice = current
					updateTodayPriceUI()
				}
			}
			ramInformationModel.lowPrice?.let { low ->
				if (current < low) {
					ramInformationModel.lowPrice = current
					updateTodayPriceUI()
				}
			}
			calculatePricePercent()
			fragment.setCurrentPriceAndPercent(
				BigDecimal("$current").toPlainString(),
				ramInformationModel.pricePercent.orZero()
			)
		}
		
	}
	
}


// 计算百分比
fun calculatePricePercent() {
	RAMTradeRoomData.ramInformationModel?.let { ramInformationModel ->
		ramInformationModel.openPrice?.let { open ->
			ramInformationModel.currentPrice?.let { current ->
				var trend = (current - open) / open
				trend *= 100f
				ramInformationModel.pricePercent = trend
			}
		}
	}
}


@SuppressLint("SetTextI18n")
fun RAMPMarketDetailPresenter.getTodayPrice() {
	GoldStoneAPI.getEOSRAMTodayPrice { model, error ->
		if (!model.isNull() && error.isNone()) {
			RAMTradeRoomData.ramInformationModel?.apply {
				openPrice = model!!.open.toFloat()
				HighPrice = model.high.toFloat()
				lowPrice = model.low.toFloat()
			}
		} else {
			GoldStoneAPI.context.runOnUiThread {
				fragment.context.alert(error.message)
			}
		}
		GoldStoneAPI.context.runOnUiThread {
			updateTodayPriceUI()
		}
		
	}
}







