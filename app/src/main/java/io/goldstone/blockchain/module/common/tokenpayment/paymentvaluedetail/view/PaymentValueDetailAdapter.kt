package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel

/**
 * @date 28/03/2018 12:24 PM
 * @author KaySaith
 */

class PaymentValueDetailAdapter(
  override val dataSet: ArrayList<PaymentValueDetailModel>,
  private val callback: PaymentValueDetailCell.() -> Unit
  ) : HoneyBaseAdapterWithHeaderAndFooter<PaymentValueDetailModel, PaymentValueDetailHeaderView, PaymentValueDetailCell, PaymentValueDetailFooter>() {

  override fun generateCell(context: Context): PaymentValueDetailCell {
    val cell = PaymentValueDetailCell(context)
    callback(cell)
   return cell
  }

  override fun generateFooter(context: Context) = PaymentValueDetailFooter(context)

  override fun generateHeader(context: Context) = PaymentValueDetailHeaderView(context)

  override fun PaymentValueDetailCell.bindCell(data: PaymentValueDetailModel, position: Int) {
    model = data
  }

}