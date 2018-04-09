package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter.PaymentValueDetailPresenter

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailFragment : BaseRecyclerFragment<PaymentValueDetailPresenter, PaymentValueDetailModel>() {

  val address by lazy { arguments?.getString(ArgumentKey.paymentValueDetail) }

  override val presenter = PaymentValueDetailPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<PaymentValueDetailModel>?) {
    recyclerView.adapter = PaymentValueDetailAdapter(asyncData.orEmptyArray())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      PaymentValueDetailModel("0.00476 ETH", "≈ 47.6 Gwei(Gas Price) * 100000(Gas Limit)", "recommend", true),
      PaymentValueDetailModel("0.00258 ETH", "≈ 47.6 Gwei(Gas Price) * 100000(Gas Limit)", "cheap", false),
      PaymentValueDetailModel("0.00982 ETH", "≈ 47.6 Gwei(Gas Price) * 100000(Gas Limit)", "fast", false)
    )

    recyclerView.getItemViewAtAdapterPosition<PaymentValueDetailHeaderView>(0) {
      setInputFocus()
      address?.apply { showTargetAddress(this) }
    }

    recyclerView.getItemViewAtAdapterPosition<PaymentValueDetailFooter>(4) {
      confirmClickEvent = Runnable {
        System.out.println("hello work")
        presenter.beginTransfer()
      }
    }

  }

}