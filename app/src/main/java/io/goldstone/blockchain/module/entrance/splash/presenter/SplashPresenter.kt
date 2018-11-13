package io.goldstone.blockchain.module.entrance.splash.presenter

import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orElse
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable.Companion.initEOSAccountName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.currentPublicKeyIsActivated
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.hasActivatedOrWatchOnly
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.File

/**
 * @date 30/03/2018 2:21 AM
 * @author KaySaith
 */
class SplashPresenter(val activity: SplashActivity) {

	// 获取当前的汇率
	@WorkerThread
	fun updateCurrencyRateFromServer(config: AppConfigTable) {
		SharedWallet.updateCurrencyCode(config.currencyCode)
		GoldStoneAPI.getCurrencyRate(config.currencyCode) { rate, error ->
			if (rate != null && error.isNone()) {
				// 更新 `SharePreference` 中的值
				SharedWallet.updateCurrentRate(rate)
				// 更新数据库的值
				GoldStoneDataBase.database.currencyDao().updateUsedRate(rate)
			}
		}
	}

	fun initDefaultToken(@WorkerThread callback: () -> Unit) {
		// if there isn't network init local token list
		val localDefaultTokens =
			GoldStoneDataBase.database.defaultTokenDao().getAllTokens()
		// 先判断是否插入本地的 `JSON` 数据
		if (localDefaultTokens.isEmpty()) {
			StartingPresenter.insertLocalTokens(activity, callback)
		} else {
			callback()
		}
	}

	fun initDefaultMarketByNetWork() {
		doAsync {
			GoldStoneDataBase.database.exchangeTableDao().getAll().isEmpty() isTrue {
				StartingPresenter.insertLocalMarketList(activity) {
					updateMarketListByNetWork()
				}
			} otherwise {
				updateMarketListByNetWork()
			}
		}

	}

	fun initNodeList(@WorkerThread callback: () -> Unit) {
		val localNode =
			GoldStoneDataBase.database.chainNodeDao().getAll()
		if (localNode.isEmpty()) {
			StartingPresenter.insertLocalNodeList(activity, callback)
		} else callback()
	}

	fun initSupportCurrencyList(@WorkerThread callback: () -> Unit) {
		val localCurrencies =
			GoldStoneDataBase.database.currencyDao().getSupportCurrencies()
		if (localCurrencies.isEmpty()) {
			StartingPresenter.insertLocalCurrency(activity, callback)
		} else callback()
	}


	// 因为密钥都存储在本地的 `Keystore File` 文件里面, 当升级数据库 `FallBack` 数据的情况下
	// 需要也同时清理本地的 `Keystore File`
	fun cleanWhenUpdateDatabaseOrElse(@WorkerThread callback: (allWallets: List<WalletTable>) -> Unit) {
		doAsync {
			val allWallets = GoldStoneDataBase.database.walletDao().getAllWallets()
			if (allWallets.isEmpty()) {
				cleanKeyStoreFile(activity.filesDir)
				unregisterGoldStoneID(SharedWallet.getGoldStoneID())
			} else {
				val needUnregister =
					!SharedWallet.getNeedUnregisterGoldStoneID().equals("Default", true)
				if (needUnregister) {
					unregisterGoldStoneID(SharedWallet.getNeedUnregisterGoldStoneID())
				}
			}
			callback(allWallets)
		}
	}

