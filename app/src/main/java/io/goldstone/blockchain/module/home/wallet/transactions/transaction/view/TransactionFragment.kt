package io.goldstone.blockchain.module.home.wallet.transactions.transaction.view

import android.view.ViewGroup
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.presenter.TransactionPresenter

/**
 * @date 24/03/2018 2:37 AM
 * @author KaySaith
 */

class TransactionFragment : BaseOverlayFragment<TransactionPresenter>() {

  override val presenter = TransactionPresenter(this)

  override fun setContentHeight() = 500.uiPX()

  override fun ViewGroup.initView() {
    headerTitle = TransactionText.trannsaction
  }

}