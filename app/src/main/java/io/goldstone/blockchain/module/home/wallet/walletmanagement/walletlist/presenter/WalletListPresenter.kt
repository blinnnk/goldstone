package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.jump
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
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
			it?.apply {
				WalletTable.getTargetWalletType(this).let {
					when (it) {
						WalletType.BTCOnly -> {
							if (Config.isTestEnvironment()) {
								showConfirmationAlertView("Bitcoin Mainnet") {
									NodeSelectionPresenter.setAllMainnet {
										fragment.activity?.jump<SplashActivity>()
									}
								}
							} else fragment.activity?.jump<SplashActivity>()
						}

						WalletType.BTCTestOnly -> {
							if (!Config.isTestEnvironment()) {
								showConfirmationAlertView("Bitcoin Testnet") {
									NodeSelectionPresenter.setAllTestnet {
										fragment.activity?.jump<SplashActivity>()
									}
								}
							} else fragment.activity?.jump<SplashActivity>()
						}

						WalletType.LTCOnly -> {
							if (Config.isTestEnvironment()) {
								showConfirmationAlertView("Litecoin Mainnet") {
									NodeSelectionPresenter.setAllMainnet {
										fragment.activity?.jump<SplashActivity>()
									}
								}
							} else fragment.activity?.jump<SplashActivity>()
						}

						WalletType.BCHOnly -> {
							if (Config.isTestEnvironment()) {
								showConfirmationAlertView(" Bitcoin Cash Mainnet") {
									NodeSelectionPresenter.setAllMainnet {
										fragment.activity?.jump<SplashActivity>()
									}
								}
							} else fragment.activity?.jump<SplashActivity>()
						}

						WalletType.MultiChain -> {
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
						else -> fragment.activity?.jump<SplashActivity>()
					}
				}
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
							MyTokenTable.getMyTokensByAddress(WalletTable.getAddressesByWallet(wallet)) { myTokens ->
								WalletTable.getTargetWalletType(wallet).let { walletType ->
									if (myTokens.isEmpty()) {
										data.add(WalletListModel(wallet, 0.0, walletType.content))
										completeMark()
									} else {
										val balance = myTokens.sumByDouble { walletToken ->
											val thisToken = allTokens.find {
												it.contract.equals(walletToken.contract, true)
											}!!
											CryptoUtils.toCountByDecimal(
												walletToken.balance,
												thisToken.decimals
											) * thisToken.price
										}

										// 计算当前钱包下的 `token` 对应的货币总资产
										WalletListModel(wallet, balance, walletType.content).let {
											data.add(it)
											completeMark()
										}
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