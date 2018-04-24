package io.goldstone.blockchain.module.home.wallet.notifications.notification.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.NotificationText
import io.goldstone.blockchain.module.home.wallet.notifications.notification.presenter.NotificationPresenter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment

/**
 * @date 25/03/2018 1:46 AM
 * @author KaySaith
 */

class NotificationFragment : BaseOverlayFragment<NotificationPresenter>() {

  override val presenter = NotificationPresenter(this)

  override fun ViewGroup.initView() {
    headerTitle = NotificationText.notification
    addFragmentAndSetArgument<NotificationListFragment>(ContainerID.content) {
      // Send Argument
    }
  }

}