package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.common.sharedpreference.SharedChain


/**
 * @author KaySaith
 * @date  2018/09/21
 */

object Current {
	fun chainIDs(): List<String> = listOf(
		SharedChain.getCurrentETH().chainID.id,
		SharedChain.getETCCurrent().chainID.id,
		SharedChain.getBTCCurrent().chainID.id,
		SharedChain.getLTCCurrent().chainID.id,
		SharedChain.getBCHCurrent().chainID.id,
		SharedChain.getEOSCurrent().chainID.id
	)
}