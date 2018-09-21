package io.goldstone.blockchain.common.value


/**
 * @author KaySaith
 * @date  2018/09/21
 */

object Current {
	fun chianIDs(): List<String> = listOf(
		Config.getCurrentChain().id,
		Config.getETCCurrentChain().id,
		Config.getBTCCurrentChain().id,
		Config.getLTCCurrentChain().id,
		Config.getBCHCurrentChain().id,
		Config.getEOSCurrentChain().id
	)
}