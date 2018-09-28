package io.goldstone.blockchain.kernel.network.eos

import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.CoinSymbol

/**
 * @date: 2018/9/19.
 * @author: yanglihai
 * @description: eos ram 之间的转换工具
 */
object EOSRAMUtil {
	
	fun getRAMPrice(
		unit: EOSUnit,
		hold:(Double) -> Unit
	) {
		EOSAPI.getRAMBalance({
			LogUtil.error("getRAMPrice", it)
			hold(0.toDouble())
		}) { model ->
			val divisor = when(unit.value) {
				EOSUnit.Byte.value -> 1
				EOSUnit.KB.value -> 1024
				EOSUnit.MB.value -> 1024 * 1024
				else -> 1
			}
			val price = 1 * model.quote.balance.toDouble() / (1 + (model.base.balance.toDouble() / divisor))
			hold(price)
		}
		
	}
	
	
	fun getRAMAmountByCoin(
		pair: Pair<Double, CoinSymbol>,
		unit: EOSUnit,
		hold:(Double) -> Unit
	) {
		EOSAPI.getRAMBalance({
			LogUtil.error("getRAMAmountByCoin", it)
		}) { model ->
			val ramTotal = model.supply.toDouble()
			val eosBalance = model.quote.balance.toDouble() + pair.first
			val mi = model.quote.weight.toDouble() / 1000.toDouble()
			val ramcoreOfEOS = -ramTotal * (1 - Math.pow(1 + (pair.first / eosBalance), mi))
			
			val ramcoreTotal = model.supply.toDouble() - pair.first
			val RAMBalance = model.base.balance.toDouble()
			val constF = 1000 / model.base.weight.toDouble()
			
			var ramAmount = RAMBalance * (Math.pow(1 + (ramcoreOfEOS / ramcoreTotal), constF) - 1)
			
			val divisor = when(unit.value) {
				EOSUnit.Byte.value -> 1
				EOSUnit.KB.value -> 1024
				EOSUnit.MB.value -> 1024 * 1024
				else -> 1
			}
			
			ramAmount /= divisor
			
			hold(ramAmount)
		}
	}
	
	
}