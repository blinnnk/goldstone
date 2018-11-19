package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.jump
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view.WalletListAdapter
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view.WalletListFragment

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */
class WalletListPresenter(
	override val fragment: WalletListFragment
) : BaseRecyclerPresenter<WalletListFragment, WalletListModel>() {

	override fun updateData() {
		fragment.asyncData = arrayListOf()
		WalletTable.getAll {
			diffAndUpdateAdapterData<WalletListAdapter>(
				mapTo(arrayListOf()) { wallet ->
					WalletListModel(wallet, wallet.getWalletType().type.orEmpty())
				}
			)
		}
	}

	private fun switchWalletInDatabase(address: String, isMainnet: Boolean) {
		WalletTable.switchCurrentWallet(address) { it ->
			SharedWallet.updateCurrentIsWatchOnlyOrNot(it.isWatchOnly)
			if (isMainnet) NodeSelectionPresenter.setAllMainnet {
				SharedValue.updateIsTestEnvironment(false)
				fragment.activity?.jump<SplashActivity>()
			} else NodeSelectionPresenter.setAllTestnet(true) {
				SharedValue.updateIsTestEnvironment(true)
				fragment.activity?.jump<SplashActivity>()
			}
		}
	}

	fun switchWallet(address: String) {
		load {
			WalletTable.dao.getWalletByAddress(address)?.getWalletType()!!
		} then { walletType ->
			when {
				walletType.isBTC() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Mainnet") {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
						fragment.activity?.jump<SplashActivity>()
					}
				}

				walletType.isBTCTest() -> {
					if (!SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Testnet") {
							switchWalletInDatabase(address, false)
						}
					} else {
						switchWalletInDatabase(address, false)
						fragment.activity?.jump<SplashActivity>()
					}
				}

				walletType.isLTC() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Litecoin Mainnet") {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
						fragment.activity?.jump<SplashActivity>()
					}
				}

				walletType.isBCH() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Cash Mainnet") {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
						fragment.activity?.jump<SplashActivity>()
					}
				}

				walletType.isEOSJungle() -> {
					if (!SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("EOS Jungle Testnet") {
							switchWalletInDatabase(address, false)
						}
					} else {
						switchWalletInDatabase(address, false)
						fragment.activity?.jump<SplashActivity>()
					}
				}

				walletType.isEOSMainnet() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("EOS Mainnet Testnet") {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
						fragment.activity?.jump<SplashActivity>()
					}
				}

				walletType.isBIP44() || walletType.isMultiChain() -> {
					if (SharedValue.isTestEnvironment())
						switchWalletInDatabase(address, false)
					else switchWalletInDatabase(address, true)
				}
				// `EOS` 以及以太坊都不需要额外判断是否是测试网络
				else -> fragment.activity?.jump<SplashActivity>()
			}
		}
	}

	private fun showConfirmationAlertView(content: String, callback: () -> Unit) {
		fragment.context?.showAlertView(
			"Switch Chain Network",
			WalletSettingsText.switchChainNetAlert(content),
			false,
			{} // Cancel Event
		) {
			callback()
		}
	}

	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<ProfileOverlayFragment> {
			showAddButton(true) {
				presenter.showWalletAddingMethodDashboard()
			}
		}
	}
}