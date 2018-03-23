package io.goldstone.blockchain.module.home.wallet.wallet.model

/**
 * @date 23/03/2018 11:57 PM
 * @author KaySaith
 */

data class WalletDetailCellModel(
  var src: Int = 0,
  var tokenSymbol: String = "",
  var tokenName: String = "",
  var count: Double = 0.0,
  var money: Double = 0.0,
  var moneyText: String = "≈ $money USD"
)