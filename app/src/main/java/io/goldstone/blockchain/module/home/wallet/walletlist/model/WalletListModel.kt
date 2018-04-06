package io.goldstone.blockchain.module.home.wallet.walletlist.model

import io.goldstone.blockchain.R

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */

data class WalletListModel(
  val addressName: String = "",
  val address: String = "",
  val count: Double = 0.0,
  val avatar: Int = R.drawable.avatar,
  val isWatchOnly: Boolean = false
)