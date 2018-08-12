package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model

import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.value.WalletType
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
		getSubtitleByType(data),
		getSubtitleByType(data),
		balance,
		data.isWatchOnly,
		data.isUsing,
		type
	)

	companion object {
		fun getSubtitleByType(wallet: WalletTable): String {
			return when (WalletTable.getTargetWalletType(wallet)) {
				WalletType.LTCOnly -> wallet.currentLTCAddress
				WalletType.MultiChain -> WalletText.multiChain
				WalletType.ETHERCAndETCOnly -> wallet.currentETHAndERCAddress
				WalletType.BTCTestOnly -> wallet.btcTestAddresses
				WalletType.BTCOnly -> wallet.btcAddresses
			}
		}
	}
}