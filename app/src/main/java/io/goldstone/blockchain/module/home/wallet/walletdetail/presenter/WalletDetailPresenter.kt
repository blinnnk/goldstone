package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
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

class WalletDetailPresenter(
	override val fragment: WalletDetailFragment
) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {

	override fun onFragmentShowFromHidden() {
		updateMyTokensPrice()
	}

	override fun updateData() {
		updateAllTokensInWallet()
	}

	fun updateMyTokensPrice() {
		fragment.asyncData?.let { asyncData ->
			asyncData.map { it.contract }.toJsonArray {
				GoldStoneAPI.getPriceByContractAddress(it) { newPrices ->
					newPrices.forEachOrEnd { item, isEnd ->
						// 同时更新缓存里面的数据
						DefaultTokenTable.updateTokenPrice(item.contract, item.price) {
							if (isEnd) { updateAllTokensInWallet() }
						}
					}
				}
			}
		}
	}

	private fun updateAllTokensInWallet() {
		// 先初始化空数组再更新列表
		fragment.asyncData.isNull() isTrue {
			fragment.asyncData = arrayListOf()
		}
		// Check the count of local wallets
		WalletTable.apply { getAll { walletCount = size } }
		// Check the info of wallet currency list
		WalletDetailCellModel.getModels { it ->
			val newData =
				it.sortedByDescending { it.currency }.toArrayList()
			diffAndUpdateAdapterData<WalletDetailAdapter>(newData)
			fragment.updateHeaderValue()
			fragment.setEmptyViewBy(it)
		}
	}

	/**
	 * 每次后台到前台更新首页的 `token` 信息
	 */
	override fun onFragmentResume() {
		CreateWalletPresenter.updateMyTokensValue {
			if (fragment.asyncData.isNull()) {
				updateAllTokensInWallet()
			} else {
				updateMyTokensPrice()
			}
		}
		showPinCodeFragment()
	}

	private fun showPinCodeFragment() {
		fragment.activity?.supportFragmentManager?.findFragmentByTag(FragmentTag.pinCode).isNull() isTrue {
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
			putString(ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings)
		}
	}

	fun showMyTokenDetailFragment(model: WalletDetailCellModel) {
		fragment.activity?.addFragmentAndSetArguments<TokenDetailOverlayFragment>(ContainerID.main) {
			putSerializable(ArgumentKey.tokenDetail, model)
		}
	}

	private fun WalletDetailFragment.updateHeaderValue() {
		val totalBalance = fragment.asyncData?.sumByDouble { it.currency }
		// Once the calculation is finished then update `WalletTable`
		WalletTable.current.balance = totalBalance
		recyclerView.getItemAtAdapterPosition<WalletDetailHeaderView>(0) {
			it?.model = WalletDetailHeaderModel(
				null,
				CryptoUtils.scaleTo9(WalletTable.current.name),
				CryptoUtils.scaleAddress(WalletTable.current.address),
				totalBalance.toString(),
				WalletTable.walletCount.orZero()
			)
		}
	}
}



