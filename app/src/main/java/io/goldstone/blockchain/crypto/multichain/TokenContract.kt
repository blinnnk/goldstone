package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.value.Config


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class TokenContract(val contract: String?) {
	fun isEOS(): Boolean {
		return contract.equals(eosContract, true)
	}

	fun isETH(): Boolean {
		return contract.equals(ethContract, true)
	}

	fun isBTC(): Boolean {
		return contract.equals(btcContract, true)
	}

	fun isLTC(): Boolean {
		return contract.equals(ltcContract, true)
	}

	fun isBCH(): Boolean {
		return contract.equals(bchContract, true)
	}

	fun isETC(): Boolean {
		return contract.equals(etcContract, true)
	}

	fun getCurrentChainID(): String {
		return when {
			contract.equals(etcContract, true) -> Config.getETCCurrentChain()
			contract.equals(btcContract, true) -> Config.getBTCCurrentChain()
			contract.equals(ltcContract, true) -> Config.getLTCCurrentChain()
			contract.equals(bchContract, true) -> Config.getBCHCurrentChain()
			contract.equals(eosContract, true) -> Config.getEOSCurrentChain()
			else -> Config.getCurrentChain() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		}
	}

	companion object {
		// GoldStone 业务约定的值
		const val ethContract = "0x60"
		const val etcContract = "0x61"
		const val btcContract = "0x0"
		const val ltcContract = "0x2"
		const val bchContract = "0x145"
		const val eosContract = "0x194"
	}
}