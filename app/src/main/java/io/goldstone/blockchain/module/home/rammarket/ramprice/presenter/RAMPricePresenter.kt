package io.goldstone.blockchain.module.home.rammarket.ramprice.presenter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isTrue
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.github.mikephil.charting.data.CandleEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.rammarket.model.RAMInformationModel
import io.goldstone.blockchain.module.home.rammarket.ramprice.view.RAMPriceDetailFragment
import io.goldstone.blockchain.module.home.rammarket.ramprice.view.RAMPriceDetailView
import io.goldstone.blockchain.module.home.rammarket.view.EOSRAMPriceCandleChart
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: 头部的price展示presenter
 */
class RAMPricePresenter(override val fragment: RAMPriceDetailFragment)
 : BasePresenter<RAMPriceDetailFragment>() {
	
	private val ramInformationKey = "eosRAMInfo"
	private val ramCandleDataKey = "eosRAMCandle"
	private lateinit var ramInformationModel: RAMInformationModel
	private var candleDataMap: HashMap<String, ArrayList<CandleChartModel>> = hashMapOf()
	private var period: String = EOSRAMChartType.Hour.info
	private var dateType: Int = DateUtils.FORMAT_SHOW_TIME
	private val ramPriceSocket by lazy {
		object : GoldStoneWebSocket("{\"t\": \"unsub_eos_ram_service\"}") {
			override fun onOpened() {
				sendMessage("{\"t\":\"sub_eos_ram_service\"}")
			}
			
			override fun getServerBack(content: JSONObject, isDisconnected: Boolean) {
				if (isDisconnected) {
					runSocket()
					return
				}
				val price = content.getString("price")
				val current = BigDecimal(price.isEmptyThen("0")).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
				ramInformationModel.currentPrice = current
				GoldStoneAPI.context.runOnUiThread {
					updateCurrentPriceUI()
				}
			}
		}
	}
	
	override fun onFragmentResume() {
		super.onFragmentResume()
		updateHeaderData()
	}
	
	override fun onFragmentPause() {
		super.onFragmentPause()
		ramPriceSocket.isSocketConnected() isTrue {
			ramPriceSocket.closeSocket()
		}
	}
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		fragment.context?.apply {
			ramInformationModel = try {
				val jsonData = getStringFromSharedPreferences(ramInformationKey)
				Gson().fromJson(jsonData, RAMInformationModel::class.java)
			} catch (error: Exception) {
				RAMInformationModel(
					null,
					null,
					null,
					null,
					null,
					null,
					null
				)
			}
			
			candleDataMap = try {
				val jsonData = getStringFromSharedPreferences(ramCandleDataKey)
				val type = object : TypeToken<HashMap<String, ArrayList<CandleChartModel>>>() {}.type
				Gson().fromJson(jsonData, type)
			} catch (error: Exception) {
				hashMapOf()
			}
		}
	}
	
	override fun onFragmentDestroy() {
		fragment.context?.apply {
			saveDataToSharedPreferences(ramInformationKey, Gson().toJson(ramInformationModel))
			saveDataToSharedPreferences(ramCandleDataKey, Gson().toJson(candleDataMap))
		}
		super.onFragmentDestroy()
	}
	
	private fun updateHeaderData() {
		updateTodayPrice()
		ramPriceSocket.isSocketConnected() isFalse {
			ramPriceSocket.runSocket()
		}
	}
	
	fun updateRAMCandleData(
		period: String,
		dateType: Int
	) {
		this@RAMPricePresenter.period = period
		this@RAMPricePresenter.dateType = dateType
		// 请求的数据条目数量
		val size = DataValue.candleChartCount
		GoldStoneAPI.getEOSRAMPriceTendcyCandle(
			period,
			size,
			{
				// Show the error exception to user
				fragment.context.alert(it.toString().showAfterColonContent())
				fragment.candleChart.updateCandleChartUI()
			}
		) {
			// 更新 `UI` 界面
			candleDataMap[period] = it
			GoldStoneAPI.context.runOnUiThread {
				fragment.candleChart.updateCandleChartUI()
			}
		}
	}
	
	private fun EOSRAMPriceCandleChart.updateCandleChartUI() {
		candleDataMap[period]?.apply {
			resetData(dateType, this.mapIndexed { index, entry ->
				CandleEntry(
					index.toFloat(),
					entry.high.toFloat(),
					entry.low.toFloat(),
					entry.open.toFloat(),
					entry.close.toFloat(),
					entry.time)
			})
		}
		
		
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateTodayPrice() {
		GoldStoneAPI.getEOSRAMPriceToday { model, error ->
			if (error.isNone()) {
				model?.let {
					val open = BigDecimal(it.open).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
					val high = BigDecimal(it.high).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
					val low = BigDecimal(it.low).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
					ramInformationModel.openPrice = open
					ramInformationModel.HighPrice = high
					ramInformationModel.lowPrice = low
				}
			}
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramInformationHeader.updateTodayPriceUI()
				updatePricePercentUI()
			}
			
		}
	}
	
	private fun RAMPriceDetailView.updateTodayPriceUI() {
		startPrice.text = EOSRAMText.openPrice(ramInformationModel.openPrice ?: "")
		highPrice.text = EOSRAMText.highPrice(ramInformationModel.HighPrice ?: "")
		lowPrice.text = EOSRAMText.lowPrice(ramInformationModel.lowPrice ?: "")
	}
	
	
	private fun updateCurrentPriceUI() {
		fragment.ramInformationHeader.currentPrice.text = ramInformationModel.currentPrice
		ramInformationModel.currentPrice?.let { current ->
			ramInformationModel.HighPrice?.let { high ->
				if (current.toFloat() > high.toFloat()) {
					ramInformationModel.HighPrice = current
					fragment.ramInformationHeader.highPrice.text = EOSRAMText.highPrice(current)
				}
			}
			ramInformationModel.lowPrice?.let { low ->
				if (current.toFloat() < low.toFloat()) {
					ramInformationModel.lowPrice = current
					fragment.ramInformationHeader.lowPrice.text = EOSRAMText.lowPrice(current)
				}
			}
		}
		updatePricePercentUI()
	}
	
	@SuppressLint("SetTextI18n")
	private fun updatePricePercentUI() {
		ramInformationModel.openPrice?.let { open ->
			ramInformationModel.currentPrice?.let { current ->
				var trend = (current.toDouble() - open.toDouble()) / open.toDouble()
				trend *= 100.toDouble()
				val trendBigDecimal = BigDecimal(trend).divide(BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP)
				fragment.ramInformationHeader.apply {
					if (trend > 0) {
						trendcyPercent.text = "+$trendBigDecimal%"
						trendcyPercent.textColor = Spectrum.green
					} else {
						trendcyPercent.text = "-$trendBigDecimal%"
						trendcyPercent.textColor = Spectrum.red
					}
				}
			}
		}
	}
	
	
	
}