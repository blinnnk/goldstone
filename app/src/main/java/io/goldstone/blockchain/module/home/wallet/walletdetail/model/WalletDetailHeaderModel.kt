package io.goldstone.blockchain.module.home.wallet.walletdetail.model

/**
 * @date 2018/5/12 11:43 PM
 * @author KaySaith
 */
data class WalletDetailHeaderModel(
	val avatar: String?,
	val name: String,
	val address: String,
	val totalBalance: String
)