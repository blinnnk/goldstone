package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.blinnnk.extension.isNull
import com.blinnnk.util.*
import com.github.mikephil.charting.data.CandleEntry
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.gson.Gson
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSRAMUtil
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.RAMTradeRefreshEvent
import io.goldstone.blockchain.module.home.quotation.tradermemory.RefreshReceiver
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.RAMInformationModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.textColor
import org.json.JSONException
import java.math.BigDecimal

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EOSRAMPriceTrendPresenter(override val fragment: EOSRAMPriceTrendFragment)
	: BasePresenter<EOSRAMPriceTrendFragment>(), RefreshReceiver {
	
	private val sharedPreferencesKey = "eosRAMInfo"
	
	private lateinit var ramInformationModel: RAMInformationModel
	
	private var period: String = EOSRAMChartType.Hour.info
	private var dateType: Int = DateUtils.FORMAT_SHOW_TIME
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		updateHeaderData()
	}
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		fragment.context?.apply {
			ramInformationModel = try {
				val jsonData = getStringFromSharedPreferences(sharedPreferencesKey)
				Gson().fromJson(jsonData, RAMInformationModel::class.java)
			} catch (error: Exception) {
				RAMInformationModel(null, null, null, null, null, null, null)
			}
		}
		RAMTradeRefreshEvent.register(this)
	}
	
	override fun onFragmentDestroy() {
		RAMTradeRefreshEvent.unRegister(this)
		fragment.context?.apply {
			saveDataToSharedPreferences(sharedPreferencesKey, Gson().toJson(ramInformationModel).toString())
		}
		super.onFragmentDestroy()
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateHeaderData() {
		updateTodayPrice()
		updateCurrentPrice()
		updateRAMAmount()
	}
	
	
	fun updateRAMCandleData(
		period: String,
		dateType: Int
	) {
		this@EOSRAMPriceTrendPresenter.period = period
		this@EOSRAMPriceTrendPresenter.dateType = dateType
		// 请求的数据条目数量
		val size = DataValue.candleChartCount
		GoldStoneAPI.getEOSRAMPriceTendcyCandle(
			period,
			size,
			{
				// Show the error exception to user
				fragment.context.alert(it.toString().showAfterColonContent())
				fragment.candleChart.updateCandleChartUI(arrayListOf(), dateType)
			}
		) {
			// 把数据更新到数据库
			// 更新 `UI` 界面
			GoldStoneAPI.context.runOnUiThread {
				fragment.candleChart.updateCandleChartUI(it, dateType)
			}
		}
	}
	
	private fun EOSRAMPriceTrendCandleChart.updateCandleChartUI(
		data: ArrayList<CandleChartModel>,
		dateType:Int
	) {
		resetData(dateType, data.mapIndexed { index, entry ->
			CandleEntry(
				index.toFloat(),
				entry.high.toFloat(),
				entry.low.toFloat(),
				entry.open.toFloat(),
				entry.close.toFloat(),
				entry.time)
			}
		)
		
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateTodayPrice() {
		GoldStoneAPI.getEOSRAMPriceToday( {
			// Show the error exception to user
			fragment.context.alert(it.toString().showAfterColonContent())
			fragment.ramInformationHeader.updateTodayPriceUI()
			fragment.ramInformationHeader.updatePricePercentUI()
		}) {
			GoldStoneAPI.context.runOnUiThread {
				val open = BigDecimal(it.open).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
				val high = BigDecimal(it.high).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
				val low = BigDecimal(it.low).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
				ramInformationModel.openPrice = open
				ramInformationModel.HighPrice = high
				ramInformationModel.lowPrice = low
				fragment.ramInformationHeader.updateTodayPriceUI()
				fragment.ramInformationHeader.updatePricePercentUI()
			}
			
		}
	}
	
	private fun RAMInformationHeader.updateTodayPriceUI() {
		startPrice.text = EOSRAMText.openPrice(ramInformationModel.openPrice ?: "")
		highPrice.text = EOSRAMText.highPrice(ramInformationModel.HighPrice ?: "")
		lowPrice.text = EOSRAMText.lowPrice(ramInformationModel.lowPrice ?: "")
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateCurrentPrice() {
		EOSRAMUtil.getRAMPrice(EOSUnit.KB) {
			GoldStoneAPI.context.runOnUiThread {
				if (it != 0.toDouble()) {
					val current = BigDecimal(it.toString()).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
					ramInformationModel.currentPrice = current
					fragment.ramInformationHeader.updateCurrentPriceUI()
				}
			}
		}
	}
	
	private fun RAMInformationHeader.updateCurrentPriceUI() {
		currentPrice.text = ramInformationModel.currentPrice
		updatePricePercentUI()
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateRAMAmount() {
		EOSAPI.getGlobalInformation( {
			fragment.context.alert(it.toString().showAfterColonContent())
			fragment.ramInformationHeader.updateRAMAmountUI()
		}) {
			GoldStoneAPI.context.runOnUiThread {
				if (!it.maxRamSize.isNull() && !it.totalRamBytesReserved.isNull()) {
					val gbDivisior = Math.pow(1024.toDouble(), 3.toDouble())
					var maxAmount = BigDecimal(it.maxRamSize)
					var reservedAmount = BigDecimal(it.totalRamBytesReserved)
					val percent = reservedAmount.divide(maxAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal("100"))
					maxAmount = maxAmount.divide(BigDecimal(gbDivisior), 2 ,BigDecimal.ROUND_HALF_UP)
					reservedAmount = reservedAmount.divide(BigDecimal(gbDivisior), 2 ,BigDecimal.ROUND_HALF_UP)
					ramInformationModel.ramAmountPercent = percent.stripTrailingZeros().toPlainString()
					ramInformationModel.occupyAmount = reservedAmount.toString()
					ramInformationModel.maxAmount = maxAmount.toString()
					
					fragment.ramInformationHeader.updateRAMAmountUI()
					
				}
			}
		}
	}
	
	private fun RAMInformationHeader.updateRAMAmountUI() {
		ramTotalReserved.text = EOSRAMText.ramAccupyAmount(ramInformationModel.occupyAmount ?: "")
		ramMax.text = EOSRAMText.ramTotalAmount(ramInformationModel.maxAmount ?: "")
		ramInformationModel.ramAmountPercent?.apply {
			ramPercent.text = "${this}%"
			percentProgressBar.progress = this.toFloat().toInt()
		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun RAMInformationHeader.updatePricePercentUI() {
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
	
	override fun onReceive(any: Any) {
		if (NetworkUtil.hasNetworkWithAlert(fragment.context)) {
			when(any.toString()) {
				"" -> updateHeaderData()
				"candle" -> updateRAMCandleData(period, dateType)
			}
		}
	}
	
	
	
}











