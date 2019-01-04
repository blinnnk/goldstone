package io.goldstone.blockchain.kernel.network.eos.eosram

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.eos.EOSCPUUnit
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import java.math.BigInteger


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
		@WorkerThread hold: (priceInEOS: Double?, error: RequestError) -> Unit
	) {
		EOSAPI.getRAMMarket { data, error ->
			if (error.isNone() && data.isNotNull()) {
				val divisor = when (unit.value) {
					EOSUnit.KB.value -> 1024
					EOSUnit.MB.value -> 1024 * 1024
					EOSUnit.Byte.value -> 1
					else -> 0
				}
				val price = 1 * data.eosBalance / (1 + (data.ramBalance.toDouble() / divisor))
				hold(price, RequestError.None)
			} else hold(null, error)
		}
	}

	fun getRAMAmountByCoin(
		pair: Pair<Double, CoinSymbol>,
		unit: EOSUnit,
		@WorkerThread hold: (amount: Double?, error: RequestError) -> Unit
	) {
		EOSAPI.getRAMMarket { data, error ->
			if (data.isNotNull() && error.isNone()) {
				val ramTotal = data.ramCore
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

	fun getCPUPrice(
		account: EOSAccount,
		unit: EOSCPUUnit = EOSCPUUnit.MS,
		hold: (priceInEOS: Double?, error: GoldStoneError) -> Unit
	) {
		EOSAPI.getAccountInfo(
			account
		) { accountInfo, error ->
			if (accountInfo.isNotNull() && error.isNone()) {
				val priceInEOS =
					getCPUPriceByTime(accountInfo.cpuWeight, accountInfo.cpuLimit.max, unit)
				hold(priceInEOS, GoldStoneError.None)
			} else hold(null, error)
		}
	}

	fun getNETPrice(
		account: EOSAccount,
		unit: EOSUnit = EOSUnit.KB,
		hold: (priceInEOS: Double?, error: GoldStoneError) -> Unit
	) {
		EOSAPI.getAccountInfo(
			account
		) { accountInfo, error ->
			if (accountInfo.isNotNull() && error.isNone()) {
				val priceInEOS =
					getNETPriceByTime(accountInfo.netWeight, accountInfo.netLimit.max, unit)
				hold(priceInEOS, GoldStoneError.None)
			} else hold(null, error)
		}
	}

	/**
	 * @date: 2018/9/26
	 * @author LiHai
	 * [unit] 如果不是毫秒单位，一律返回纳秒单位价格
	 */
	private fun getCPUPriceByTime(
		myDelegateCPUWeight: BigInteger,
		myMaxCPULimit: BigInteger,
		unit: EOSCPUUnit
	): Double {
		var cpuPrice =
			myDelegateCPUWeight.toEOSCount() / (myMaxCPULimit * BigInteger.valueOf(3)).toDouble()
		if (unit == EOSCPUUnit.MS) {
			cpuPrice *= 1000
		}
		return cpuPrice
	}

	private fun getNETPriceByTime(
		myDelegateNETWeight: BigInteger,
		myMaxNETLimit: BigInteger,
		unit: EOSUnit
	): Double {
		var netPrice =
			myDelegateNETWeight.toEOSCount() / (myMaxNETLimit * BigInteger.valueOf(3)).toDouble()
		val multiplier = when (unit.value) {
			EOSUnit.Byte.value -> 1
			EOSUnit.KB.value -> 1024
			EOSUnit.MB.value -> 1024 * 1024
			else -> 1
		}
		netPrice *= multiplier
		return netPrice
	}
}