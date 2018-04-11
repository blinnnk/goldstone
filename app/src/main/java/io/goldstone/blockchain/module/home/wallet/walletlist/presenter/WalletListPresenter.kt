package io.goldstone.blockchain.module.home.wallet.walletlist.presenter

import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletlist.view.WalletListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */

class WalletListPresenter(
  override val fragment: WalletListFragment
) : BaseRecyclerPresenter<WalletListFragment, WalletListModel>() {

  override fun updateData() {
    updateAllWalletBalance {
      fragment.asyncData = this
    }
  }

  fun switchWallet(address: String) {
    WalletTable.switchCurrentWallet(address) {
      fragment.activity?.jump<MainActivity>()
    }
  }

  private fun updateAllWalletBalance(hold: ArrayList<WalletListModel>.() -> Unit) {
    val data = ArrayList<WalletListModel>()
    // 获取全部本机钱包
    WalletTable.getAll {
      // 获取全部本地记录的 `Token` 信息
      DefaultTokenTable.getTokens { allTokens ->
        doAsync {
          forEach { wallet ->
            // 获取对应的钱包下的全部 `token`
            MyTokenTable.getTokensWith(wallet.address) {
              // 计算当前钱包下的 `token` 对应的货币总资产
              WalletListModel(wallet, it.sumByDouble { walletToken ->
                val thisToken = allTokens.find { it.symbol == walletToken.symbol }!!
                CryptoUtils.toCountByDecimal(walletToken.balance, thisToken.decimals) * thisToken.price
              }).let {
                data.add(it)
                fragment.context?.runOnUiThread {
                  // 因为结果集是在异步状态下准备, 返回的数据按照 `id` 重新排序
                  if (data.size == size) hold(data.sortedByDescending { it.id }.toArrayList())
                }
              }
            }
          }
        }
      }
    }
  }

}