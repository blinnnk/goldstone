package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailAdapter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view.WalletManagementFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */

/**
 * 放在内存里面的数据, 提升展示的速度
 */
var walletDetailMemoryData: ArrayList<WalletDetailCellModel>? = null

class WalletDetailPresenter(
	override val fragment: WalletDetailFragment
) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {

	override fun onFragmentShowFromHidden() {
		updateData()
	}

	override fun updateData() {
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
				}
			}
		}
	}

	private fun updateUIByData(data: ArrayList<WalletDetailCellModel>) {
		coroutinesTask({
			/** 先按照资产情况排序, 资产为零的按照权重排序 */
			val currencyList = data.filter { it.currency > 0.0 }
			val weightList = data.filter { it.currency == 0.0 }
			currencyList.sortedByDescending { it.currency }
				.plus(weightList.sortedByDescending { it.weight }).toArrayList()
		}) {
			walletDetailMemoryData = it
			diffAndUpdateAdapterData<WalletDetailAdapter>(it)
			fragment.updateHeaderValue()
			fragment.setEmptyViewBy(it)
		}
	}

	/**
	 * 每次后台到前台更新首页的 `token` 信息
	 */
	override fun onFragmentResume() {
		updateData()
		showPinCodeFragment()
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

	fun showTransactionsFragment() {
		fragment.activity?.addFragment<TransactionFragment>(ContainerID.main)
	}

	fun showWalletListFragment() {
		fragment.activity?.addFragment<WalletManagementFragment>(ContainerID.main)
	}

	fun showNotificationListFragment() {
		fragment.activity?.addFragment<NotificationFragment>(ContainerID.main)
	}

	fun showTokenManagementFragment() {
		fragment.activity?.addFragment<TokenManagementFragment>(ContainerID.main)
	}

	fun showWalletSettingsFragment() {
		fragment.activity?.addFragmentAndSetArguments<WalletSettingsFragment>(ContainerID.main) {
			putString(
				ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings
			)
		}
	}

	fun showMyTokenDetailFragment(model: WalletDetailCellModel) {
		fragment.activity?.addFragmentAndSetArguments<TokenDetailOverlayFragment>(ContainerID.main) {
			putSerializable(
				ArgumentKey.tokenDetail, model
			)
		}
	}

	private fun WalletDetailFragment.updateHeaderValue() {
		val totalBalance = fragment.asyncData?.sumByDouble { it.currency }
		// Once the calculation is finished then update `WalletTable`
		WalletTable.current.balance = totalBalance
		recyclerView.getItemAtAdapterPosition<WalletDetailHeaderView>(0) {
			it?.model = WalletDetailHeaderModel(
				null, CryptoUtils.scaleTo9(WalletTable.current.name),
				CryptoUtils.scaleAddress(WalletTable.current.address), totalBalance.toString(),
				WalletTable.walletCount.orZero()
			)
		}
	}
}


