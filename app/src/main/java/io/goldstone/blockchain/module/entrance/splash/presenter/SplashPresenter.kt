package io.goldstone.blockchain.module.entrance.splash.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.orElse
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable.Companion.updateEOSAccountName
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

/**
 * @date 30/03/2018 2:21 AM
 * @author KaySaith
 */
class SplashPresenter(val activity: SplashActivity) {

	fun hasAccountThenLogin() {
		WalletTable.getCurrentWallet {
			if (eosAccountNames.isEmpty()) {
				EOSAPI.getAccountNameByPublicKey(
					currentEOSAddress,
					{ activity.alert("${it.message}") }
				) { accounts ->
					updateEOSAccountName(accounts) { hasDefaultName ->
						cacheDataAndSetNetBy(
							// 如果是含有 DefaultName 的钱包需要更新临时缓存钱包的内的值
							this.apply { if (hasDefaultName) currentEOSAccountName.updateCurrent(accounts.first().name) }
						) {
							activity.jump<MainActivity>()
						}
					}
				}
			} else cacheDataAndSetNetBy(this) {
				activity.jump<MainActivity>()
			}
		}
	}

	private fun cacheDataAndSetNetBy(
		wallet: WalletTable,
		callback: () -> Unit
	) {
		val type = wallet.getTargetWalletType()
		when (type) {
			WalletType.BTCTestOnly -> NodeSelectionPresenter.setAllTestnet {
				cacheWalletData(wallet, callback)
				Config.updateCurrentWalletType(WalletType.BTCTestOnly.content)
			}
			WalletType.BTCOnly -> NodeSelectionPresenter.setAllMainnet {
				cacheWalletData(wallet, callback)
				Config.updateCurrentWalletType(WalletType.BTCOnly.content)
			}

			WalletType.LTCOnly -> NodeSelectionPresenter.setAllMainnet {
				cacheWalletData(wallet, callback)
				Config.updateCurrentWalletType(WalletType.LTCOnly.content)
			}

			WalletType.EOSOnly -> NodeSelectionPresenter.setAllMainnet {
				cacheWalletData(wallet, callback)
				Config.updateCurrentWalletType(WalletType.EOSOnly.content)
			}

			WalletType.BCHOnly -> NodeSelectionPresenter.setAllMainnet {
				cacheWalletData(wallet, callback)
				Config.updateCurrentWalletType(WalletType.BCHOnly.content)
			}

			WalletType.ETHERCAndETCOnly -> {
				if (Config.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
					cacheWalletData(wallet, callback)
				} else NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				Config.updateCurrentWalletType(WalletType.ETHERCAndETCOnly.content)
			}

			WalletType.Bip44MultiChain -> {
				if (Config.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
					cacheWalletData(wallet, callback)
				} else NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				Config.updateCurrentWalletType(WalletType.Bip44MultiChain.content)
			}

			WalletType.MultiChain -> {
				if (Config.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
					cacheWalletData(wallet, callback)
				} else NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				Config.updateCurrentWalletType(WalletType.MultiChain.content)
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

	// 获取当前的汇率
	fun updateCurrencyRateFromServer(config: AppConfigTable) {
		doAsync {
			Config.updateCurrencyCode(config.currencyCode)
			GoldStoneAPI.getCurrencyRate(
				config.currencyCode,
				{
					LogUtil.error("Request of get currency rate has error")
				}
			) {
				// 更新 `SharePreference` 中的值
				Config.updateCurrentRate(it)
				// 更新数据库的值
				SupportCurrencyTable.updateUsedRateValue(it)
			}
		}
	}

	// 因为密钥都存储在本地的 `Keystore File` 文件里面, 当升级数据库 `FallBack` 数据的情况下
	// 需要也同时清理本地的 `Keystore File`
	fun cleanWhenUpdateDatabaseOrElse(callback: () -> Unit) {
		WalletTable.getAll {
			if (isEmpty()) {
				cleanKeyStoreFile(activity.filesDir)
				unregisterGoldStoneID(Config.getGoldStoneID())
			} else {
				val needUnregister =
					!Config.getNeedUnregisterGoldStoneID().equals("Default", true)
				if (needUnregister) {
					unregisterGoldStoneID(Config.getNeedUnregisterGoldStoneID())
				}
			}
			callback()
		}
	}

	private fun unregisterGoldStoneID(targetGoldStoneID: String) {
		if (NetworkUtil.hasNetwork(activity)) {
			GoldStoneAPI.unregisterDevice(
				targetGoldStoneID,
				{
					// 出现请求错误标记 `Clean` 失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`
					Config.updateUnregisterGoldStoneID(targetGoldStoneID)
					LogUtil.error("unregisterDevice", it)
				}
			) { isSuccessful ->
				// 服务器操作失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`, 成功的话清空.
				val newID = if (isSuccessful) "Default" else targetGoldStoneID
				Config.updateUnregisterGoldStoneID(newID)
			}
		} else {
			// 没有网络的情况下标记 `Clean` 失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`
			Config.updateUnregisterGoldStoneID(targetGoldStoneID)
		}
	}

	private fun cleanKeyStoreFile(dir: File): Boolean {
		if (dir.isDirectory) {
			val children = dir.list()
			for (index in children.indices) {
				val success = cleanKeyStoreFile(File(dir, children[index]))
				if (!success) return false
			}
		}
		// The directory is now empty so delete it
		return dir.delete()
	}

	private fun cacheWalletData(wallet: WalletTable, callback: () -> Unit) {
		wallet.apply {
			doAsync {
				Config.updateCurrentEthereumAddress(currentETHAndERCAddress)
				Config.updateCurrentBTCAddress(currentBTCAddress)
				Config.updateCurrentBTCSeriesTestAddress(currentBTCSeriesTestAddress)
				Config.updateCurrentETCAddress(currentETCAddress)
				Config.updateCurrentLTCAddress(currentLTCAddress)
				Config.updateCurrentBCHAddress(currentBCHAddress)
				Config.updateCurrentEOSAddress(currentEOSAddress)
				Config.updateCurrentEOSName(currentEOSAccountName.getCurrent())
				Config.updateCurrentIsWatchOnlyOrNot(isWatchOnly)
				Config.updateCurrentWalletID(id)
				Config.updateCurrentBalance(balance.orElse(0.0))
				Config.updateCurrentName(name)
				uiThread { callback() }
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
			
			// update local exchangeTable info list
			StartingPresenter.updateExchangesTables ( {
				LogUtil.error(activity::javaClass.name)
			}) {}
		}
	}
}