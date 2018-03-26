package io.goldstone.blockchain.module.home.wallet.notifications.notification.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.*
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
            showBackButton(true) { popFragment() }
            showCloseButton(false)
          }
        }
      }
    }
  }

  private fun NotificationFragment.popFragment() {
    childFragmentManager.fragments.apply {
      if (last() is TransactionDetailFragment) removeChildFragment(last())
      overlayView.header.apply {
        showBackButton(false)
        showCloseButton(true)
      }
      // 恢复 `TransactionListFragment` 的视图
      this[size - 2]?.recoveryHeight()
    }
  }

  private fun Fragment.recoveryHeight() {
    if (this is NotificationListFragment) {
      fragment.showChildFragment(this)
      presenter.recoveryFragmentHeight()
    }
  }

}