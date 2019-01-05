package io.goldstone.blinnnk.kernel.network.btcseries.blockinfo

import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.value.WebUrl


/**
 * @author KaySaith
 * @date  2018/11/09
 */

object BlockInfoURL {
	val blockInfoURL: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.backupBtcTest
		else WebUrl.backUpBtcMain
	}

	val getBalance: (address: String) -> String = {
		"${blockInfoURL()}/balance?active=$it"
	}

	val getUnspents: (address: String) -> String = {
		"${blockInfoURL()}/unspent?active=$it"
	}
}