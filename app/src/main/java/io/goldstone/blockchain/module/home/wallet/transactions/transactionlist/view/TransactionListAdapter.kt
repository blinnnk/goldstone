package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 24/03/2018 2:14 PM
 * @author KaySaith
 */

class TransactionListAdapter(
  override val dataSet: ArrayList<TransactionListModel>
  ) : HoneyBaseAdapter<TransactionListModel, TransactionListCell>() {

  override fun generateCell(context: Context) = TransactionListCell(context)

  override fun TransactionListCell.bindCell(data: TransactionListModel) {
    model = data
  }


}