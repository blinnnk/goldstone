package io.goldstone.blockchain.module.home.wallet.walletdetail.model

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
  var balance: Double = 0.0,
  var price: Double = 0.0,
  var currency: Double = 0.0
) {
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
        allTokens.forEachIndexed { index, token ->
          defaultTokens.find { it.symbol == token.symbol }?.let {
            val count = CryptoUtils.formatDouble(token.balance / Math.pow(10.0, it.decimals))
            tokenList.add(WalletDetailCellModel(
              it.iconUrl,
              it.symbol,
              it.name,
              count,
              it.price,
              CryptoUtils.formatDouble(count * it.price)
            ))
            if (index == allTokens.lastIndex) { hold(tokenList) }
          }
        }
      }
    }


  }
}