package io.goldstone.blockchain.common.utils

import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue

object AddressUtils {
	fun getCurrentBTCAddress(): String {
		return if (SharedValue.isTestEnvironment()) SharedAddress.getCurrentBTCSeriesTest()
		else SharedAddress.getCurrentBTC()
	}

	fun getCurrentLTCAddress(): String {
		return if (SharedValue.isTestEnvironment()) SharedAddress.getCurrentBTCSeriesTest()
		else SharedAddress.getCurrentLTC()
	}

	fun getCurrentBCHAddress(): String {
		return if (SharedValue.isTestEnvironment()) SharedAddress.getCurrentBTCSeriesTest()
		else SharedAddress.getCurrentBCH()
	}
}