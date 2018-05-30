package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailHeaderModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailAdapter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view.WalletManagementFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */
class WalletDetailPresenter(
	override val fragment: WalletDetailFragment
) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		updateData()
		updateUnreadCount()
		fragment.getMainActivity()?.backEvent = null
	}
	
	override fun updateData() {
		fragment.showMiniLoadingView()
		// 查询钱包总数更新数字
		WalletTable.apply { getAll { walletCount = size } }
		// 先初始化空数组再更新列表
		if (fragment.asyncData.isNull()) {
			fragment.asyncData = arrayListOf()
			fragment.updateHeaderValue()
		}
		// Check the info of wallet currency list
		WalletDetailCellModel.apply {
			// 显示本地的 `Token` 据
			getLocalModels { it ->
				updateUIByData(it)
				// 再检查链上的最新价格和数量
				getChainModels {
					updateUIByData(it)
					fragment.removeMiniLoadingView()
				}
			}
		}
	}
	
	/**
	 * 每次后台到前台更新首页的 `token` 信息, 除了第一次初始化加载的时候
	 */
	override fun onFragmentResume() {
		updateData()
		showPinCodeFragment()
		updateUnreadCount()
		fragment.getMainActivity()?.backEvent = null
	}
	
	fun showTransactionsFragment() {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.transaction) isFalse {
				addFragment<TransactionFragment>(ContainerID.main, FragmentTag.transaction)
			}
		}
	}
	
	fun showWalletListFragment() {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.walletManagement) isFalse {
				addFragment<WalletManagementFragment>(ContainerID.main, FragmentTag.walletManagement)
			}
		}
	}
	
	fun showNotificationListFragment() {
		XinGePushReceiver.clearAppIconRedot()
		fragment.activity?.apply {
			findIsItExist(FragmentTag.notification) isFalse {
				addFragment<NotificationFragment>(ContainerID.main, FragmentTag.notification)
			}
		}
	}
	
	fun showTokenManagementFragment() {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.tokenManagement) isFalse {
				addFragment<TokenManagementFragment>(ContainerID.main, FragmentTag.tokenManagement)
			}
		}
	}
	
	fun showWalletSettingsFragment() {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.walletSettings) isFalse {
				addFragmentAndSetArguments<WalletSettingsFragment>(
					ContainerID.main, FragmentTag.walletSettings
				) {
					putString(
						ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings
					)
				}
			}
		}
	}
	
	fun showMyTokenDetailFragment(model: WalletDetailCellModel) {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.tokenDetail) isFalse {
				addFragmentAndSetArguments<TokenDetailOverlayFragment>(
					ContainerID.main, FragmentTag.tokenDetail
				) {
					putSerializable(
						ArgumentKey.tokenDetail, model
					)
				}
			}
		}
	}
	
	fun updateUnreadCount() {
		doAsync {
			AppConfigTable.getAppConfig { config ->
				NotificationTable.getAllNotifications { notifications ->
					config?.apply {
						/**
						 * 时间戳, 如果本地一条通知记录都没有, 那么传入设备创建的时间, 就是 `GoldStone ID` 的最后 `13` 位
						 * 如果本地有数据获取最后一条的创建时间作为请求时间
						 */
						val time = if (notifications.isEmpty()) goldStoneID.substring(
							goldStoneID.length - System.currentTimeMillis().toString().length, goldStoneID.length
						).toLong()
						else notifications.maxBy { it.createTIme }?.createTIme.orElse(0)
						GoldStoneAPI.getUnreadCount(goldStoneID, time) {
							GoldStoneAPI.context.runOnUiThread {
								if (it.isNotEmpty() && it.toIntOrNull().orZero() > 0) {
									fragment.setNotificationUnreadCount(it)
								} else {
									fragment.recoveryNotifyButtonStyle()
								}
							}
						}
					}
				}
			}
		}
	}
	
	private fun updateUIByData(data: ArrayList<WalletDetailCellModel>) {
		coroutinesTask(
			{
				/** 先按照资产情况排序, 资产为零的按照权重排序 */
				val currencyList = data.filter { it.currency > 0.0 }
				val weightList = data.filter { it.currency == 0.0 }
				currencyList.sortedByDescending { it.currency }
					.plus(weightList.sortedByDescending { it.weight }).toArrayList()
			}
		) {
			diffAndUpdateAdapterData<WalletDetailAdapter>(it)
			fragment.updateHeaderValue()
		}
	}
	
	private fun showPinCodeFragment() {
		fragment.activity?.supportFragmentManager?.findFragmentByTag(
			FragmentTag.pinCode
		).isNull() isTrue {
			AppConfigTable.getAppConfig {
				it?.showPincode?.isTrue {
					fragment.activity?.addFragmentAndSetArguments<PasscodeFragment>(
						ContainerID.main, FragmentTag.pinCode
					) {
						// Send Argument
					}
				}
			}
		}
	}
	
	private fun WalletDetailFragment.updateHeaderValue() {
		val totalBalance = fragment.asyncData?.sumByDouble { it.currency }
		// Once the calculation is finished then update `WalletTable`
		WalletTable.current.balance = totalBalance
		recyclerView.getItemAtAdapterPosition<WalletDetailHeaderView>(0) {
			it?.model = WalletDetailHeaderModel(
				null,
				WalletTable.current.name,
				CryptoUtils.scaleAddress(WalletTable.current.address),
				totalBalance.toString(),
				WalletTable.walletCount.orZero()
			)
		}
	}
}


