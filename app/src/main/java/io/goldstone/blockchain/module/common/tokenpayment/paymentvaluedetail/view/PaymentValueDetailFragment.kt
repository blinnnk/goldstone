package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.blinnnk.extension.into
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter.PaymentValueDetailPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.jetbrains.anko.*

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailFragment : BaseRecyclerFragment<PaymentValueDetailPresenter, PaymentValueDetailModel>() {

  val address by lazy { arguments?.getString(ArgumentKey.paymentAddress) }
  val symbol by lazy { arguments?.getString(ArgumentKey.paymentSymbol) }

  private var currentMinerFeeType = MinerFeeType.Recommend.content

  override val presenter = PaymentValueDetailPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<PaymentValueDetailModel>?) {
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
    }

    recyclerView.getItemViewAtAdapterPosition<PaymentValueDetailFooter>(asyncData?.size.orZero() + 1) {
      confirmClickEvent = Runnable {
        address?.apply { showConfirmAttentionView() }
      }
    }
  }

  private val passwordInput by lazy { EditText(context!!) }
  private fun showConfirmAttentionView() {
    context?.apply {
      alert(
        CommonText.enterPassword.toUpperCase(),
        TransactionText.confirmTransaction
      ) {
        WalletTable.current.isWatchOnly.isFalse {
          customView {
            verticalLayout {
              lparams { padding = 20.uiPX() }
              passwordInput.apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = CommonText.enterPassword
                hintTextColor = Spectrum.opacity1White
              }.into(this)
            }
          }
        }
        yesButton {
          presenter.transfer(currentMinerFeeType, passwordInput.text.toString())
          activity?.apply { SoftKeyboard.hide(this) }
        }
        noButton { }
      }.show()
    }
  }

}