package io.goldstone.blockchain.module.entrance.splash.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.orElse
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable.Companion.initEOSAccountName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.currentPublicKeyHasActivated
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.hasActivatedOrWatchOnlyEOSAccount
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
			if (
				!eosAccountNames.currentPublicKeyHasActivated() &&
				!eosAccountNames.hasActivatedOrWatchOnlyEOSAccount() &&
				getCurrentBip44Addresses().any { it.getChainType().isEOS() }
			) {
				checkOrUpdateEOSAccount()
			} else cacheDataAndSetNetBy(this) { activity.jump<MainActivity>() }
		}
	}

	// 获取当前的汇率
	fun updateCurrencyRateFromServer(config: AppConfigTable) {
		doAsync {
			SharedWallet.updateCurrencyCode(config.currencyCode)
			GoldStoneAPI.getCurrencyRate(
				config.currencyCode,
				{
					LogUtil.error("Request of get currency rate has error")
				}
			) {
				// 更新 `SharePreference` 中的值
				SharedWallet.updateCurrentRate(it)
				// 更新数据库的值
				SupportCurrencyTable.updateUsedRateValue(it)
			}
		}
	}

	private fun WalletTable.checkOrUpdateEOSAccount() {
		EOSAPI.getAccountNameByPublicKey(
			currentEOSAddress,
			{
				activity.alert(it.message)
				activity.jump<MainActivity>()
			}
		) { accounts ->
			if (accounts.isEmpty()) cacheDataAndSetNetBy(this) {
				activity.jump<MainActivity>()
			} else initEOSAccountName(accounts) {
				// 如果是含有 `DefaultName` 的钱包需要更新临时缓存钱包的内的值
				cacheDataAndSetNetBy(apply { currentEOSAccountName.updateCurrent(accounts.first().name) }) {
					activity.jump<MainActivity>()
				}
			}
		}
	}

	private fun cacheDataAndSetNetBy(wallet: WalletTable, callback: () -> Unit) {
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
			type.isEOS() -> if (SharedValue.isTestEnvironment()) NodeSelectionPresenter.setAllTestnet {
				cacheWalletData(wallet, callback)
			} else NodeSelectionPresenter.setAllMainnet {
				cacheWalletData(wallet, callback)
			}
			type.isEOSJungle() -> NodeSelectionPresenter.setAllTestnet {
				cacheWalletData(wallet, callback)
			}
			type.isEOSMainnet() -> NodeSelectionPresenter.setAllMainnet {
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


	// 因为密钥都存储在本地的 `Keystore File` 文件里面, 当升级数据库 `FallBack` 数据的情况下
	// 需要也同时清理本地的 `Keystore File`
	fun cleanWhenUpdateDatabaseOrElse(@UiThread callback: (allWallets: List<WalletTable>) -> Unit) {
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
			uiThread { callback(allWallets) }
		}
	}

	private fun unregisterGoldStoneID(targetGoldStoneID: String) {
		if (NetworkUtil.hasNetwork(activity)) {
			GoldStoneAPI.unregisterDevice(
				targetGoldStoneID,
				{
					// 出现请求错误标记 `Clean` 失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`
					SharedWallet.updateUnregisterGoldStoneID(targetGoldStoneID)
					LogUtil.error("unregisterDevice", it)
				}
			) { isSuccessful ->
				// 服务器操作失败, 在数据库标记下次需要恢复清理的 `GoldStone ID`, 成功的话清空.
				val newID = if (isSuccessful) "Default" else targetGoldStoneID
				SharedWallet.updateUnregisterGoldStoneID(newID)
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

	private fun cacheWalletData(wallet: WalletTable, callback: () -> Unit) {
		wallet.apply {
			doAsync {
				SharedAddress.updateCurrentEthereum(currentETHSeriesAddress)
				SharedAddress.updateCurrentBTC(currentBTCAddress)
				SharedAddress.updateCurrentBTCSeriesTest(currentBTCSeriesTestAddress)
				SharedAddress.updateCurrentETC(currentETCAddress)
				SharedAddress.updateCurrentLTC(currentLTCAddress)
				SharedAddress.updateCurrentBCH(currentBCHAddress)
				SharedAddress.updateCurrentEOS(currentEOSAddress)
				SharedAddress.updateCurrentEOSName(currentEOSAccountName.getCurrent())
				SharedWallet.updateCurrentIsWatchOnlyOrNot(isWatchOnly)
				SharedWallet.updateCurrentWalletID(id)
				SharedWallet.updateCurrentBalance(balance.orElse(0.0))
				SharedWallet.updateCurrentName(name)
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
		}
	}
}