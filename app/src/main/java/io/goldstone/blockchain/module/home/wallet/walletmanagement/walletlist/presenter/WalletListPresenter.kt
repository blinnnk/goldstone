package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.jump
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
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
		updateAllWalletBalance {
			fragment.asyncData = this
		}
	}

	fun switchWallet(address: String) {
		WalletTable.switchCurrentWallet(address) { it ->
			if (it.isNull()) return@switchCurrentWallet
			val walletType = it?.getWalletType()!!
			when {
				walletType.isBTC() -> {
					if (Config.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Mainnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isBTCTest() -> {
					if (!Config.isTestEnvironment()) {
						showConfirmationAlertView("Bitcoin Testnet") {
							NodeSelectionPresenter.setAllTestnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isLTC() -> {
					if (Config.isTestEnvironment()) {
						showConfirmationAlertView("Litecoin Mainnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isBCH() -> {
					if (Config.isTestEnvironment()) {
						showConfirmationAlertView(" Bitcoin Cash Mainnet") {
							NodeSelectionPresenter.setAllMainnet {
								fragment.activity?.jump<SplashActivity>()
							}
						}
					} else fragment.activity?.jump<SplashActivity>()
				}

				walletType.isBIP44() -> {
					if (Config.isTestEnvironment()) {
						NodeSelectionPresenter.setAllTestnet {
							fragment.activity?.jump<SplashActivity>()
						}
					} else {
						NodeSelectionPresenter.setAllMainnet {
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
			false
		) {
			callback()
		}
	}

	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.showCloseButton(false)
			overlayView.header.showBackButton(true) {
				headerTitle = WalletSettingsText.walletSettings
				presenter.showWalletSettingListFragment()
			}
		}
		fragment.getParentFragment<ProfileOverlayFragment> {
			overlayView.header.showAddButton(true) {
				presenter.showWalletAddingMethodDashboard()
			}
		}
	}

	private fun updateAllWalletBalance(hold: ArrayList<WalletListModel>.() -> Unit) {
		val data = ArrayList<WalletListModel>()
		// 获取全部本机钱包
		WalletTable.getAll all@{
			// 获取全部本地记录的 `Token` 信息
			DefaultTokenTable.getCurrentChainTokens { allTokens ->
				object : ConcurrentAsyncCombine() {
					override var asyncCount = size
					override fun concurrentJobs() {
						this@all.forEach { wallet ->
							// 获取对应的钱包下的全部 `token`
							MyTokenTable.getTokensByAddress(wallet.getCurrentAddresses()) { myTokens ->
								val targetWalletType = wallet.getWalletType()
								if (myTokens.isEmpty()) {
									data.add(WalletListModel(wallet, 0.0, targetWalletType.type!!))
									completeMark()
								} else {
									val balance = myTokens.sumByDouble { walletToken ->
										val thisToken = allTokens.find {
											it.contract.equals(walletToken.contract, true)
										}!!
										walletToken.balance * thisToken.price
									}

									// 计算当前钱包下的 `token` 对应的货币总资产
									WalletListModel(wallet, balance, targetWalletType.type!!).let {
										data.add(it)
										completeMark()
									}
								}
							}
						}
					}

					// 因为结果集是在异步状态下准备, 返回的数据按照 `id` 重新排序
					override fun mergeCallBack() = hold(data.sortedByDescending { it.id }.toArrayList())
				}.start()
			}
		}
	}
}