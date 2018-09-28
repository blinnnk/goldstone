package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter

import android.annotation.SuppressLint
import com.blinnnk.extension.isNull
import com.github.mikephil.charting.data.CandleEntry
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
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EOSRAMPriceTrendCandleChart
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EOSRAMPriceTrendFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.textColor
import java.math.BigDecimal

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EOSRAMPriceTrendPresenter(override val fragment: EOSRAMPriceTrendFragment)
	: BasePresenter<EOSRAMPriceTrendFragment>(), RefreshReceiver {
	
	private var todayOpenPrice: String? = null
	private var todayCurrentPrice: String? = null
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		updateHeaderData()
	}
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		RAMTradeRefreshEvent.register(this)
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		RAMTradeRefreshEvent.unRegister(this)
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateHeaderData() {
		LogUtil.debug("updateheadData", "update")
		getTodayPrice()
		EOSRAMUtil.getRAMPrice(EOSUnit.KB) {
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramInformationHeader.apply {
					currentPrice.text = BigDecimal(it.toString()).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString()
					todayCurrentPrice = it.toString()
					setTrendPercent()
				}
			}
		}
		
		EOSAPI.getGlobalInformation( {
			fragment.context.alert(it.toString().showAfterColonContent())
		}) {
			GoldStoneAPI.context.runOnUiThread {
				if (!it.maxRamSize.isNull() && !it.totalRamBytesReserved.isNull()) {
					val gbDivisior = Math.pow(1024.toDouble(), 3.toDouble())
					var maxAmount = BigDecimal(it.maxRamSize)
					var reservedAmount = BigDecimal(it.totalRamBytesReserved)
					val percent = reservedAmount.divide(maxAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal("100"))
					maxAmount = maxAmount.divide(BigDecimal(gbDivisior), 2 ,BigDecimal.ROUND_HALF_UP)
					reservedAmount = reservedAmount.divide(BigDecimal(gbDivisior), 2 ,BigDecimal.ROUND_HALF_UP)
					fragment.ramInformationHeader.apply {
						ramTotalReserved.text = EOSRAMText.ramAccupyAmount(reservedAmount.toString())
						ramMax.text = EOSRAMText.ramTotalAmount(maxAmount.toString())
						ramPercent.text = "${percent.stripTrailingZeros().toPlainString()}%"
						percentProgressBar.progress = percent.toInt()
					}
				}
			}
		}
	}
	
	
	fun updateEosRamPriceTrend(
		period: String,
		dateType: Int
	) {
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
	private fun getTodayPrice() {
		GoldStoneAPI.getEOSRAMPriceToday( {
			// Show the error exception to user
			fragment.context.alert(it.toString().showAfterColonContent())
		}) {
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramInformationHeader.apply {
					this@EOSRAMPriceTrendPresenter.todayOpenPrice = it.open
					startPrice.text = EOSRAMText.openPrice(BigDecimal(it.open).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString())
					highPrice.text = EOSRAMText.highPrice(BigDecimal(it.high).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString())
					lowPrice.text = EOSRAMText.lowPrice(BigDecimal(it.low).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP).toString())
					setTrendPercent()
				}
			}
			
		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun setTrendPercent() {
		todayOpenPrice?.let { open ->
			todayCurrentPrice?.let { current ->
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
		LogUtil.debug("refreshing", "刷新收到了")
	}
	
	
	
}











