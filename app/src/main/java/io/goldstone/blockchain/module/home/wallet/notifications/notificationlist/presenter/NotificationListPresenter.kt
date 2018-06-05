package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListAdapter
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import java.io.Serializable

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */
data class NotificationTransactionInfo(
	val hash: String,
	val chainID: String,
	val isReceived: Boolean
) : Serializable

class NotificationListPresenter(
	override val fragment: NotificationListFragment
) : BaseRecyclerPresenter<NotificationListFragment, NotificationTable>() {
	
	override fun updateData() {
		super.updateData()
		getDataFromDatabase()
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.getMainActivity()
			?.supportFragmentManager
			?.findFragmentByTag(FragmentTag.home)
			?.findChildFragmentByTag<WalletDetailFragment>(FragmentTag.walletDetail)
			?.apply {
				presenter.updateUnreadCount()
			}
	}
	
	fun showTransactionListDetailFragment(transactionInfo: NotificationTransactionInfo) {
		fragment.getParentFragment<NotificationFragment>()?.apply {
			presenter.showTargetFragment<TransactionDetailFragment>(
				TransactionText.detail,
				NotificationText.notification,
				Bundle().apply {
					putSerializable(
						ArgumentKey.notificationTransaction,
						transactionInfo
					)
				})
		}
	}
	
	fun showWebFragment(title: String, url: String) {
		fragment.getParentFragment<NotificationFragment>()?.apply {
			presenter.showTargetFragment<WebViewFragment>(
				title,
				NotificationText.notification,
				Bundle().apply {
					putString(ArgumentKey.webViewUrl, url)
				})
		}
		setHeightMatchParent()
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
			updateParentContentLayoutHeight(localData.size, fragment.setSlideUpWithCellHeight())
		}
	}
	
	private fun updateDataFromServer(requestTime: Long) {
		AppConfigTable.getAppConfig { config ->
			GoldStoneAPI.getNotificationList(
				config?.goldStoneID.orEmpty(),
				requestTime,
				{
					showServerErrorDialog()
				}
			) {
				fragment.removeLoadingView()
				it.isNotEmpty() isTrue {
					NotificationTable.insertData(it.map { NotificationTable(it) }.toArrayList()) {
						getDataFromDatabase()
					}
				}
			}
		}
	}
	
	private fun showServerErrorDialog() {
		// error call back
		GoldStoneDialog.show(fragment.context!!) {
			showOnlyConfirmButton {
				GoldStoneDialog.remove(fragment.context!!)
			}
			setContent(
				"SERVER IS BUSY",
				"a lot of requests are happening, please wait a moment, let little forg has a rest"
			)
			setImage(R.drawable.server_error_banner)
		}
	}
}