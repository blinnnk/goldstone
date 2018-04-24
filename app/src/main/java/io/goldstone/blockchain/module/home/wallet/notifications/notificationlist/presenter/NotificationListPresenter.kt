package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListAdapter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment

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

  private fun getDataFromDatabase() {
    fragment.getMainActivity()?.showLoadingView()
    NotificationTable.getAllNotifications { localData ->
      val latestTime =localData.maxBy { it.createTIme }?.createTIme
      val requestTime = if (latestTime.isNull()) 0 else latestTime!!
      fragment.asyncData.isNull() isFalse {
        diffAndUpdateSingleCellAdapterData<NotificationListAdapter>(localData)
      } otherwise {
        fragment.asyncData = localData
      }
      updateDataFromServer(requestTime)
    }
  }

  private fun updateDataFromServer(requestTime: Long) {
    AppConfigTable.getAppConfig { config ->
      GoldStoneAPI.getNotificationList(config?.goldStoneID.orEmpty(), requestTime) {
        fragment.getMainActivity()?.removeLoadingView()
        it.isNotEmpty() isTrue {
          NotificationTable.insertData(it.map { NotificationTable(it) }.toArrayList()) {
            getDataFromDatabase()
          }
        }
      }
    }
  }

  fun showTransactionListDetailFragment() {
    fragment.getParentFragment<NotificationFragment>()?.presenter?.showTargetFragment(true)
  }

}