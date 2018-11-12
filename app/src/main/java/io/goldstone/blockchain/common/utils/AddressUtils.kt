package io.goldstone.blockchain.common.utils

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.jetbrains.anko.doAsync

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
		val allWallets = GoldStoneDataBase.database.walletDao().getAllWallets()
		val targetWallets =
			if (excludeWalletID != null) allWallets.filterNot { it.id == excludeWalletID } else allWallets
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