package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter.PaymentValueDetailPresenter

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
        address?.apply { presenter.transfer(currentMinerFeeType, "125883") }
      }
    }

  }

}