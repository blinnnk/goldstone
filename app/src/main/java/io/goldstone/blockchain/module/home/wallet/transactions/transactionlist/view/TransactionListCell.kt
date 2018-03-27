package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.textColor

@SuppressLint("SetTextI18n")
/**
 * @date 24/03/2018 2:15 PM
 * @author KaySaith
 */

open class TransactionListCell(context: Context) : BaseValueCell(context) {

  var model: TransactionListModel by observing(TransactionListModel()) {

    icon.apply {
      if (model.isReceived) {
        src = R.drawable.receive_icon
        iconColor = Spectrum.green
        count.title.textColor = Spectrum.green
      } else {
        src = R.drawable.send_icon
        iconColor = GrayScale.midGray
        count.title.textColor = Spectrum.red
      }
    }

    info.apply {
      title.text = model.addressName
      subtitle.text = model.addressInfo
    }

    count.apply {
      title.text = (if (model.isReceived) "+" else "-") + model.count.toString()
      subtitle.text = model.symbol
    }

  }

  init {

    setGrayStyle()
    setValueStyle(true)
  }

}