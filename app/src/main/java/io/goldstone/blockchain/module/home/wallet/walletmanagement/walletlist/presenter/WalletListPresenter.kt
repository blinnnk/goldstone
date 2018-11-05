package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.jump
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
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view.WalletListFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */
class WalletListPresenter(
	override val fragment: WalletListFragment
) : BaseRecyclerPresenter<WalletListFragment, WalletListModel>() {

	override fun updateData() {
		WalletTable.getAll {
			fragment.asyncData = mapTo(arrayListOf()) { wallet ->
				WalletListModel(wallet, wallet.getWalletType().type.orEmpty())
			}
		}
	}

	fun switchWallet(address: String) {
		WalletTable.switchCurrentWallet(address) { it ->
			val walletType = it.getWalletType()
			SharedWallet.updateCurrentIsWatchOnlyOrNot(it.isWatchOnly).let {
			}
			when {
				walletType.isBTC() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Mainnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isBTCTest() -> {
					if (!SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Testnet") {
							NodeSelectionPresenter.setAllTestnet(true) {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isLTC() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Litecoin Mainnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isBCH() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Cash Mainnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isEOSJungle() -> {
					if (!SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("EOS Jungle Testnet") {
							NodeSelectionPresenter.setAllTestnet(true) {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isEOSMainnet() -> {
					if (SharedValue.isTestEnvironment()) {
						showConfirmationAlertView("EOS Mainnet Testnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isBIP44() -> {
					if (SharedValue.isTestEnvironment()) {
						NodeSelectionPresenter.setAllTestnet(true) {
							fragment.activity?.jump<SplashActivity>()
						}
					} else {
						NodeSelectionPresenter.setAllMainnet(true) {
							fragment.activity?.jump<SplashActivity>()
						}
					}
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
			{ callback() } // Cancel Event
		) {
			callback()
		}
	}

	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.showBackButton(true) {
				presenter.showWalletSettingListFragment()
			}
		}
		fragment.getParentFragment<ProfileOverlayFragment> {
			overlayView.header.showAddButton(true) {
				presenter.showWalletAddingMethodDashboard()
			}
		}
	}
}