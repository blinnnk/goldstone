package io.goldstone.blinnnk.module.home.wallet.notifications.notification.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.module.home.wallet.notifications.notification.presenter.NotificationPresenter
import io.goldstone.blinnnk.module.home.wallet.notifications.notificationlist.view.NotificationListFragment

/**
 * @date 25/03/2018 1:46 AM
 * @author KaySaith
 */

class NotificationFragment : BaseOverlayFragment<NotificationPresenter>() {

	override val presenter = NotificationPresenter(this)

	override fun ViewGroup.initView() {
		addFragmentAndSetArgument<NotificationListFragment>(ContainerID.content)
	}
}