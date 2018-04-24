package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 1:48 AM
 * @author KaySaith
 */

class NotificationListFragment : BaseRecyclerFragment<NotificationListPresenter, NotificationTable>() {

  override val presenter = NotificationListPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<NotificationTable>?) {
    recyclerView.adapter = NotificationListAdapter(asyncData.orEmptyArray()) {
      onClick {
        presenter.showTransactionListDetailFragment()
        preventDuplicateClicks()
      }
    }
  }

  override fun setSlideUpWithCellHeight() = 60.uiPX()

}