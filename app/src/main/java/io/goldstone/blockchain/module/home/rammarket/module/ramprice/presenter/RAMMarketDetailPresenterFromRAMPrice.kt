package io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter

import android.annotation.SuppressLint
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.model.RAMPriceTable
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketDetailPresenter
import kotlinx.coroutines.*
import java.math.BigDecimal

/**
 * @date: 2018/11/2.
 * @author: yanglihai
 * @description:
 */

fun RAMMarketDetailPresenter.saveCandleDataToDatabase() {
	GlobalScope.launch(Dispatchers.Default) {
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
		if (localData == null) {
			GoldStoneDataBase.database.ramPriceDao().insert(
				RAMPriceTable(
					0,
					minuteJson,
					hourJson,
					dayJson
				)
			)
		} else {
			if (minuteJson != null && minuteJson.isNotEmpty()) localData.minuteData = minuteJson
			if (hourJson != null && hourJson.isNotEmpty()) localData.hourData = hourJson
			if (dayJson != null && dayJson.isNotEmpty()) localData.dayData = dayJson
			GoldStoneDataBase.database.ramPriceDao().update(localData)
		}
	}
}

fun RAMMarketDetailPresenter.getChartDataFromDatabase(dataType: String, callback: () -> Unit) {
	val type = object : TypeToken<ArrayList<CandleChartModel>>() {}.type
	GlobalScope.launch(Dispatchers.Default) {
		GoldStoneDataBase.database.ramPriceDao().getData()?.apply {
			candleDataMap[dataType] = when(dataType) {
				EOSRAMChartType.Minute.info -> {
					if (minuteData == null) arrayListOf() else Gson().fromJson(minuteData, type)
				}
				EOSRAMChartType.Hour.info -> {
					if (hourData == null) arrayListOf() else Gson().fromJson(hourData, type)
				}
				EOSRAMChartType.Day.info -> {
					if (dayData == null) arrayListOf() else Gson().fromJson(dayData, type)
				}
				else -> arrayListOf()
			}
		}
		callback()
	}
}

fun RAMMarketDetailPresenter.updateRAMCandleData(ramChartType: EOSRAMChartType) {
	if (candleDataMap.isEmpty() || candleDataMap[ramChartType.info].isNull()) {
		getChartDataFromDatabase(ramChartType.info) {
			calculateCountAndUpdate(ramChartType)
		}
	} else {
		GlobalScope.launch(Dispatchers.Default) {
			calculateCountAndUpdate(ramChartType)
		}
	}
	
}

fun RAMMarketDetailPresenter.calculateCountAndUpdate(ramChartType: EOSRAMChartType) {
	val ramTypeData = candleDataMap[ramChartType.info]
	if (ramTypeData != null) {
		ramTypeData.maxBy { it.time }.apply {
			if (this == null) {
				getCountDataFormNet(ramChartType, DataValue.candleChartCount)
			} else {
				// 根据时间差计算出需要请求的数据条目数
				val timeDistance = System.currentTimeMillis() - this.time.toLong()
				var size = when (ramChartType.code) {
					EOSRAMChartType.Minute.code -> (timeDistance / (1000 * 60)).toInt()
					EOSRAMChartType.Hour.code -> (timeDistance / (1000 * 60 * 60)).toInt()
					EOSRAMChartType.Day.code -> (timeDistance / (1000 * 60 * 60 * 24)).toInt()
					else -> 0
				}
				if (size > DataValue.candleChartCount) {
					size = DataValue.candleChartCount
				}
				
				if (size == 0 || size == 1) {
					// 只能取到前一个时间段的数据，所以size=1的时候，取得数据无效，所以直接更新UI
					launchUI {
						candleDataMap[ramChartType.info]?.apply {
							gsView.updateCandleChartUI(ramChartType.dateType, this)
						}
					}
				} else getCountDataFormNet(ramChartType, size)
			}
		}
	} else {
		getCountDataFormNet(ramChartType, DataValue.candleChartCount)
	}
}

fun RAMMarketDetailPresenter.getCountDataFormNet(ramChartType: EOSRAMChartType, count: Int) {
	val period = ramChartType.info
	val dateType = ramChartType.dateType
	GoldStoneAPI.getEOSRAMPriceTrendCandle(period, count) { data, error ->
		val candleData = data?.toArrayList()
		if (candleData != null && error.isNone()) {
			// 更新 `UI` 界面
			if (candleData.isNotEmpty()) {
				candleDataMap[period].let { localList ->
					if (localList == null || localList.isEmpty()) {
						candleDataMap[period] = candleData
					} else {
						localList.addAll(candleData)
						val resultList = localList.asSequence()
							.distinctBy { it.time }
							.sortedBy { it.time }
							.toList()
							.subList(localList.size - DataValue.candleChartCount, localList.lastIndex)
							.toArrayList()
						localList.clear()
						localList.addAll(resultList)
						
					}
				}
			}
			
			launchUI {
				candleDataMap[period]?.apply {
					gsView.updateCandleChartUI(dateType, this)
				}
			}
			
		} else {
			launchUI {
				// Show the error exception to user
				gsView.showError(error)
				candleDataMap[period]?.apply {
					gsView.updateCandleChartUI(dateType, this)
				}
			}
		}
	}
}

fun RAMMarketDetailPresenter.updateTodayPriceUI() {
	ramInformationModel.let {
		gsView.showTodayPrice(
			it.openPrice.formatCount(4),
			it.HighPrice.formatCount(4),
			it.lowPrice.formatCount(4)
		)
	}
	
}

fun RAMMarketDetailPresenter.updateCurrentPriceUI() {
	ramInformationModel.let { infoModel ->
		if (infoModel.currentPrice > infoModel.HighPrice) {
			infoModel.HighPrice = infoModel.currentPrice
		}
		if (infoModel.currentPrice < infoModel.lowPrice) {
			infoModel.lowPrice = infoModel.currentPrice
		}
		updateTodayPriceUI()
		calculatePricePercent()
		gsView.showCurrentPriceAndPercent(
			infoModel.currentPrice,
			infoModel.pricePercent
		)
		
	}
	
}


// 计算百分比
fun RAMMarketDetailPresenter.calculatePricePercent() {
	if (ramInformationModel.openPrice == 0.0) return
	val trend = BigDecimal((ramInformationModel.currentPrice - ramInformationModel.openPrice))
		.divide(BigDecimal(ramInformationModel.openPrice), 4, BigDecimal.ROUND_HALF_UP)
		.multiply(BigDecimal("100"))
		.toDouble()
	ramInformationModel.pricePercent = trend
}


@SuppressLint("SetTextI18n")
fun RAMMarketDetailPresenter.getTodayPrice() {
	GoldStoneAPI.getEOSRAMTodayPrice { model, error ->
		if (model != null && error.isNone()) {
			ramInformationModel.apply {
				openPrice = model.open.toDoubleOrZero()
				HighPrice = model.high.toDoubleOrZero()
				lowPrice = model.low.toDoubleOrZero()
			}
			calculatePricePercent()
		} else {
			launchUI{
				gsView.showError(error)
					// 出错了可能长连接已经断了， 需要在此给当前价格赋值
				gsView.showCurrentPriceAndPercent(
					ramInformationModel.currentPrice,
					ramInformationModel.pricePercent
				)
				
			}
		}
		launchUI {
			updateTodayPriceUI()
		}
	}
}






