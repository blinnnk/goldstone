package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.jump
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.multichain.ChainID
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
@Suppress("DEPRECATION")
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

	// EOS 的 测试网没有办法直接用 主网 和 测试网两个状态区分. 所以需要额外单独传递
	// EOS ChainID 作为标识
	private fun switchWalletInDatabase(address: String, isMainnet: Boolean) {
		WalletTable.switchCurrentWallet(address) { it ->
			SharedWallet.updateCurrentIsWatchOnlyOrNot(it.isWatchOnly)
			if (isMainnet) NodeSelectionPresenter.setAllMainnet(true) {
				SharedValue.updateIsTestEnvironment(false)
				Runtime.getRuntime().gc()
				fragment.activity?.jump<SplashActivity>()
			} else NodeSelectionPresenter.setAllTestnet(true) {
				SharedValue.updateIsTestEnvironment(true)
				Runtime.getRuntime().gc()
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
						showMainnetConfirmationAlertView(WalletText.btcMainnet) {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
					}
				}

				walletType.isBTCTest() -> {
					if (!SharedValue.isTestEnvironment()) {
						showTestnetConfirmationAlertView(WalletText.btcTestnet) {
							switchWalletInDatabase(address, false)
						}
					} else {
						switchWalletInDatabase(address, false)
					}
				}

				walletType.isLTC() -> {
					if (SharedValue.isTestEnvironment()) {
						showMainnetConfirmationAlertView(WalletText.ltcMainnet) {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
					}
				}

				walletType.isBCH() -> {
					if (SharedValue.isTestEnvironment()) {
						showMainnetConfirmationAlertView(WalletText.bchMainnet) {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
					}
				}

				walletType.isEOSJungle() -> {
					if (!SharedValue.isTestEnvironment() || SharedWallet.getCurrentWalletType().isEOSKylin()) {
						showTestnetConfirmationAlertView(WalletText.eosJungle) {
							switchWalletInDatabase(address, false)
						}
					} else {
						switchWalletInDatabase(address, false)
					}
				}

				walletType.isEOSKylin() -> {
					if (!SharedValue.isTestEnvironment() || SharedWallet.getCurrentWalletType().isEOSJungle()) {
						showTestnetConfirmationAlertView(WalletText.eosKylin) {
							switchWalletInDatabase(address, false)
						}
					} else {
						switchWalletInDatabase(address, false)
					}
				}

				walletType.isEOSMainnet() -> {
					if (SharedValue.isTestEnvironment()) {
						showTestnetConfirmationAlertView(WalletText.eosMainnet) {
							switchWalletInDatabase(address, true)
						}
					} else {
						switchWalletInDatabase(address, true)
					}
				}
				// 观察钱包只导入地址就会是这个属性
				walletType.isEOS() -> {
					switchWalletInDatabase(address, true)
				}

				walletType.isETHSeries() -> {
					switchWalletInDatabase(address, true)
				}

				walletType.isBIP44() || walletType.isMultiChain() -> {
					if (SharedValue.isTestEnvironment())
						switchWalletInDatabase(address, false)
					else switchWalletInDatabase(address, true)
				}
			}
		}
	}

	private fun showMainnetConfirmationAlertView(content: String, callback: () -> Unit) {
		Dashboard(fragment.context!!) {
			showAlertView(
				WalletSettingsText.switchChainNetAlertTitle,
				WalletSettingsText.switchChainNetToMainAlert(content),
				false
			) {
				callback()
			}
		}
	}

	private fun showTestnetConfirmationAlertView(content: String, callback: () -> Unit) {
		Dashboard(fragment.context!!) {
			showAlertView(
				WalletSettingsText.switchChainNetAlertTitle,
				WalletSettingsText.switchChainNetToTestAlert(content),
				false
			) {
				callback()
			}
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