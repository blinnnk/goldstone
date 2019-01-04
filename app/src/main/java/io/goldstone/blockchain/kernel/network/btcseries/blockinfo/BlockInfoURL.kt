package io.goldstone.blockchain.kernel.network.btcseries.blockinfo

import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.value.WebUrl


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