package io.goldstone.blockchain.module.entrance.splash.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orElse
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.language.currentLanguage
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.kernel.commontable.SupportCurrencyTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable.Companion.initEOSAccountName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.currentPublicKeyIsActivated
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.hasActivatedOrWatchOnly
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import java.io.File

/**
 * @date 30/03/2018 2:21 AM
 * @author KaySaith
 */
class SplashPresenter(val activity: SplashActivity) {

	@WorkerThread
	fun initDefaultToken(context: Context) {
		// 先判断是否插入本地的 `JSON` 数据
		if (DefaultTokenTable.dao.rowCount() == 0) {
			val localDefaultTokens =
				context.convertLocalJsonFileToJSONObjectArray(R.raw.local_token_list).map { DefaultTokenTable(it) }
			DefaultTokenTable.dao.insertAll(localDefaultTokens)
		}
	}

	@WorkerThread
	fun initDefaultExchangeData(context: Context) {
		if (ExchangeTable.dao.rowCount() == 0) {
			val localData =
				context.convertLocalJsonFileToJSONObjectArray(R.raw.local_market_list).map { ExchangeTable(it) }
			ExchangeTable.dao.insertAll(localData)
		}
	}

	@WorkerThread
	fun initNodeList(context: Context, callback: () -> Unit) {
		if (ChainNodeTable.dao.rowCount() == 0) {
			val localData =
				context.convertLocalJsonFileToJSONObjectArray(R.raw.node_list).map { ChainNodeTable(it) }
			ChainNodeTable.dao.insertAll(localData)
			callback()
		} else callback()
	}

	@WorkerThread
	fun initSupportCurrencyList(context: Context) {
		if (SupportCurrencyTable.dao.rowCount() == 0) {
			val localCurrency =
				context.convertLocalJsonFileToJSONObjectArray(R.raw.support_currency_list).map {
					SupportCurrencyTable(it).apply {
						// 初始化的汇率显示本地 `Json` 中的值, 之后是通过网络更新
						if (currencySymbol.equals(CountryCode.currentCurrency, true)) {
							isUsed = true
							SharedWallet.updateCurrentRate(rate)
						}
					}
				}
			SupportCurrencyTable.dao.insertAll(localCurrency)
		}
	}


	// 因为密钥都存储在本地的 `Keystore File` 文件里面, 当升级数据库 `FallBack` 数据的情况下
	// 需要也同时清理本地的 `Keystore File`
	@WorkerThread
	fun cleanWhenUpdateDatabaseOrElse(callback: () -> Unit) {
		val walletCount = WalletTable.dao.rowCount()
		if (walletCount == 0) {
			cleanKeyStoreFile(activity.filesDir)
			unregisterGoldStoneID(SharedWallet.getGoldStoneID())
		} else {
			val needUnregister =
				!SharedWallet.getNeedUnregisterGoldStoneID().equals("Default", true)
			if (needUnregister) {
				unregisterGoldStoneID(SharedWallet.getNeedUnregisterGoldStoneID())
			}
		}
		callback()
	}

	/**
	 * Querying the language type of the current account
	 * set and displaying the interface from the database.
	 */
	fun initLaunchLanguage(code: Int) {
		currentLanguage = code
		SharedWallet.updateCurrentLanguageCode(code)
	}

	private fun unregisterGoldStoneID(targetGoldStoneID: String) {
		if (NetworkUtil.hasNetwork()) {
			GoldStoneAPI.unregisterDevice(targetGoldStoneID) { isSuccessful, error ->
				if (isSuccessful.isNotNull() && error.isNone()) {
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

	companion object {
		@WorkerThread
		fun updateAccountInformation(context: Context, callback: () -> Unit) {
			val currentWallet = WalletTable.dao.findWhichIsUsing(true) ?: return
			if (
				!currentWallet.eosAccountNames.currentPublicKeyIsActivated() &&
				!currentWallet.eosAccountNames.hasActivatedOrWatchOnly() &&
				currentWallet.getCurrentBip44Addresses().any { it.getChainType().isEOS() }
			) {
				if (NetworkUtil.hasNetwork()) {
					checkOrUpdateEOSAccount(context, currentWallet, callback)
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

		@WorkerThread
		fun checkOrUpdateEOSAccount(context: Context, wallet: WalletTable, callback: () -> Unit) {
			// 观察钱包的时候会把 account name 存成 address 当删除钱包检测到下一个默认钱包
			// 刚好是 EOS 观察钱包的时候越过检查 Account Name 的缓解
			val isEOSWatchOnly =
				EOSAccount(wallet.currentEOSAddress).isValid(false)
			if (isEOSWatchOnly) cacheDataAndSetNetBy(wallet, callback)
			else EOSAPI.getAccountNameByPublicKey(wallet.currentEOSAddress) { accounts, error ->
				if (accounts.isNotNull() && error.isNone()) {
					if (accounts.isEmpty()) cacheDataAndSetNetBy(wallet, callback)
					else initEOSAccountName(accounts) {
						// 如果是含有 `DefaultName` 的钱包需要更新临时缓存钱包的内的值
						cacheDataAndSetNetBy(
							wallet.apply { currentEOSAccountName.updateCurrent(accounts.first().name) },
							callback
						)
					}
				} else launchUI {
					val title = "Check EOS Account Name Error"
					val subtitle = error.message
					Dashboard(context) {
						showAlertView(
							title,
							subtitle,
							false,
							{ cacheDataAndSetNetBy(wallet, callback) }
						) { cacheDataAndSetNetBy(wallet, callback) }
					}
				}
			}
		}

		@WorkerThread
		private fun cacheWalletData(wallet: WalletTable, callback: () -> Unit) {
			with(wallet) {
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
				callback()
			}
		}

		@WorkerThread
		private fun cacheDataAndSetNetBy(wallet: WalletTable, callback: () -> Unit) {
			val type = wallet.getWalletType()
			type.updateSharedPreference()
			SharedWallet.updateBackUpMnemonicStatus(wallet.hasBackUpMnemonic)
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
				type.isEOSKylin() -> NodeSelectionPresenter.setAllTestnet {
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