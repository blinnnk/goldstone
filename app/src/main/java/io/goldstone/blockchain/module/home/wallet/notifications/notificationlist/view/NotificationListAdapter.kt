package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationListModel

/**
 * @date 25/03/2018 1:52 AM
 * @author KaySaith
 */

class NotificationListAdapter(
  override val dataSet: ArrayList<NotificationListModel>
  ) : HoneyBaseAdapter<NotificationListModel, NotificationListCell>() {

  override fun generateCell(context: Context) = NotificationListCell(context)

  override fun NotificationListCell.bindCell(data: NotificationListModel, position: Int) {
    model = data
  }


}