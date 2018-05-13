package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.NotificationText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListAdapter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import java.io.Serializable

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */

data class NotificationTransactionInfo(val hash: String, val isReceived: Boolean) : Serializable

class NotificationListPresenter(
	override val fragment: NotificationListFragment
) : BaseRecyclerPresenter<NotificationListFragment, NotificationTable>() {

	override fun updateData() {
		super.updateData()
		getDataFromDatabase()
	}

	private fun getDataFromDatabase() {
		fragment.showLoadingView("Loading notification history data")
		NotificationTable.getAllNotifications { localData ->
			val latestTime = localData.maxBy { it.createTIme }?.createTIme
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
			updateParentContentLayoutHeight(localData.size, fragment.setSlideUpWithCellHeight())
		}
	}

	private fun updateDataFromServer(requestTime: Long) {
		AppConfigTable.getAppConfig { config ->
			GoldStoneAPI.getNotificationList(config?.goldStoneID.orEmpty(), requestTime) {
				fragment.removeLoadingView()
				it.isNotEmpty() isTrue {
					NotificationTable.insertData(it.map { NotificationTable(it) }.toArrayList()) {
						getDataFromDatabase()
					}
				}
			}
		}
	}

	fun showTransactionListDetailFragment(transactionInfo: NotificationTransactionInfo) {
		fragment.getParentFragment<NotificationFragment>()?.apply {
			presenter.showTargetFragment<TransactionDetailFragment>(TransactionText.detail,
				NotificationText.notification,
				Bundle().apply { putSerializable(ArgumentKey.notificationTransaction, transactionInfo) })
		}
	}

}