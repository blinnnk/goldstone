package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import com.blinnnk.extension.forEachOrEnd
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 23/03/2018 11:57 PM
 * @author KaySaith
 */

data class WalletDetailCellModel(
  var iconUrl: String = "",
  var symbol: String = "",
  var name: String = "",
  var decimal: Double = 0.0,
  var count: Double = 0.0,
  var price: Double = 0.0,
  var currency: Double = 0.0
) {

  constructor(data: DefaultTokenTable, balance: Double) : this(
    data.iconUrl,
    data.symbol,
    data.name,
    data.decimals,
    CryptoUtils.formatDouble(balance / Math.pow(10.0, data.decimals)),
    data.price,
    0.0
  ) {
    currency = CryptoUtils.formatDouble(count * data.price)
  }

  companion object {

    fun getModels(walletAddress: String, hold: (ArrayList<WalletDetailCellModel>) -> Unit) {
      DefaultTokenTable.getTokens {
        completeTokensInfo(it, walletAddress, hold)
      }
    }

    private fun completeTokensInfo(
      defaultTokens: ArrayList<DefaultTokenTable>,
      walletAddress: String,
      hold: (ArrayList<WalletDetailCellModel>) -> Unit
    ) {
      val tokenList = ArrayList<WalletDetailCellModel>()
      MyTokenTable.getTokensWith(walletAddress) { allTokens ->
        allTokens.forEach { token ->
          defaultTokens.find { it.symbol == token.symbol }?.let {
            tokenList.add(WalletDetailCellModel(it, token.balance))
            if (tokenList.size == allTokens.size) hold(tokenList)
          }
        }
      }
    }

  }
}