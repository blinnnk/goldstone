package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailPresenter(
  override val fragment: PaymentValueDetailFragment
) : BaseRecyclerPresenter<PaymentValueDetailFragment, PaymentValueDetailModel>() {

  fun beginTransfer() {
    transfer()
  }

  private fun transfer() {
    val code = "0xf8ad830f42408505d21dba00830493e0944c0a1ffca1c5a08a73ef7d7021e74ddbfed4975d80b844a9059cbb000000000000000000000000b1fdd955d6179d84299cd98c268d80542746600b0000000000000000000000000000000000000000000000008ac7230489e800002aa09b1c5a9a638c23f6108d1b90fd515d4ade2e3234c9765c0f831216f69f1d6282a0695bfe5dc901cd1b38ed04f7c1ee77c3bd884b8e2bcb854f05d1b74ef7cc3674"

    GoldStoneEthCall.sendRawTransaction(code) {
      System.out.println(it)
    }
  }

}