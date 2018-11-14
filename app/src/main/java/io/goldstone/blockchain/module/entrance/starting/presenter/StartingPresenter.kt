package io.goldstone.blockchain.module.entrance.starting.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.BackupServerChecker
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.doAsync

@Suppress("IMPLICIT_CAST_TO_ANY")
/**
 * @date 22/03/2018 2:56 AM
 * @author KaySaith
 */
class StartingPresenter(override val fragment: StartingFragment) :
	BasePresenter<StartingFragment>() {

	fun showCreateWalletFragment() {
		fragment.activity?.addFragment<WalletGenerationFragment>(ContainerID.splash)
	}

	fun showImportWalletFragment() {
		fragment.activity?.addFragment<WalletImportFragment>(ContainerID.splash)
	}

	fun updateWalletInfoForUserInfo(walletList: List<WalletTable>) {
		walletList.apply {
			// 记录当前最大的钱包 `ID` 用来生成默认头像和名字
			SharedWallet.updateMaxWalletID(maxBy { it.id }?.id.orZero())
			SharedWallet.updateWalletCount(size)
		}
	}

	companion object {

		fun updateShareContentFromServer() {
			GoldStoneAPI.getShareContent { shareContent, error ->
				if (!shareContent.isNull() && error.isNone()) {
					val shareText = if (shareContent.title.isEmpty() && shareContent.content.isEmpty()) {
						ProfileText.shareContent
					} else {
						"${shareContent.title}\n${shareContent.content}\n${shareContent.url}"
					}
					GoldStoneDataBase.database.appConfigDao().updateShareContent(shareText)
				} else {
					BackupServerChecker.checkBackupStatusByException(error)
				}
			}
		}

		fun insertLocalTokens(context: Context, @WorkerThread callback: () -> Unit) {
			context.convertLocalJsonFileToJSONObjectArray(R.raw.local_token_list).map {
				DefaultTokenTable(it)
			}.let {
				GoldStoneDataBase.database.defaultTokenDao().insertAll(it)
				callback()
			}
		}

		fun insertLocalMarketList(context: Context, callback: () -> Unit) {
			context.convertLocalJsonFileToJSONObjectArray(R.raw.local_market_list)
				.forEachOrEnd { market, isEnd ->
					GoldStoneDataBase.database.exchangeTableDao().insert(ExchangeTable(market))
					if (isEnd) callback()
				}
		}

		fun insertLocalNodeList(context: Context, @WorkerThread callback: () -> Unit) {
			doAsync {
				context.convertLocalJsonFileToJSONObjectArray(R.raw.node_list).map {
					ChainNodeTable(it)
				}.let {
					GoldStoneDataBase.database.chainNodeDao().insertAll(it)
					callback()
				}
			}
		}

		fun getAndUpdateExchangeTables(
			@WorkerThread hold: (exchangeTableList: ArrayList<ExchangeTable>?, error: RequestError) -> Unit
		) {
			AppConfigTable.getAppConfig(Dispatchers.Default) {
				GoldStoneAPI.getMarketList(
					it?.exchangeListMD5.orEmpty()
				) { serverExchangeTables, md5, error ->
					if (!serverExchangeTables.isNull() && error.isNone()) {
						if (serverExchangeTables.isEmpty()) {
							hold(null, RequestError.RPCResult("Empty Data"))
						} else {
							updateExchangeTables(serverExchangeTables, md5.orEmpty())
							hold(serverExchangeTables, RequestError.None)
						}
					} else {
						hold(null, error)
					}
				}
			}
		}

		private fun updateExchangeTables(serverTableList: ArrayList<ExchangeTable>, md5: String) {
			val exchangeDao = GoldStoneDataBase.database.exchangeTableDao()
			val localData = exchangeDao.getAll()
			if (localData.isEmpty()) exchangeDao.insertAll(serverTableList)
			else exchangeDao.apply {
				deleteAll()
				insertAll(
					serverTableList.map { serverData ->
						serverData.apply {
							isSelected = localData.find { local ->
								local.marketId == serverData.marketId
							}?.isSelected.orFalse()
						}
					})
			}
			AppConfigTable.updateExchangeListMD5(md5)
		}

		private fun List<DefaultTokenTable>.updateLocalTokenIcon(localTokens: ArrayList<DefaultTokenTable>) {
			doAsync {
				val unManuallyData = localTokens.filter { it.serverTokenID.isNotEmpty() }
				filter { server ->
					unManuallyData.find {
						it.serverTokenID.equals(server.serverTokenID, true)
					}?.let {
						// 如果本地的非手动添加的数据没有存在于最新从 `Server` 拉取下来的意味着已经被 `CMS` 移除
						GoldStoneDataBase.database.defaultTokenDao().update(it.apply { isDefault = false })
					}

					localTokens.any { local ->
						local.chainID.equals(server.chainID, true)
							&& local.contract.equals(server.contract, true)
					}
				}.apply {
					if (isEmpty()) return@doAsync
					val defaultTokenDao =
						GoldStoneDataBase.database.defaultTokenDao()
					forEach { server ->
						defaultTokenDao.getToken(server.contract, server.symbol, server.chainID)?.let {
							defaultTokenDao.update(it.apply {
								iconUrl = server.iconUrl
								isDefault = server.isDefault
								forceShow = server.forceShow
							})
						}
					}
				}
			}
		}

		fun insertLocalCurrency(context: Context, @WorkerThread callback: () -> Unit) {
			context.convertLocalJsonFileToJSONObjectArray(R.raw.support_currency_list).map {
				SupportCurrencyTable(it).apply {
					// 初始化的汇率显示本地 `Json` 中的值, 之后是通过网络更新
					if (currencySymbol.equals(CountryCode.currentCurrency, true)) {
						isUsed = true
						SharedWallet.updateCurrentRate(rate)
					}
				}
			}.let {
				GoldStoneDataBase.database.currencyDao().insertAll(it)
				callback()
			}
		}
	}
}