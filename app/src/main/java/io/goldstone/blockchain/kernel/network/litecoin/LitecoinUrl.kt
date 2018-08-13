package io.goldstone.blockchain.kernel.network.litecoin

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 2018/8/13 12:08 PM
 * @author KaySaith
 */

object LitecoinUrl {
	private var currentUrl: (api: String) -> String = {
		if (Config.isTestEnvironment()) WebUrl.ltcTest(it)
		else WebUrl.ltcMain(it)
	}
	val getBalance: (address: String) -> String = { address ->
		"${currentUrl("get_address_balance")}/$address"
	}
}