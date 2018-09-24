package io.goldstone.blockchain.kernel.network.eos.EOSRAM

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.network.eos.EOSAPI


/**
 * @author KaySaith
 * @date  2018/09/24
 */

object EOSResourceUtil {
	// `Price` 是 `RAM` 在指定单位下对应的 `EOS` 个数
	fun getRAMPrice(
		unit: EOSUnit,
		@UiThread hold: (price: Double?, error: RequestError) -> Unit
	) {
		EOSAPI.getRAMMarket(true) { data, error ->
			if (error.isNone() && !data.isNull()) {
				val divisor = when (unit.value) {
					EOSUnit.KB.value -> 1024
					EOSUnit.MB.value -> 1024 * 1024
					EOSUnit.Byte.value -> 1
					else -> 0
				}
				val price = 1 * data!!.eosBalance / (1 + (data.ramBalance.toDouble() / divisor))
				hold(price, RequestError.None)
			} else hold(null, error)
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
}