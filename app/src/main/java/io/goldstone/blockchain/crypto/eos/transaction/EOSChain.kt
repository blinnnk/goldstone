package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.ChainID

/**
 * @author KaySaith
 * @date 2018/09/03
 */

enum class EOSChain(val id: String) {
	Main(ChainID.eosMain),
	Test(ChainID.eosTest);
	companion object {
		fun getCurrent(): EOSChain {
			return if (Config.getEOSCurrentChain().isEOSMain()) Main else Test
		}
	}
}