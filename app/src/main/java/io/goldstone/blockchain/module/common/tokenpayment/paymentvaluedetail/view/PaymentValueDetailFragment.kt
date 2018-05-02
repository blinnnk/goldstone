package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter.PaymentValueDetailPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailFragment :
  BaseRecyclerFragment<PaymentValueDetailPresenter, PaymentValueDetailModel>() {

  val address by lazy { arguments?.getString(ArgumentKey.paymentAddress) }
  val token by lazy { arguments?.get(ArgumentKey.paymentSymbol) as? WalletDetailCellModel }

  private var transferCount = 0.0
  override val presenter = PaymentValueDetailPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView, asyncData: ArrayList<PaymentValueDetailModel>?
  ) {
    recyclerView.adapter = PaymentValueDetailAdapter(asyncData.orEmptyArray()) {
      presenter.setCellClickEvent(this)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    recyclerView.getItemViewAtAdapterPosition<PaymentValueDetailHeaderView>(0) {
      setInputFocus()
      address?.apply { showTargetAddress(this) }
      presenter.updateHeaderValue(this)
      inputTextListener {
        it.isNotEmpty() isTrue {
          transferCount = it.toDouble()
        }
      }
      setHeaderSymbol(token?.symbol.orEmpty())
    }

    recyclerView.getItemViewAtAdapterPosition<PaymentValueDetailFooter>(asyncData?.size.orZero() + 1) {
      MyTokenTable.getBalanceWithSymbol(token?.symbol!!, WalletTable.current.address, true) { balance ->
        confirmClickEvent = Runnable {
          if (transferCount <= 0) {
            context?.runOnUiThread {
              alert("Please Enter Your Transfer Value")
            }
          } else {
            if (balance > transferCount) showConfirmAttentionView()
            else {
              context?.runOnUiThread {
                alert("You haven't enough currency to transfer")
              }
            }
          }
        }
      }
    }
  }

  private fun showConfirmAttentionView() {
    context?.showAlertView(
      TransactionText.confirmTransaction,
      CommonText.enterPassword.toUpperCase()
    ) {
      presenter.transfer(it?.text.toString())
    }
  }

}