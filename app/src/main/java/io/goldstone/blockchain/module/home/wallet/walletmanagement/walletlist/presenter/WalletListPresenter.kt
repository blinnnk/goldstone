package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.jump
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
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
		WalletTable.switchCurrentWallet(address) {
			fragment.activity?.jump<SplashActivity>()
		}
	}
	
	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.showAddButton(false)
			overlayView.header.showBackButton(true) {
				headerTitle = WalletSettingsText.walletSettings
				presenter.showWalletSettingListFragment()
			}
		}
	}
	
	private fun updateAllWalletBalance(hold: ArrayList<WalletListModel>.() -> Unit) {
		val data = ArrayList<WalletListModel>()
		// 获取全部本机钱包
		WalletTable.getAll {
			// 获取全部本地记录的 `Token` 信息
			DefaultTokenTable.getCurrentChainTokens { allTokens ->
				object : ConcurrentAsyncCombine() {
					override var asyncCount = size
					override fun concurrentJobs() {
						forEach { wallet ->
							// 获取对应的钱包下的全部 `token`
							MyTokenTable.getCurrentChainTokensWithAddress(wallet.address) {
								if (it.isEmpty()) {
									data.add(WalletListModel(wallet, 0.0))
									completeMark()
								} else {
									// 计算当前钱包下的 `token` 对应的货币总资产
									WalletListModel(wallet, it.sumByDouble { walletToken ->
										val thisToken = allTokens.find {
											it.contract.equals(walletToken.contract, true)
										}!!
										CryptoUtils.toCountByDecimal(
											walletToken.balance,
											thisToken.decimals
										) * thisToken.price
									}).let {
										data.add(it)
										completeMark()
									}
								}
							}
						}
					}
					
					override fun mergeCallBack() {
						// 因为结果集是在异步状态下准备, 返回的数据按照 `id` 重新排序
						hold(data.sortedByDescending { it.id }.toArrayList())
					}
				}.start()
			}
		}
	}
}