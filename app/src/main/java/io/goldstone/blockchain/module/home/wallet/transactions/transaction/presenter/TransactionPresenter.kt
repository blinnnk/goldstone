package io.goldstone.blockchain.module.home.wallet.transactions.transaction.presenter

import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.otherwise
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListFragment

/**
 * @date 24/03/2018 2:37 AM
 * @author KaySaith
 */

class TransactionPresenter(
  override val fragment: TransactionFragment
) : BaseOverlayPresenter<TransactionFragment>() {

  fun showTargetFragment(isDetail: Boolean) {
    fragment.apply {
      isDetail.isFalse {
        addFragmentAndSetArgument<TransactionListFragment>(ContainerID.content) {
          // Send Arguments
        }
      } otherwise {
        childFragmentManager.fragments.last()?.let {
          if (it is TransactionListFragment) hideChildFragment(it)
          addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
            // Send Arguments
          }
          overlayView.header.apply {
            showBackButton(true) {
              popFragmentFrom<TransactionDetailFragment>()
            }
            showCloseButton(false)
          }
        }
      }
    }
  }

}

