package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.NotificationText
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.isBTCSeries
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 1:48 AM
 * @author KaySaith
 */
class NotificationListFragment :
	BaseRecyclerFragment<NotificationListPresenter, NotificationTable>() {

	override val pageTitle: String = NotificationText.notification
	override val presenter = NotificationListPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<NotificationTable>?
	) {
		recyclerView.adapter = NotificationListAdapter(asyncData.orEmptyArray()) {
			onClick { _ ->
				model?.apply {
					if (type == 1) {
						presenter.showWebFragment(title, actionContent)
					} else {
						val fromAddress: String
						val toAddress: String
						if (CoinSymbol(NotificationTable.getSymbol(extra.orEmpty())).isBTCSeries()) {
							// TODO Bitcoin Transaction FromAddress 需要处理多 FromAddress 地址的情况
							fromAddress =
								NotificationTable.getBTCTransactionData(extra.orEmpty(), true)[0].address
							toAddress =
								NotificationTable.getBTCTransactionData(extra.orEmpty(), false)
									.map { it.address }
									.toString()
						} else {
							fromAddress = NotificationTable.getFromAddress(extra.orEmpty())
							toAddress = NotificationTable.getToAddress(extra.orEmpty())
						}
						presenter.showTransactionListDetailFragment(
							NotificationTransactionInfo(
								actionContent,
								NotificationTable.getChianID(extra.orEmpty()),
								NotificationTable.getReceiveStatus(extra.orEmpty()).orFalse(),
								NotificationTable.getSymbol(extra.orEmpty()),
								NotificationTable.getValue(extra.orEmpty()),
								createTime,
								toAddress,
								fromAddress
							)
						)
					}
				}
				preventDuplicateClicks()
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}
}