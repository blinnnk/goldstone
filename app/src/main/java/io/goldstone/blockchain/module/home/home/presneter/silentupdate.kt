package io.goldstone.blockchain.module.home.home.presneter

import android.content.Context
import com.blinnnk.extension.*
import com.blinnnk.util.ConcurrentAsyncCombine
import com.blinnnk.util.Connectivity
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.kernel.commontable.MyTokenTable
import io.goldstone.blockchain.kernel.commontable.SupportCurrencyTable
import io.goldstone.blockchain.kernel.commontable.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.BackupServerChecker
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.presenter.TokenAssetPresenter
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author KaySaith
 * @date  2018/09/26
 */
abstract class SilentUpdater {

	private val account = SharedAddress.getCurrentEOSAccount()
	private val chainID = SharedChain.getEOSCurrent().chainID

	fun star(context: Context) = GlobalScope.launch(Dispatchers.Default) {
		updateNewDAPP()// todo
		// 数据量很小, 使用频发, 可以在 `4G` 下请求
		updateRAMUnitPrice()
		updateCPUUnitPrice()
		updateNETUnitPrice()
		// 这个会分别在 4G 和 Wifi 下用不同的询问方式进行执行
		fun updateTokenInfo() {
			updateERCDefaultTokenInfo()
			updateMyTokenCurrencyPrice()
			checkAvailableEOSTokenList()
			checkAvailableERC20TokenList()
			// 拉取 EOS 的总的 Delegate Data
			updateDelegateBandwidthData()
		}
		AppConfigTable.dao.getAppConfig()?.let {
			checkMD5Info(it) { hasNewDefaultTokens,
												 hasNewChainNodes,
												 hasNewExchanges,
												 hasNewTerm,
												 hasNewConfig,
												 hasNewShareContent,
												 hasNewRecommendedDAPP,
												 hasNewDAPP,
												 hasNewDAPPJSCode ->
				fun updateData() {
					if (hasNewDefaultTokens) updateLocalDefaultTokens()
					if (hasNewChainNodes) updateNodeData()
					if (hasNewExchanges) updateLocalExchangeData()
					if (hasNewTerm) updateAgreement()
					if (hasNewShareContent) updateShareContent()
					if (hasNewConfig) {
						// TODO
					}
					if (hasNewRecommendedDAPP) updateRecommendedDAPP()
					if (hasNewDAPP) updateNewDAPP()
					if (hasNewDAPPJSCode) updateDAPPJSCode()
					// 确认后更新 MD5 值到数据库
					AppConfigTable.dao.updateMD5Info(
						newDefaultTokenListMD5,
						newChainNodesMD5,
						newExchangeListMD5,
						newTermMD5,
						newConfigMD5,
						newShareContentMD5,
						newRecommendedDAPPMD5,
						newDAPPMD5,
						"" // TODO newDAPPJSCode
					)
				}
				when {
					Connectivity.isConnectedWifi(GoldStoneApp.appContext) -> updateData()
					Connectivity.isConnectedMobile(GoldStoneApp.appContext) -> {
						launchUI {
							GoldStoneDialog(context).showMobile4GConfirm {
								GlobalScope.launch(Dispatchers.Default) {
									updateData()
									updateTokenInfo()
								}
							}
						}
					}
				}
			}
			updateCurrencyRateFromServer(it)
		}
		// 是 WIFI 的情况下, 才执行静默更新
		if (Connectivity.isConnectedWifi(GoldStoneApp.appContext)) {
			updateTokenInfo()
		}

		//  初始化检查更新 EOS Account Info
		if (account.isValid(false)) {
			TokenAssetPresenter.updateEOSAccountInfoFromChain(account, chainID) {}
		}
	}

	private var newDefaultTokenListMD5 = ""
	private var newChainNodesMD5 = ""
	private var newExchangeListMD5 = ""
	private var newTermMD5 = ""
	private var newConfigMD5 = ""
	private var newShareContentMD5 = ""
	private var newRecommendedDAPPMD5 = ""
	private var newDAPPMD5 = ""
	private var newDAPPJSCode = ""
	private fun checkMD5Info(
		config: AppConfigTable,
		hold: (
			hasNewDefaultTokens: Boolean,
			hasNewChainNodes: Boolean,
			hasNewExchanges: Boolean,
			hasNewTerm: Boolean,
			hasNewConfig: Boolean,
			hasNewShareContent: Boolean,
			hasNewRecommendedDAPP: Boolean,
			hasNewDAPP: Boolean,
			hasNewDAPPCode: Boolean
		) -> Unit
	) {
		GoldStoneAPI.getMD5List { md5s, error ->
			if (md5s.isNotNull() && error.isNone()) {
				newDefaultTokenListMD5 = md5s.safeGet("default_token_list_md5")
				newChainNodesMD5 = md5s.safeGet("chain_nodes_md5")
				newExchangeListMD5 = md5s.safeGet("market_list_md5")
				newTermMD5 = md5s.safeGet("agreement_md5")
				newConfigMD5 = md5s.safeGet("config_list_md5")
				newShareContentMD5 = md5s.safeGet("share_content_md5")
				newRecommendedDAPPMD5 = md5s.safeGet("dapp_recommend_md5")
				newDAPPMD5 = md5s.safeGet("dapps_md5")
				newDAPPJSCode = md5s.safeGet("get_js_md5")
				hold(
					config.defaultCoinListMD5 != newDefaultTokenListMD5,
					config.nodeListMD5 != newChainNodesMD5,
					config.exchangeListMD5 != newExchangeListMD5,
					config.termMD5 != newTermMD5,
					config.configMD5 != newConfigMD5,
					config.shareContentMD5 != newShareContentMD5,
					config.dappRecommendMD5 != newRecommendedDAPPMD5,
					config.newDAPPMD5 != newDAPPMD5,
					config.dappJSCodeMD5 != newDAPPJSCode
				)
			}
		}
	}

