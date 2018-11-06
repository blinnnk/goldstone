package io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter

import android.annotation.SuppressLint
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.*
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.model.RAMPriceTable
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketDetailPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.math.BigDecimal

/**
 * @date: 2018/11/2.
 * @author: yanglihai
 * @description:
 */

fun RAMMarketDetailPresenter.saveCandleDataToDatabase() {
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
	doAsync {
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
			updateChartDataFromNet(ramChartType)
		}
	} else {
		doAsync {
			updateChartDataFromNet(ramChartType)
		}
	}
	
}

fun RAMMarketDetailPresenter.updateChartDataFromNet(ramChartType: EOSRAMChartType) {
	val period = ramChartType.info
	val dateType = ramChartType.dateType
	var size = 0
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
		GoldStoneAPI.getEOSRAMPriceTrendCandle(period, size) { candleData, error ->
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

fun RAMMarketDetailPresenter.updateTodayPriceUI() {
	ramInformationModel.let {
		fragment.setTodayPrice(
			it.openPrice.formatCount(8),
			it.HighPrice.formatCount(8),
			it.lowPrice.formatCount(8)
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
		fragment.setCurrentPriceAndPercent(
			BigDecimal("${infoModel.currentPrice}").toPlainString(),
			infoModel.pricePercent
		)
		
	}
	
}


// 计算百分比
fun RAMMarketDetailPresenter.calculatePricePercent() {
	var trend = (ramInformationModel.currentPrice - ramInformationModel.openPrice) / ramInformationModel.openPrice
	trend *= 100f
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
		} else {
			GoldStoneAPI.context.runOnUiThread {
				fragment.context.alert(error.message)
//				fragment.safeShowError(error)
					// 出错了可能长连接已经断了， 需要在此给当前价格赋值
				fragment.setCurrentPriceAndPercent(
					BigDecimal.valueOf(ramInformationModel.currentPrice).toPlainString(),
					ramInformationModel.pricePercent
				)
				
			}
		}
		GoldStoneAPI.context.runOnUiThread {
			updateTodayPriceUI()
		}
	}
}






