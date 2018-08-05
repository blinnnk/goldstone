package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model

import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 * @rewriteDate 26/07/2018 3:30 PM
 * @rewriter wcx
 * @description 修改avatar通过id获取
 */
data class WalletListModel(
	var id: Int = 0,
	var addressName: String = "",
	var address: String = "",
	var subtitle: String = "",
	var balance: Double = 0.0,
	var isWatchOnly: Boolean = false,
	var isUsing: Boolean = false,
	var type: String = ""
) {
	
	constructor(data: WalletTable, balance: Double, type: String) : this(
		data.id,
		data.name,
		showSubtitleByType(data, true),
		showSubtitleByType(data, false),
		balance,
		data.isWatchOnly,
		data.isUsing,
		type
	)
	
	companion object {
		fun showSubtitleByType(wallet: WalletTable, isAddress: Boolean): String {
			return if (wallet.currentETHAndERCAddress.isEmpty()) {
				if (wallet.currentBTCTestAddress.isEmpty()) {
					wallet.currentBTCAddress
				} else {
					wallet.currentBTCTestAddress
				}
			} else if (wallet.currentBTCAddress.isEmpty()) {
				wallet.currentETHAndERCAddress
			} else {
				if (isAddress) {
					wallet.currentETHAndERCAddress
				} else {
					WalletText.multiChainWallet
				}
			}
		}
	}
}