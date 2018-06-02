package io.goldstone.blockchain.module.entrance.splash.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 30/03/2018 2:21 AM
 * @author KaySaith
 */
class SplashPresenter(val activity: SplashActivity) {
	
	fun hasAccountThenLogin() {
		WalletTable.getAll {
			isNotEmpty() isTrue {
				WalletTable.getCurrentWallet {
					it?.apply {
						WalletTable.current = it
						activity.jump<MainActivity>()
					}
				}
			}
		}
	}
	
	fun initDefaultTokenByNetWork(callback: () -> Unit) {
		// if there isn't network init local token list
		DefaultTokenTable.getAllTokens {
			// 先判断是否插入本地的 `JSON` 数据
			it.isEmpty() isTrue {
				StartingPresenter.insertLocalTokens(activity) {
					updateLocalTokensByNetWork()
					callback()
				}
			} otherwise {
				updateLocalTokensByNetWork()
				callback()
			}
		}
	}
	
	fun initSupportCurrencyList(callback: () -> Unit) {
		SupportCurrencyTable.getSupportCurrencies {
			it.isEmpty() isTrue {
				StartingPresenter.insertLocalCurrency(activity, callback)
			} otherwise {
				callback()
			}
		}
	}
	
	private fun updateLocalTokensByNetWork() {
		// 每次有网络的时候都插入或更新网络数据
		NetworkUtil.hasNetwork(activity) isTrue {
			// update local `Tokens` info list
			StartingPresenter.updateLocalDefaultTokens {
				LogUtil.error(activity::javaClass.name)
			}
		}
	}
}