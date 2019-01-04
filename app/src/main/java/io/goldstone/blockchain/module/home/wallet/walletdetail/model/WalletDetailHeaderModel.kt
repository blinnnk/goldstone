package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import java.io.Serializable

/**
 * @date 2018/5/12 11:43 PM
 * @author KaySaith
 */
data class WalletDetailHeaderModel(
	val walletID: Int,
	val avatar: String?,
	val name: String,
	val address: String,
	val totalBalance: String
) : Serializable