package io.goldstone.blockchain.kernel.network.eos.eosram

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import org.jetbrains.anko.runOnUiThread


/**
 * @author LiHai
 * @date  2018/09/21
 * @ReWriter KaySaith
 * @date 2018/09/24
 */

object EOSResourceUtil {
	// `Price` 是 `RAM` 在指定单位下对应的 `EOS` 个数
	fun getRAMPrice(
		unit: EOSUnit,
		isMainThread: Boolean = true,
		hold: (priceInEOS: Double?, error: RequestError) -> Unit
	) {
		EOSAPI.getRAMMarket(isMainThread) { data, error ->
			if (error.isNone() && !data.isNull()) {
				val divisor = when (unit.value) {
					EOSUnit.KB.value -> 1024
					EOSUnit.MB.value -> 1024 * 1024
					EOSUnit.Byte.value -> 1
					else -> 0
				}
				val price = 1 * data!!.eosBalance / (1 + (data.ramBalance.toDouble() / divisor))
				if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(price, RequestError.None) }
				else hold(price, RequestError.None)
			} else {
				if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(null, error) }
				else hold(null, error)
			}
		}
	}

	fun getRAMAmountByCoin(
		pair: Pair<Double, CoinSymbol>,
		unit: EOSUnit,
		@UiThread hold: (amount: Double?, error: RequestError) -> Unit
	) {
		EOSAPI.getRAMMarket(true) { data, error ->
			if (!data.isNull() && error.isNone()) {
				val ramTotal = data!!.ramCore
				val eosBalance = data.eosBalance + pair.first
				val mi = data.eosWeight / 1000.0
				val ramCoreInEOS = -ramTotal * (1 - Math.pow(1 + (pair.first / eosBalance), mi))

				val ramCoreTotal = data.ramCore - pair.first
				val ramBalance = data.ramBalance.toDouble()
				val constF = 1000 / data.ramWeight

				var ramAmount = ramBalance * (Math.pow(1 + (ramCoreInEOS / ramCoreTotal), constF) - 1)

				val divisor = when (unit.value) {
					EOSUnit.Byte.value -> 1
					EOSUnit.KB.value -> 1024
					EOSUnit.MB.value -> 1024 * 1024
					else -> 1
				}
				ramAmount /= divisor
				hold(ramAmount, error)
			} else hold(null, error)
		}
	}
	
	/**
	 * @date: 2018/9/26
	 * [unit] 如果不是毫秒单位，一律返回纳秒单位价格
	 */
	fun getCPUPriceByTime(
		totalResourcesCPUWeight: Double,
		cpuLimitMax: Double,
		unit: EOSUnit
	): Double {
		var cpuPrice = totalResourcesCPUWeight / (cpuLimitMax * 3)
		if (unit == EOSUnit.MS) {
			cpuPrice *= 1000
		}
		return cpuPrice
	}
	
	fun getNETPriceByUnit(
		totalResourcesNETWeight: Double,
		netLimitMax: Double,
		unit: EOSUnit
	): Double {
		var netPrice = totalResourcesNETWeight / (netLimitMax * 3)
		val multiplier = when(unit.value) {
			EOSUnit.Byte.value-> 1
			EOSUnit.KB.value -> 1024
			EOSUnit.MB.value -> 1024 * 1024
			else -> 1
		}
		netPrice *= multiplier
		return netPrice
	}
	
	
}














