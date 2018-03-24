package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationListModel
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationListPresenter

/**
 * @date 25/03/2018 1:48 AM
 * @author KaySaith
 */

class NotificationListFragment : BaseRecyclerFragment<NotificationListPresenter, NotificationListModel>() {

  override val presenter = NotificationListPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<NotificationListModel>?) {
    recyclerView.adapter = NotificationListAdapter(asyncData.orEmptyArray())
  }

  override fun setSlideUpWithCellHeight() = 60.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      NotificationListModel("12.92 EOS received successful", "From: 0x83o2pd......8d87s6d7f ", "3 days ago"),
      NotificationListModel("5.28 ETH received successful", "From: 0x83o2pd......8d87s6d7f ", "4 days ago"),
      NotificationListModel("1 ETH received successful", "From: 0x83o2pd......8d87s6d7f ", "1 week ago"),
      NotificationListModel("12 EOS received successful", "From: 0x83o2pd......8d87s6d7f ", "1 week ago"),
      NotificationListModel("12.92 EOS received successful", "From: 0x83o2pd......8d87s6d7f ", "3 days ago"),
      NotificationListModel("5.28 ETH received successful", "From: 0x83o2pd......8d87s6d7f ", "4 days ago"),
      NotificationListModel("1 ETH received successful", "From: 0x83o2pd......8d87s6d7f ", "1 week ago"),
      NotificationListModel("12 EOS received successful", "From: 0x83o2pd......8d87s6d7f ", "1 week ago")
    )

  }

}