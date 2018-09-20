package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.presenter

import com.blinnnk.extension.orZero
import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.view.CPUTradingFragment


/**
 * @author KaySaith
 * @date  2018/09/18
 */
class CPUTradingPresenter(
	override val fragment: CPUTradingFragment
) : BasePresenter<CPUTradingFragment>() {

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setCPUUsageValue()
	}

	private fun setCPUUsageValue() {
		EOSAccountTable.getAccountByName(Config.getCurrentEOSName()) { account ->
			val cpuEOSValue = "${account?.cpuWeight?.toEOSCount()}" suffix CoinSymbol.eos
			val availableCPU = account?.cpuLimit?.max.orZero() - account?.cpuLimit?.used.orZero()
			fragment.setCPUUsage(cpuEOSValue, availableCPU, account?.cpuLimit?.max.orZero())
			val netEOSValue = "${account?.netWeight?.toEOSCount()}" suffix CoinSymbol.eos
			val availableNET = account?.netLimit?.max.orZero() - account?.netLimit?.used.orZero()
			fragment.setNETUsage(netEOSValue, availableNET, account?.netLimit?.max.orZero())
		}
	}
}