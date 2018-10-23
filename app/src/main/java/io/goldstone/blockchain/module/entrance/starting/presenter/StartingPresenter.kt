package io.goldstone.blockchain.module.entrance.starting.presenter

import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.BackupServerChecker
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

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
			GoldStoneAPI.getShareContent(
				{ BackupServerChecker.checkBackupStatusByException(it) }
			) {
				val shareText = if (it.title.isEmpty() && it.content.isEmpty()) {
					ProfileText.shareContent
				} else {
					"${it.title}\n${it.content}\n${it.url}"
				}
				AppConfigTable.updateShareContent(shareText)
			}
		}

		fun insertLocalTokens(context: Context, callback: () -> Unit) {
			doAsync {
				context.convertLocalJsonFileToJSONObjectArray(R.raw.local_token_list)
					.forEachOrEnd { token, isEnd ->
						DefaultTokenTable(token).let {
							GoldStoneDataBase.database.defaultTokenDao().insert(it)
							context.runOnUiThread {
								if (isEnd) callback()
							}
						}
					}
			}
		}

		fun insertLocalCurrency(context: Context, callback: () -> Unit) {
			doAsync {
				context.convertLocalJsonFileToJSONObjectArray(R.raw.support_currency_list)
					.forEachOrEnd { item, isEnd ->
						val model =
							if (item.safeGet("currencySymbol") == CountryCode.currentCurrency) {
								SupportCurrencyTable(item).apply {
									isUsed = true
									// 初始化的汇率显示本地 `Json` 中的值, 之后是通过网络更新
									SharedWallet.updateCurrentRate(rate)
								}
							} else {
								SupportCurrencyTable(item)
							}

						GoldStoneDataBase.database.currencyDao().insert(model)

						context.runOnUiThread {
							if (isEnd) callback()
						}
					}
			}
		}

		fun updateLocalDefaultTokens(errorCallback: (GoldStoneError) -> Unit) {
			doAsync {
				GoldStoneAPI.getDefaultTokens(errorCallback) { serverTokens ->
					// 没有网络数据直接返回
					if (serverTokens.isEmpty()) return@getDefaultTokens
					DefaultTokenTable.getAllTokens(false) { localTokens ->
						// 开一个线程更新图片
						serverTokens.updateLocalTokenIcon(localTokens)
						// 移除掉一样的数据
						serverTokens.filterNot { server ->
							localTokens.any { local ->
								local.chainID.equals(server.chainID, true)
									&& local.contract.equals(server.contract, true)
							}
						}.apply {
							GoldStoneDataBase.database.defaultTokenDao().insertAll(this)
						}
					}
				}
			}
		}
		
		fun updateExchangesTablesAndCallback(
			@UiThread hold: (exchangeTableList: ArrayList<ExchangeTable>?, error :RequestError) -> Unit
		) {
			AppConfigTable.getAppConfig {
				GoldStoneAPI.getMarketList(
					it?.exchangeListMD5.orEmpty()
				) { serverExchangeTables, md5, error ->
					if (serverExchangeTables.isNull() || !error.isNone()) {
						hold(null, error)
					} else {
						serverExchangeTables!!.isNotEmpty() isTrue {
							updateExchangeTables(serverExchangeTables, md5.orEmpty())
						}
						hold(serverExchangeTables, RequestError.None)
					}
					
				}
			}
		}
		
		private fun updateExchangeTables(serverTableList: ArrayList<ExchangeTable>, md5: String) {
			GoldStoneDataBase.database.exchangeTableDao().getAll().let { localExchangeTables ->
				localExchangeTables.isEmpty() isTrue {
					ExchangeTable.insertAll(serverTableList)
				} otherwise {
					serverTableList.forEach { serverTable ->
						localExchangeTables.filter { localTable ->
							localTable.exchangeId == serverTable.exchangeId
						}.apply {
							// 逻辑上这里最多只能找到一条
							this.isNotEmpty() isTrue {
								serverTable.isSelected = this[0].isSelected
							}
						}
					}
					ExchangeTable.deleteAll {
						ExchangeTable.insertAll(serverTableList)
					}
				}
				
				AppConfigTable.updateExchangeListMD5(md5.orEmpty())
			}
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
					forEach { server ->
						GoldStoneDataBase.database.defaultTokenDao().apply {
							getTokenByContract(server.contract, server.symbol, server.chainID)?.let {
								update(it.apply {
									iconUrl = server.iconUrl
									isDefault = server.isDefault
									forceShow = server.forceShow
								})
							}
						}
					}
				}
			}
		}
	}
}