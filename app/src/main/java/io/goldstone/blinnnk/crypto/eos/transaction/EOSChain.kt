package io.goldstone.blinnnk.crypto.eos.transaction

import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.multichain.ChainID

/**
 * @author KaySaith
 * @date 2018/09/03
 */

enum class EOSChain(val id: String) {
	Main(ChainID.eosMain),
	Kylin(ChainID.eosKylin),
	Jungle(ChainID.eosJungle);

	companion object {
		fun getCurrent(): EOSChain {
			val chainID = SharedChain.getEOSCurrent().chainID
			return when {
				chainID.isEOSMain() -> Main
				chainID.isEOSKylin() -> Kylin
				else -> Jungle
			}
		}
	}
}