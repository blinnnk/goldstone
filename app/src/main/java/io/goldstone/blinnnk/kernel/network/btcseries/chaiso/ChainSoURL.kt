package io.goldstone.blinnnk.kernel.network.btcseries.chaiso

import io.goldstone.blinnnk.common.sharedpreference.SharedValue


/**
 * @author KaySaith
 * @date  2018/11/09
 */
object ChainSoURL {
	val getBalanceFromChainSo: (address: String) -> String = { address ->
		val param = if (SharedValue.isTestEnvironment()) "LTCTest" else "LTC"
		"https://chain.so/api/v2/get_address_balance/$param/$address"
	}
}