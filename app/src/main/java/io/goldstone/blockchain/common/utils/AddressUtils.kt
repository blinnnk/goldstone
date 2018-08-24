package io.goldstone.blockchain.common.utils

import io.goldstone.blockchain.common.value.Config

object AddressUtils {
	fun getCurrentBTCAddress(): String {
		return if (Config.isTestEnvironment()) Config.getCurrentBTCSeriesTestAddress()
		else Config.getCurrentBTCAddress()
	}

	fun getCurrentLTCAddress(): String {
		return if (Config.isTestEnvironment()) Config.getCurrentBTCSeriesTestAddress()
		else Config.getCurrentLTCAddress()
	}

	fun getCurrentBCHAddress(): String {
		return if (Config.isTestEnvironment()) Config.getCurrentBTCSeriesTestAddress()
		else Config.getCurrentBCHAddress()
	}
}