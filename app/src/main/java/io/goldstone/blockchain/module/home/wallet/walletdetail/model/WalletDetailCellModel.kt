package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
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
      GoldStoneDataBase.database.apply {
        DefaultTokenTable.getTokens {
          completeTokensInfo(it, walletAddress, hold)
        }
      }
    }

    private fun completeTokensInfo(
      defaultTokens: ArrayList<DefaultTokenTable>,
      walletAddress: String,
      hold: (ArrayList<WalletDetailCellModel>) -> Unit
      ) {
      val modelList = ArrayList<WalletDetailCellModel>()
      MyTokenTable.getTokensWith(walletAddress) {
        it.forEachIndexed { index, token ->
          defaultTokens.find { it.symbol == token.symbol }?.apply {
            modelList.add(WalletDetailCellModel(iconUrl, symbol, name, token.balance, price, 0.0))
            if (index == it.lastIndex) hold(modelList)
          }
        }
      }
    }
  }
}