	private fun updateLocalExchangeData() {
		GoldStoneAPI.getMarketList { exchanges, error ->
			if (exchanges.isNotNull() && error.isNone()) {
				val exchangeDao = ExchangeTable.dao
				val localData = exchangeDao.getAll()
				if (localData.isEmpty()) {
					exchangeDao.insertAll(exchanges)
				} else exchangeDao.apply {
					insertAll(
						exchanges.map { serverData ->
							serverData.apply {
								isSelected = localData.find { local ->
									local.marketId == serverData.marketId
								}?.isSelected.orFalse()
							}
						})
				}
			}
		}
	}

	private fun checkAvailableERC20TokenList() {
		val transactionDao =
			GoldStoneDataBase.database.transactionDao()
		val allTransactions =
			transactionDao.getTransactionsByAddress(
				SharedAddress.getCurrentEthereum(),
				SharedChain.getCurrentETH().chainID.id
			)
		val maxBlockNumber =
			allTransactions.filterNot {
				it.contractAddress.equals(TokenContract.etcContract, true)
			}.maxBy { it.blockNumber }?.blockNumber
		getERC20TokenTransactions(maxBlockNumber ?: 0)
	}

	private fun getERC20TokenTransactions(startBlock: Int) {
		RequisitionUtil.requestUnCryptoData<ERC20TransactionModel>(
			EtherScanApi.getTokenTransactions(SharedAddress.getCurrentEthereum(), startBlock),
			listOf("result")
		) { transactions, error ->
			if (transactions?.isNotEmpty() == true && error.isNone()) {
				val defaultDao =
					GoldStoneDataBase.database.defaultTokenDao()
				val transactionDao =
					GoldStoneDataBase.database.transactionDao()
				// onConflict InsertAll 利用 RoomDatabase 进行双主键做重复判断 
				// 覆盖或新增到本地 TransactionTable 数据库里
				transactionDao.insertAll(transactions.map { TransactionTable(it) })
				// 检测获取的 ERC20 本地是否有对应的 DefaultToken 记录, 如果没有插入到本地数据库
				// 供其他场景使用
				val chain = SharedChain.getCurrentETH()
				transactions.distinctBy { it.contract }.forEach { erc20 ->
					defaultDao.getERC20Token(erc20.contract, chain.chainID.id)
						?: defaultDao.insert(DefaultTokenTable(erc20, chain.chainID))
				}

				// 目前遇到 EtherScan 的 ERC20 接口仍然会返回
				transactions.filter {
					it.tokenSymbol.isEmpty() || it.tokenDecimal.isEmpty()
				}.apply {
					updateUnknownDefaultToken(
						map { DefaultTokenTable(it, chain.chainID) },
						chain
					)
				}
			}
		}
	}

