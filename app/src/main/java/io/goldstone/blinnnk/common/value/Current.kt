package io.goldstone.blinnnk.common.value

import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.crypto.multichain.ChainID


/**
 * @author KaySaith
 * @date  2018/09/21
 */

object Current {
	private fun allChainID(): List<ChainID> = listOf(
		SharedChain.getCurrentETH().chainID,
		SharedChain.getETCCurrent().chainID,
		SharedChain.getBTCCurrent().chainID,
		SharedChain.getLTCCurrent().chainID,
		SharedChain.getBCHCurrent().chainID,
		SharedChain.getEOSCurrent().chainID
	)

	fun chainIDs(): List<String> = allChainID().map { it.id }

	fun supportChainIDs(): List<ChainID> {
		val currentWallet = SharedWallet.getCurrentWalletType()
		return when {
			currentWallet.isETHSeries() -> listOf(SharedChain.getCurrentETH().chainID)
			currentWallet.isEOSSeries() -> listOf(SharedChain.getEOSCurrent().chainID)
			currentWallet.isBIP44() || currentWallet.isMultiChain() -> Current.allChainID()
			else -> listOf()
		}
	}
}