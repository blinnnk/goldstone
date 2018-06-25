package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @date 24/03/2018 2:14 PM
 * @author KaySaith
 */

class TransactionListAdapter(
  override val dataSet: ArrayList<TransactionListModel>,
  private val callback: TransactionListCell.() -> Unit
  ) : HoneyBaseAdapter<TransactionListModel, TransactionListCell>() {

  override fun generateCell(context: Context) = TransactionListCell(context)

  override fun TransactionListCell.bindCell(data: TransactionListModel, position: Int) {
    model = data
    callback(this)
  }
}