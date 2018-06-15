package io.goldstone.blockchain.module.home.wallet.transactions.transaction.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.presenter.TransactionPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListFragment

/**
 * @date 24/03/2018 2:37 AM
 * @author KaySaith
 */

class TransactionFragment : BaseOverlayFragment<TransactionPresenter>() {

  override val presenter = TransactionPresenter(this)

  override fun ViewGroup.initView() {
    headerTitle = TransactionText.transaction
    addFragmentAndSetArgument<TransactionListFragment>(
      ContainerID.content,
      FragmentTag.transactionList
    )
  }

}