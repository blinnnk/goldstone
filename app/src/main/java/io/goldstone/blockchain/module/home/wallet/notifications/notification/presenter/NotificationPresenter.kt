package io.goldstone.blockchain.module.home.wallet.notifications.notification.presenter

import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.otherwise
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment

/**
 * @date 25/03/2018 1:46 AM
 * @author KaySaith
 */

class NotificationPresenter(
  override val fragment: NotificationFragment
  ) : BaseOverlayPresenter<NotificationFragment>() {

  fun showTargetFragment(isDetail: Boolean) {
    fragment.apply {
      isDetail.isFalse {
        addFragmentAndSetArgument<NotificationListFragment>(ContainerID.content) {
          // Send Arguments
        }
      } otherwise {
        childFragmentManager.fragments.last()?.let {
          if (it is NotificationListFragment) hideChildFragment(it)
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