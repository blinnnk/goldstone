package io.goldstone.blockchain.module.home.rammarket.presenter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.*
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.model.RAMPriceTable
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.RecentTransactionModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.recentTransactions
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: 头部的price展示presenter
 */
class RAMPMarketDetailPresenter(override val fragment: RAMMarketDetailFragment)
 : BasePresenter<RAMMarketDetailFragment>() {
	private var candleDataMap: HashMap<String, ArrayList<CandleChartModel>> = hashMapOf()
	var recentTransactionModel: RecentTransactionModel? = null
	private var period: String = EOSRAMChartType.Hour.info
	private var dateType: Int = DateUtils.FORMAT_SHOW_TIME
	private val ramPriceSocket by lazy {
		object : GoldStoneWebSocket("{\"t\": \"unsub_eos_ram_service\"}") {
			override fun onOpened() {
				sendMessage("{\"t\":\"sub_eos_ram_service\"}")
			}
			
			override fun getServerBack(content: JSONObject, isDisconnected: Boolean) {
				if (isDisconnected) {
					fragment.setSocketDisconnectedPercentColor(GrayScale.midGray)
					return
				}
				val price = content.getString("price")
				val current = BigDecimal(price.isEmptyThen("0")).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toFloat()
				RAMTradeRoomData.ramInformationModel?.currentPrice = current
				GoldStoneAPI.context.runOnUiThread {
					updateCurrentPriceUI()
				}
			}
		}
	}
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		fragment.context?.apply {
			RAMTradeRoomData.ramInformationModel.isNull {
				RAMTradeRoomData.ramInformationModel = RAMInformationModel(
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null
				)
			}
		}
	}
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		getTodayPrice()
		recentTransactions()
	}
	
	override fun onFragmentResume() {
		super.onFragmentResume()
		ramPriceSocket.isSocketConnected() isFalse {
			ramPriceSocket.runSocket()
		}
	}
	
	override fun onFragmentPause() {
		super.onFragmentPause()
		ramPriceSocket.isSocketConnected() isTrue {
			ramPriceSocket.closeSocket()
		}
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		saveCandleDataToDatabase()
	}
	
	private fun saveCandleDataToDatabase() {
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
				localData.dayData =  dayJson
				GoldStoneDataBase.database.ramPriceDao().update(localData)
			}
		}
	}
	
	private fun getChartDataFromDatebase(callback: () -> Unit) {
		val type = object : TypeToken<ArrayList<CandleChartModel>>() {}.type
		doAsync {
			GoldStoneDataBase.database.ramPriceDao().getData()?.apply {
				minuteData?.let { jsonString ->
					candleDataMap.put(EOSRAMChartType.Minute.info,  Gson().fromJson(jsonString, type))
				}
				hourData?.let { jsonString ->
					candleDataMap.put(EOSRAMChartType.Hour.info,  Gson().fromJson(jsonString, type))
				}
				dayData?.let { jsonString ->
					candleDataMap.put(EOSRAMChartType.Day.info,  Gson().fromJson(jsonString, type))
				}
			}
			callback()
		}
	}
	
	fun updateRAMCandleData(ramChartType: EOSRAMChartType) {
		this@RAMPMarketDetailPresenter.period = ramChartType.info
		this@RAMPMarketDetailPresenter.dateType = ramChartType.dateType
		
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
	
	private fun updateChartDataFromNet(ramChartType: EOSRAMChartType) {
		var size: Int = 0
		if (candleDataMap.containsKey(ramChartType.info)) {
			val ramTypeData = candleDataMap[ramChartType.info]
			ramTypeData!!.maxBy {
				it.time
			}?.apply {
				val timeDistance = System.currentTimeMillis() - this.time.toLong()
				size = when(ramChartType.code) {
					EOSRAMChartType.Minute.code -> (timeDistance / (1000 * 60)).toInt()
					EOSRAMChartType.Hour.code -> (timeDistance / (1000 * 60 * 60)).toInt()
					else -> (timeDistance / (1000 * 60 * 60* 24)).toInt()
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
						if (candleDataMap[period].isNull())  {
							candleDataMap[period] = it
						} else {
							candleDataMap[period]!!.addAll(it)
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
	
	@SuppressLint("SetTextI18n")
	private fun getTodayPrice() {
		GoldStoneAPI.getEOSRAMTodayPrice { model, error ->
			if (!model.isNull()  && error.isNone()) {
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
	
	private fun updateTodayPriceUI() {
		RAMTradeRoomData.ramInformationModel?.let {
			fragment.setTodayPrice(BigDecimal("${it.openPrice}").toPlainString(),
				BigDecimal("${it.HighPrice}").toPlainString(),
				BigDecimal("${it.lowPrice}").toPlainString())
		}
		
	}
	
	
	private fun updateCurrentPriceUI() {
		RAMTradeRoomData.ramInformationModel?.let { ramInformationModel ->
			ramInformationModel.currentPrice?.let { current ->
				ramInformationModel.HighPrice?.let { high ->
					if (current > high) {
						ramInformationModel.HighPrice = current
						updateTodayPriceUI()
					}
				}
				ramInformationModel.lowPrice?.let { low ->
					if (current< low) {
						ramInformationModel.lowPrice = current
						updateTodayPriceUI()
					}
				}
				calculatePricePercent()
				fragment.setCurrentPriceAndPercent(BigDecimal("$current").toPlainString(), ramInformationModel.pricePercent.orZero())
			}
			
		}
		
	}
	
	// 计算百分比
	private fun calculatePricePercent() {
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
	
}