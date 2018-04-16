package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import java.io.Serializable

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
  var currency: Double = 0.0,
  var contract: String = ""
) : Serializable {

  constructor(data: DefaultTokenTable, balance: Double) : this(
    data.iconUrl,
    data.symbol,
    data.name,
    data.decimals,
    CryptoUtils.formatDouble(balance / Math.pow(10.0, data.decimals)),
    data.price,
    0.0,
    data.contract
  ) {
    currency = CryptoUtils.formatDouble(count * data.price)
  }

  companion object {

    fun getModels(
      walletAddress: String = WalletTable.current.address,
      hold: (ArrayList<WalletDetailCellModel>) -> Unit
    ) {
      completeTokensInfo(walletAddress, hold)
    }

    private fun completeTokensInfo(
      walletAddress: String,
      hold: (ArrayList<WalletDetailCellModel>) -> Unit
    ) {
      MyTokenTable.getTokensWith(walletAddress) { allTokens ->
        object : ConcurrentAsyncCombine() {
          val tokenList = ArrayList<WalletDetailCellModel>()
          override var asyncCount: Int = allTokens.size
          override fun concurrentJobs() {
            DefaultTokenTable.getTokens { localTokens ->
              allTokens.forEach { token ->
                localTokens.find { it.symbol == token.symbol }?.let {
                  tokenList.add(WalletDetailCellModel(it, token.balance))
                  completeMark()
                }
              }
            }
          }
          override fun mergeCallBack() = hold(tokenList)
        }.start()
      }
    }
  }
}