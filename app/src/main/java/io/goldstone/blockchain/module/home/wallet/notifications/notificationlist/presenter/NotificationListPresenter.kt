package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationListModel
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */

class NotificationListPresenter(
  override val fragment: NotificationListFragment
  ) : BaseRecyclerPresenter<NotificationListFragment, NotificationListModel>() {

  fun showTransactionListDetailFragment() {
    fragment.getParentFragment<NotificationFragment>()?.presenter?.showTargetFragment(true)
  }

}