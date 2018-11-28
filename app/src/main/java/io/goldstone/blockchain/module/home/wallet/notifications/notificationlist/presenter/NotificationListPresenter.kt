package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter

import android.os.Bundle
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationModel
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListAdapter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */
class NotificationListPresenter(
	override val fragment: NotificationListFragment
) : BaseRecyclerPresenter<NotificationListFragment, NotificationTable>() {

	override fun updateData() {
		super.updateData()
		getDataFromDatabase()
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.getMainActivity()?.getWalletDetailFragment()?.presenter?.start()
	}

	fun showTransactionDetailFragment(transactionInfo: NotificationModel) {
		fragment.getParentFragment<NotificationFragment>()?.apply {
			presenter.showTargetFragment<TransactionDetailFragment>(
				Bundle().apply {
					putSerializable(ArgumentKey.notificationTransaction, transactionInfo)
				})
		}
	}

	fun showWebFragment(title: String, url: String) {
		fragment.getParentFragment<NotificationFragment>()?.apply {
			presenter.showTargetFragment<WebViewFragment>(
				Bundle().apply {
					putString(ArgumentKey.webViewUrl, url)
					putString(ArgumentKey.webViewName, title)
				})
		}
	}

	private var hasLoadFromServer = false
	private fun getDataFromDatabase() {
		fragment.showLoadingView(true)
		NotificationTable.getAllNotifications { localData ->
			val latestTime = localData.maxBy { it.createTime }?.createTime ?: 0
			if (fragment.asyncData.isNull())
				fragment.asyncData = localData
			else diffAndUpdateSingleCellAdapterData<NotificationListAdapter>(localData)
			if (NetworkUtil.hasNetworkWithAlert(fragment.context) && !hasLoadFromServer)
				updateDataFromServer(latestTime)
			else fragment.showLoadingView(false)
		}
	}

	private fun updateDataFromServer(requestTime: Long) {
		GoldStoneAPI.getNotificationList(requestTime) { notificationList, error ->
			if (notificationList.isNotNull() && error.isNone()) {
				hasLoadFromServer = true
				if (notificationList.isNotEmpty())
					GoldStoneDataBase.database.notificationDao().insertAll(notificationList)
				launchUI { getDataFromDatabase() }
			} else launchUI {
				fragment.showLoadingView(false)
			}
		}
	}
}