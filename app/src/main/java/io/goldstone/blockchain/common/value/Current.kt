package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.common.sharedpreference.SharedChain


/**
 * @author KaySaith
 * @date  2018/09/21
 */

object Current {
	fun chainIDs(): List<String> = listOf(
		SharedChain.getCurrentETH().id,
		SharedChain.getETCCurrent().id,
		SharedChain.getBTCCurrent().id,
		SharedChain.getLTCCurrent().id,
		SharedChain.getBCHCurrent().id,
		SharedChain.getEOSCurrent().id
	)
}