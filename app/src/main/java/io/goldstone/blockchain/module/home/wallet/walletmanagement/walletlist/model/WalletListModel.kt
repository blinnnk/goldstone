package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model

import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */
data class WalletListModel(
        var id: Int = 0,
        var addressName: String = "",
        var address: String = "",
        var subtitle: String = "",
        var count: Double = 0.0,
        var avatar: Int = 0,
        var isWatchOnly: Boolean = false,
        var isUsing: Boolean = false
) {

  constructor(data: WalletTable, balance: Double) : this(
          data.id,
          data.name,
          showSubtitleByType(data, true),
          showSubtitleByType(data, false),
          balance,
          //修改
          data.id,
          data.isWatchOnly,
          data.isUsing
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