	private fun unregisterGoldStoneID(targetGoldStoneID: String) {
		if (NetworkUtil.hasNetwork(activity)) {
			GoldStoneAPI.unregisterDevice(targetGoldStoneID) { isSuccessful, error ->
				if (!isSuccessful.isNull() && error.isNone()) {
					// 服务器操作失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`, 成功的话清空.
					val newID = if (isSuccessful == true) "Default" else targetGoldStoneID
					SharedWallet.updateUnregisterGoldStoneID(newID)
				} else {
					// 出现请求错误标记 `Clean` 失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`
					SharedWallet.updateUnregisterGoldStoneID(targetGoldStoneID)
				}
			}
		} else {
			// 没有网络的情况下标记 `Clean` 失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`
			SharedWallet.updateUnregisterGoldStoneID(targetGoldStoneID)
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

	private fun updateMarketListByNetWork() {
		NetworkUtil.hasNetwork(activity) isTrue {
			// update local exchangeTable info list
			StartingPresenter.getAndUpdateExchangeTables { _, _ -> }
		}
	}

	companion object {
		@WorkerThread
		fun updateAccountInformation(context: Context, @UiThread callback: () -> Unit) {
			val currentWallet =
				GoldStoneDataBase.database.walletDao().findWhichIsUsing(true) ?: return
			if (
				!currentWallet.eosAccountNames.currentPublicKeyIsActivated() &&
				!currentWallet.eosAccountNames.hasActivatedOrWatchOnly() &&
				currentWallet.getCurrentBip44Addresses().any { it.getChainType().isEOS() }
			) {
				if (NetworkUtil.hasNetwork(context)) {
					currentWallet.checkOrUpdateEOSAccount(context, callback)
					SharedValue.updateAccountCheckedStatus(true)
				} else {
					// 符合需要检测 Account 条件但是因为没有网络而跳过的情况需要标记
					// 在网络 Service 检测到网络恢复的时候需要根据这个标记重新检测
					SharedValue.updateAccountCheckedStatus(false)
					cacheDataAndSetNetBy(currentWallet, callback)
				}
			} else {
				// 账户不符合需要检测的条件的时候也比较为已经检测过了
				SharedValue.updateAccountCheckedStatus(true)
				cacheDataAndSetNetBy(currentWallet, callback)
			}
		}

		private fun WalletTable.checkOrUpdateEOSAccount(
			context: Context,
			@UiThread callback: () -> Unit
		) {
			// 观察钱包的时候会把 account name 存成 address 当删除钱包检测到下一个默认钱包
			// 刚好是 EOS 观察钱包的时候越过检查 Account Name 的缓解
			val isEOSWatchOnly = EOSAccount(currentEOSAddress).isValid(false)
			if (isEOSWatchOnly) cacheDataAndSetNetBy(this, callback)
			else EOSAPI.getAccountNameByPublicKey(currentEOSAddress) { accounts, error ->
				if (accounts != null && error.isNone()) {
					if (accounts.isEmpty()) cacheDataAndSetNetBy(this, callback)
					else initEOSAccountName(accounts) {
						// 如果是含有 `DefaultName` 的钱包需要更新临时缓存钱包的内的值
						cacheDataAndSetNetBy(
							apply { currentEOSAccountName.updateCurrent(accounts.first().name) },
							callback
						)
					}
				} else launchUI {
					val title = "Check EOS Account Name Error"
					val subtitle = error.message
					context.showAlertView(
						title,
						subtitle,
						false,
						{ cacheDataAndSetNetBy(this, callback) }
					) {
						cacheDataAndSetNetBy(this, callback)
					}
				}
			}
		}

		@WorkerThread
		private fun cacheWalletData(wallet: WalletTable, @UiThread callback: () -> Unit) {
			wallet.apply {
				SharedAddress.updateCurrentEthereum(currentETHSeriesAddress)
				SharedAddress.updateCurrentBTC(currentBTCAddress)
				SharedAddress.updateCurrentBTCSeriesTest(currentBTCSeriesTestAddress)
				SharedAddress.updateCurrentETC(currentETCAddress)
				SharedAddress.updateCurrentLTC(currentLTCAddress)
				SharedAddress.updateCurrentBCH(currentBCHAddress)
				SharedAddress.updateCurrentEOS(currentEOSAddress)
				SharedAddress.updateCurrentEOSName(currentEOSAccountName.getCurrent())
				SharedWallet.updateCurrentIsWatchOnlyOrNot(isWatchOnly)
				SharedWallet.updateCurrentWalletID(avatarID)
				SharedWallet.updateCurrentBalance(balance.orElse(0.0))
				SharedWallet.updateCurrentName(name)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}

		private fun cacheDataAndSetNetBy(
			wallet: WalletTable,
			@UiThread callback: () -> Unit
		) {
			val type = wallet.getWalletType()
			type.updateSharedPreference()
			when {
				type.isBTCTest() -> NodeSelectionPresenter.setAllTestnet {
					cacheWalletData(wallet, callback)
				}
				type.isBTC() -> NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				type.isLTC() -> NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				type.isEOSJungle() -> NodeSelectionPresenter.setAllTestnet {
					cacheWalletData(wallet, callback)
				}
				type.isEOSMainnet() -> NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				type.isEOS() -> if (SharedValue.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
					cacheWalletData(wallet, callback)
				} else NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				type.isBCH() -> NodeSelectionPresenter.setAllMainnet {
					cacheWalletData(wallet, callback)
				}
				type.isETHSeries() -> {
					if (SharedValue.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
						cacheWalletData(wallet, callback)
					} else NodeSelectionPresenter.setAllMainnet {
						cacheWalletData(wallet, callback)
					}
				}
				type.isBIP44() || type.isMultiChain() -> {
					if (SharedValue.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
						cacheWalletData(wallet, callback)
					} else NodeSelectionPresenter.setAllMainnet {
						cacheWalletData(wallet, callback)
					}
				}
			}
		}
	}
}