	private fun checkAvailableEOSTokenList() {
		if (!account.isValid(false)) return
		fun updateData(tokens: List<TokenContract>) {
			// 拉取潜在资产的 `Icon Url`
			GoldStoneAPI.getIconURL(tokens) { tokenIcons, getIconError ->
				if (tokenIcons.isNotNull() && getIconError.isNone()) tokens.forEach { contract ->
					//  这个接口只服务主网下的 `Token` 插入 `DefaultToken`
					// EOS 的价格不再这里更新
					if (!CoinSymbol(contract.symbol).isEOS()) DefaultTokenTable.dao.insert(
						DefaultTokenTable(
							contract,
							tokenIcons.get(contract.orEmpty())?.url.orEmpty(),
							chainID
						)
					)
					val targetToken = MyTokenTable.dao.getTokenByContractAndAddress(
						contract.contract,
						contract.symbol,
						account.name,
						chainID.id
					)
					// 有可能用户本地已经插入并且被用户手动关闭了, 所以只有本地不存在的时候才插入
					// 插入 `MyTokenTable`
					if (targetToken.isNull()) MyTokenTable.dao.insert(
						MyTokenTable(
							account.name,
							SharedAddress.getCurrentEOS(),
							contract.symbol,
							0.0,
							contract.contract,
							chainID.id,
							false
						)
					)
				}
			}
		}

		// 优先从 `EOSPark` 获取 `Token List`, 如果出错或测试网替换为 `GoldStone` 的接口
		if (!SharedValue.isTestEnvironment()) EOSAPI.getTokenBalance(account) { tokens, error ->
			if (tokens.isNotNull() && error.isNone()) {
				updateData(tokens.map { TokenContract(it.codeName, it.symbol, it.getDecimal()) })
			} else EOSAPI.getEOSTokenList(chainID, account) { tokenList, tokenListError ->
				if (tokenList.isNotNull() && tokenListError.isNone()) updateData(tokenList)
			}
		} else EOSAPI.getEOSTokenList(chainID, account) { tokenList, tokenListError ->
			if (tokenList.isNotNull() && tokenListError.isNone()) updateData(tokenList)
		}
	}

	private fun updateERCDefaultTokenInfo() {
		val defaultDao = GoldStoneDataBase.database.defaultTokenDao()
		val chain = SharedChain.getCurrentETH()
		val unKnownDefault = defaultDao.getAllTokens().filter {
			it.chainID == chain.chainID.id
				&& it.name.isEmpty()
				&& it.decimals == 0
				&& it.symbol.isEmpty()
		}
		updateUnknownDefaultToken(unKnownDefault, chain)
	}

	// 检查更新默认 `Token` 的 `Name` 信息
	private fun updateUnknownDefaultToken(unKnowData: List<DefaultTokenTable>, chainURL: ChainURL) {
		object : ConcurrentAsyncCombine() {
			val defaultDao = GoldStoneDataBase.database.defaultTokenDao()
			override val delayTime: Long? = 300L
			override var asyncCount: Int = unKnowData.size
			override fun doChildTask(index: Int) = ETHJsonRPC.getTokenInfoByContractAddress(
				unKnowData[index].contract,
				chainURL
			) { symbol, name, decimal, error ->
				if (error.isNone()) {
					defaultDao.updateTokenInfo(
						name.orEmpty(),
						decimal ?: 0,
						symbol.orEmpty(),
						unKnowData[index].contract,
						chainURL.chainID.id
					)
					completeMark()
				} else completeMark()
			}
		}.start()
	}

	private fun updateRAMUnitPrice() {
		EOSResourceUtil.getRAMPrice(EOSUnit.KB) { priceInEOS, error ->
			if (priceInEOS.isNotNull() && error.isNone()) {
				SharedValue.updateRAMUnitPrice(priceInEOS)
			}
		}
	}

