package io.goldstone.blinnnk.common.utils

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable

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

	@WorkerThread
	fun hasExistAddress(
		allAddresses: List<String>,
		excludeWalletID: Int? = null,
		hold: (hasExistAddress: Boolean) -> Unit
	) {
		val allWallets = WalletTable.dao.getAllWallets()
		val targetWallets =
			if (excludeWalletID.isNotNull()) allWallets.filterNot { it.id == excludeWalletID } else allWallets
		if (targetWallets.isEmpty()) hold(false)
		else targetWallets.map {
			it.ethAddresses + it.btcAddresses + it.ltcAddresses + it.etcAddresses + it.btcSeriesTestAddresses + it.eosAddresses + it.bchAddresses
		}.flatten().asSequence().map {
			it.address
		}.any {
			!allAddresses.find { new -> new.equals(it, true) }.isNull()
		}.let(hold)
	}
}