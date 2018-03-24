package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter

/**
 * @date 24/03/2018 2:14 PM
 * @author KaySaith
 */

class TransactionListAdapter(override val dataSet: ArrayList<Int>) : HoneyBaseAdapter<Int, TransactionListCell>() {

  override fun generateCell(context: Context) = TransactionListCell(context)

  override fun TransactionListCell.bindCell(data: Int) {

  }


}