package io.goldstone.blockchain.module.home.wallet.walletlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.walletlist.model.WalletListModel

/**
 * @date 24/03/2018 8:57 PM
 * @author KaySaith
 */

class WalletListAdapter(
  override val dataSet: ArrayList<WalletListModel>
  ) : HoneyBaseAdapter<WalletListModel, WalletListCell>() {

  override fun generateCell(context: Context) = WalletListCell(context)

  override fun WalletListCell.bindCell(data: WalletListModel) {
    model = data
  }

}