package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.view.RAMStatisticsFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.runOnUiThread
import java.math.BigDecimal

/**
 * @date: 2018/11/6.
 * @author: yanglihai
 * @description:
 */
class RAMStatisticsPresenter(override val fragment: RAMStatisticsFragment)
	: BasePresenter<RAMStatisticsFragment>() {
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getGlobalRAMData()
		getChainRAMData()
	}
	
	private fun getGlobalRAMData() {
		EOSAPI.getGlobalInformation { globalModel, requestError ->
			if (globalModel != null && requestError.isNone()) {
				if (!globalModel.maxRamSize.isNull() && !globalModel.totalRamBytesReserved.isNull()) {
					val gbDivider = Math.pow(1024.toDouble(), 3.toDouble())
					var maxAmount = BigDecimal(globalModel.maxRamSize)
					var availableAmount = maxAmount.subtract(BigDecimal(globalModel.totalRamBytesReserved))
					val percent = availableAmount.divide(maxAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal("100"))
					maxAmount = maxAmount.divide(BigDecimal(gbDivider), 2 , BigDecimal.ROUND_HALF_UP)
					availableAmount = availableAmount.divide(BigDecimal(gbDivider), 2 , BigDecimal.ROUND_HALF_UP)
					GoldStoneAPI.context.runOnUiThread {
						fragment.setGlobalRAMData(availableAmount.toFloat(), maxAmount.toFloat(), percent.toFloat())
					}
				}
			} else {
				GoldStoneAPI.context.runOnUiThread {
					fragment.context?.alert(requestError.message)
				}
			}
		}
	}
	
	private fun getChainRAMData() {
		EOSAPI.getRAMMarket { data, error ->
			if (data != null && error.isNone()) {
				val divider = BigDecimal(Math.pow(1024.toDouble(), 3.toDouble()))
				val ramBalance = BigDecimal(data.ramBalance).divide(divider, 2, BigDecimal.ROUND_HALF_UP).toPlainString()
				val ramOfEOS = data.eosBalance.formatCount(4)
				GoldStoneAPI.context.runOnUiThread {
					fragment.setChainRAMData(ramBalance, ramOfEOS)
				}
			} else {
				GoldStoneAPI.context.runOnUiThread {
					fragment.context?.alert(error.message)
				}
			}
		}
	}
	
	
	
}