package io.goldstone.blockchain.module.entrance.starting.presenter

import android.content.Context
import com.blinnnk.extension.*
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
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
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
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
			Config.updateMaxWalletID(maxBy { it.id }?.id.orZero())
			Config.updateWalletCount(size)
		}
	}
	
	companion object {
		
		fun updateShareContentFromServer() {
			GoldStoneAPI.getShareContent(
				{
					BackupServerChecker.checkBackupStatusByException(it)
					LogUtil.error("showShareChooser", it)
				}
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
									Config.updateCurrentRate(rate)
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
		
		fun updateLocalDefaultTokens(errorCallback: (Exception) -> Unit) {
			doAsync {
				GoldStoneAPI.getDefaultTokens(errorCallback) { serverTokens ->
					// 没有网络数据直接返回
					if (serverTokens.isEmpty()) return@getDefaultTokens
					DefaultTokenTable.getAllTokens { localTokens ->
						// 开一个线程更新图片
						serverTokens.updateLocalTokenIcon(localTokens)
						// 移除掉一样的数据
						serverTokens.filterNot { server ->
							localTokens.any { local ->
								local.chainID.equals(server.chainID, true)
								&& local.contract.equals(server.contract, true)
							}
						}.apply {
							if (isEmpty()) return@getAllTokens
							// 如果还有不一样的网络数据插入数据库
							forEach {
								GoldStoneDataBase.database.defaultTokenDao().insert(it)
							}
						}
					}
				}
			}
		}
		
		fun updateExchangesTables(
			errorCallback: (Exception) -> Unit,
			callback: () -> Unit
		) {
			doAsync {
				AppConfigTable.getAppConfig {
					GoldStoneAPI.getMarketList(
						it?.exchangeListMD5.orEmpty(),
						errorCallback
					) { serverExchangeTables, md5 ->
						serverExchangeTables.isNotEmpty() isTrue {
							if (it?.exchangeListMD5.orEmpty() != md5) {
								AppConfigTable.updateExchangeListMD5(md5)
								ExchangeTable.getAll { localExchangeTables ->
									localExchangeTables.isEmpty() isTrue {
										ExchangeTable.insertAll(serverExchangeTables) {}
									} otherwise {
										localExchangeTables.filterNot { local ->
											serverExchangeTables.any { server ->
												local.id == server.id
											}
										}.apply {
											// 相同的数据过滤掉，剩下的数据是服务器没有的，本地存的老数据，一一删除
											forEach {
												ExchangeTable.delete(it)
											}
										}
										
										serverExchangeTables.forEach { serverTable ->
											ExchangeTable.getExchangeTableByExchangeId(serverTable.exchangeId) { localTable ->
												localTable.isNull() isTrue {
													ExchangeTable.insert(serverTable)
												} otherwise {
													serverTable.isSelected = localTable.isSelected
													serverTable.id = localTable.id
													ExchangeTable.update(serverTable)
												}
											}
											
										}
									}
									
								}
							}
						}
						callback()
					}
				}
			}
		}
		
		private fun ArrayList<DefaultTokenTable>.updateLocalTokenIcon(
			localTokens: ArrayList<DefaultTokenTable>
		) {
			doAsync {
				val unManuallyData = localTokens.filter { it.serverTokenID.isNotEmpty() }
				filter { server ->
					unManuallyData.find {
						it.serverTokenID.equals(server.serverTokenID, true)
					}?.let {
						// 如果本地的非手动添加的数据没有存在于最新从 `Server` 拉取下来的意味着已经被 `CMS` 移除
						GoldStoneDataBase.database
							.defaultTokenDao().update(it.apply { isDefault = false })
					}
					
					localTokens.any { local ->
						local.chainID.equals(server.chainID, true)
						&& local.contract.equals(server.contract, true)
					}
				}.apply {
					if (isEmpty()) return@doAsync
					forEach { server ->
						GoldStoneDataBase
							.database
							.defaultTokenDao()
							.apply {
								getTokenBySymbolContractAndChainID(
									server.symbol,
									server.contract,
									server.chainID
								)?.let {
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