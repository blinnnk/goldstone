package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter

import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view.WalletAddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class WalletAddressManagerPresneter(
	override val fragment: WalletAddressManagerFragment
) : BasePresenter<WalletAddressManagerFragment>() {
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getCurrentWalletEthereumSeriesAddress()
		showAddChildAddressButton()
	}
	
	private fun getCurrentWalletEthereumSeriesAddress() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumAddressModel(convertToChildAddresses(ethSeriesAddresses))
			}
		}
	}
	
	private fun showAddChildAddressButton() {
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.showAddButton(true, false) {
				// TODO 添加快捷创建子账号的按钮
			}
		}
	}
	
	private fun getCurrentWalletBtcSeriesAddress() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumAddressModel(convertToChildAddresses(btcSeriesAddresses))
			}
		}
	}
	
	private fun convertToChildAddresses(seriesAddress: String): List<String> {
		return if (seriesAddress.contains(",")) {
			seriesAddress.split(",")
		} else {
			listOf(seriesAddress)
		}
	}
}