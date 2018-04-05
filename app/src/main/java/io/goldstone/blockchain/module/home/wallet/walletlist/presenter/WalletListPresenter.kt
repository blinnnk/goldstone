package io.goldstone.blockchain.module.home.wallet.walletlist.presenter

import com.blinnnk.extension.jump
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletlist.view.WalletListFragment

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */

class WalletListPresenter(
  override val fragment: WalletListFragment
  ) : BaseRecyclerPresenter<WalletListFragment, WalletListModel>() {

  override fun updateData(asyncData: ArrayList<WalletListModel>?) {
    val walletList = ArrayList<WalletListModel>()
    WalletTable.getAll {
      forEachIndexed { index, wallet ->
        walletList.add(
          WalletListModel(
            wallet.name,
            wallet.address,
            wallet.balance.orElse(0.0),
            R.drawable.avatar
          )
        )
        if (index == lastIndex && walletList.size == size) {
          fragment.asyncData = walletList
        }
      }
    }
  }

  fun switchWallet(address: String) {
    WalletTable.switchCurrentWallet(address) {
      fragment.activity?.jump<MainActivity>()
    }
  }

}