package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter

import android.os.Bundle
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListAdapter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import org.jetbrains.anko.runOnUiThread

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
		fragment.getMainActivity()?.getWalletDetailFragment()?.updateUnreadCount()
	}

	fun showTransactionListDetailFragment(transactionInfo: NotificationTransactionInfo) {
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

	private fun getDataFromDatabase() {
		fragment.showLoadingView(LoadingText.notificationData)
		NotificationTable.getAllNotifications { localData ->
			val latestTime = localData.maxBy { it.createTime }?.createTime
			val requestTime = if (latestTime.isNull()) 0 else latestTime!!
			fragment.asyncData.isNull() isFalse {
				diffAndUpdateSingleCellAdapterData<NotificationListAdapter>(localData)
			} otherwise {
				fragment.asyncData = localData
			}
			NetworkUtil.hasNetworkWithAlert(fragment.context) isTrue {
				updateDataFromServer(requestTime)
			} otherwise {
				fragment.removeLoadingView()
			}
		}
	}

	private fun updateDataFromServer(requestTime: Long) {
		GoldStoneAPI.getNotificationList(requestTime) { notificationList, error ->
			fragment.removeLoadingView()
			if (!notificationList.isNull() && error.isNone()) {
				if (notificationList!!.isNotEmpty())
					GoldStoneDataBase.database.notificationDao().insertAll(notificationList)
			}
			fragment.context?.runOnUiThread {
				getDataFromDatabase()
			}
		}
	}
}