	private fun updateCPUUnitPrice() {
		if (account.isValid(false)) {
			EOSResourceUtil.getCPUPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (priceInEOS.isNotNull() && error.isNone()) {
					SharedValue.updateCPUUnitPrice(priceInEOS)
				}
			}
		}
	}

	private fun updateDelegateBandwidthData() {
		if (account.isValid(false)) {
			EOSAPI.getDelegateBandWidthList(account) { data, error ->
				if (data.isNotNull() && error.isNone()) {
					EOSAccountTable.dao.updateDelegateBandwidthData(data, account.name, chainID.id)
				}
			}
		}
	}

	private fun updateNETUnitPrice() {
		if (account.isValid(false)) {
			EOSResourceUtil.getNETPrice(account) { priceInEOS, error ->
				if (priceInEOS.isNotNull() && error.isNone()) {
					SharedValue.updateNETUnitPrice(priceInEOS)
				}
			}
		}
	}

	private fun updateNodeData() {
		GoldStoneAPI.getChainNodes { serverNodes, error ->
			val nodeDao = ChainNodeTable.dao
			if (serverNodes.isNotNull() && error.isNone() && serverNodes.isNotEmpty()) {
				nodeDao.deleteAll()
				nodeDao.insertAll(serverNodes)
			}
		}
	}

	private fun updateMyTokenCurrencyPrice() {
		MyTokenTable.getMyTokens { myTokens ->
			// `EOS` 的 `Token` 价格在下面的方法从第三方获取, 这里过滤掉 `EOS` 的 `Token`
			val contractList = myTokens.asSequence().filterNot {
				ChainID(it.chainID).isEOSMain() && !CoinSymbol(it.symbol).isEOS()
			}.map {
				"{\"address\":\"${it.contract}\",\"symbol\":\"${it.symbol}\"}"
			}.toList()
			GoldStoneAPI.getPriceByContractAddress(contractList) { newPrices, error ->
				if (!newPrices.isNullOrEmpty() && error.isNone()) {
					newPrices.forEach {
						val contract = TokenContract(it.contract, it.symbol, null)
						DefaultTokenTable.dao.updateTokenPrice(
							it.price,
							contract.contract.orEmpty(),
							contract.symbol,
							contract.getCurrentChainID().id
						)
					}
				}
			}
			// 因为第三方接口 NewDex 没有列表查询的 API, 只能一个一个的请求, 这里可能会造成
			// 线程开启太多的内存溢出. 限制每 `500ms` 检查一个 `Symbol` 的价格
			EOSAPI.getPairsFromNewDex { pairs, error ->
				if (pairs.isNotNull() && error.isNone()) {
					object : ConcurrentAsyncCombine() {
						val eosTokens =
							myTokens.filter {
								ChainID(it.chainID).isEOSMain() && !CoinSymbol(it.symbol).isEOS()
							}
						override val delayTime: Long? = 300
						override var asyncCount: Int = eosTokens.size
						override fun doChildTask(index: Int) {
							EOSAPI.updateTokenPriceFromNewDex(
								TokenContract(eosTokens[index].contract, eosTokens[index].symbol, null),
								pairs
							)
							completeMark()
						}
					}.start()
				}
			}
		}
	}

	private fun updateLocalDefaultTokens() {
		GoldStoneAPI.getDefaultTokens { serverTokens, error ->
			if (!serverTokens.isNullOrEmpty() && error.isNone()) {
				val localTokens = DefaultTokenTable.dao.getAllTokens()
				// 开一个线程更新图片
				updateLocalTokenIcon(serverTokens, localTokens)
				// 移除掉一样的数据
				DefaultTokenTable.dao.insertAll(
					serverTokens.filterNot { server ->
						localTokens.any { local ->
							local.chainID.equals(server.chainID, true)
								&& local.contract.equals(server.contract, true)
								&& local.symbol.equals(server.symbol, true)
						}
					}
				)
			}
		}
	}

	private fun updateLocalTokenIcon(
		serverTokens: List<DefaultTokenTable>,
		localTokens: List<DefaultTokenTable>
	) {
		val unManuallyData =
			localTokens.filter { it.serverTokenID.isNotEmpty() }
		serverTokens.filter { server ->
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
			if (isEmpty()) return
			val defaultDao = DefaultTokenTable.dao
			forEach { server ->
				defaultDao.getToken(server.contract, server.symbol, server.chainID)?.apply {
					defaultDao.update(
						apply {
							iconUrl = server.iconUrl
							isDefault = server.isDefault
							forceShow = server.forceShow
						}
					)
				}
			}
		}
	}

	private fun updateShareContent() {
		GoldStoneAPI.getShareContent { shareContent, error ->
			if (!shareContent.isNull() && error.isNone()) {
				val shareText = if (shareContent.title.isEmpty() && shareContent.content.isEmpty()) {
					ProfileText.shareContent
				} else {
					"${shareContent.title}\n${shareContent.content}\n${shareContent.url}"
				}
				AppConfigTable.dao.updateShareContent(shareText)
			} else {
				BackupServerChecker.checkBackupStatusByException(error)
			}
		}
	}

	private fun updateCurrencyRateFromServer(config: AppConfigTable) {
		GoldStoneAPI.getCurrencyRate(config.currencyCode) { rate, error ->
			if (rate.isNotNull() && error.isNone()) {
				// 更新 `SharePreference` 中的值
				SharedWallet.updateCurrentRate(rate)
				// 更新数据库的值
				SupportCurrencyTable.dao.updateUsedRate(rate)
			}
		}
	}

	private fun updateAgreement() {
		GoldStoneAPI.getTerms { term, error ->
			if (!term.isNullOrBlank() && error.isNone()) {
				AppConfigTable.dao.updateTerms(term)
			}
		}
	}

	private fun updateRecommendedDAPP() {
		GoldStoneAPI.getRecommendDAPPs(0) { dapps, error ->
			if (dapps.isNotNull() && error.isNone()) {
				DAPPTable.dao.deleteAllRecommend()
				DAPPTable.dao.insertAll(dapps)
			} else ErrorDisplayManager(error)
		}
	}

	private fun updateNewDAPP() {
		GoldStoneAPI.getNewDAPPs(0) { dapps, error ->
			if (dapps.isNotNull() && error.isNone()) {
				DAPPTable.dao.deleteAllUnRecommended()
				DAPPTable.dao.insertAll(dapps)
			} else ErrorDisplayManager(error)
		}
	}

	private fun updateDAPPJSCode() {
		GoldStoneAPI.getDAPPJSCode { code, error ->
			if (code.isNotNull() && error.isNone()) {
				System.out.println("code $code")
				AppConfigTable.dao.updateJSCode(code)
			} else ErrorDisplayManager(error)
		}
	}
}