package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter

import com.blinnnk.extension.orZero
import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType


/**
 * @author KaySaith
 * @date  2018/09/18
 */
open class BaseTradingPresenter(
	override val fragment: BaseTradingFragment
) : BasePresenter<BaseTradingFragment>() {

	open fun gainConfirmEvent(callback: (Boolean) -> Unit) = callback(true)
	open fun refundOrSellConfirmEvent(callback: (Boolean) -> Unit) = callback(true)

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setUsageValue()
	}

	private fun setUsageValue() {
		EOSAccountTable.getAccountByName(Config.getCurrentEOSName()) { account ->
			when (fragment.tradingType) {
				TradingType.CPU -> {
					val cpuEOSValue = "${account?.cpuWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableCPU = account?.cpuLimit?.max.orZero() - account?.cpuLimit?.used.orZero()
					fragment.setProcessUsage(cpuEOSValue, availableCPU, account?.cpuLimit?.max.orZero())
				}
				TradingType.NET -> {
					val netEOSValue = "${account?.netWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableNET = account?.netLimit?.max.orZero() - account?.netLimit?.used.orZero()
					fragment.setProcessUsage(netEOSValue, availableNET, account?.netLimit?.max.orZero())
				}
				TradingType.RAM -> {
					val availableRAM = account?.ramQuota.orZero() - account?.ramUsed.orZero()
					fragment.setProcessUsage(CommonText.calculating, availableRAM, account?.ramQuota.orZero())
				}
			}
		}
	}

	fun updateLocalDataAndUI() {
		val currentAccountName = Config.getCurrentEOSName()
		EOSAPI.getAccountInfoByName(
			currentAccountName,
			{ LogUtil.error("updateLocalResourceData", it) }
		) { newData ->
			EOSAccountTable.getAccountByName(currentAccountName, false) { localData ->
				localData?.let { local ->
					GoldStoneDataBase.database.eosAccountDao().update(newData.apply { this.id = local.id })
					setUsageValue()
				}
			}
		}
	}
}