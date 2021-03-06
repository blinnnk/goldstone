package io.goldstone.blinnnk.module.home.home.presneter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.ConcurrentAsyncCombine
import com.blinnnk.util.Connectivity
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.component.overlay.GoldStoneDialog
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.crypto.eos.EOSUnit
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.multichain.node.ChainNodeTable
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.kernel.commontable.*
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import io.goldstone.blinnnk.kernel.network.common.BackupServerChecker
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.kernel.network.common.RequisitionUtil
import io.goldstone.blinnnk.kernel.network.eos.EOSAPI
import io.goldstone.blinnnk.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blinnnk.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blinnnk.kernel.network.ethereum.EtherScanApi
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blinnnk.module.common.tokendetail.tokenasset.presenter.TokenAssetPresenter
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blinnnk.module.home.quotation.quotationrank.model.QuotationRankTable
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel

/**
 * @author KaySaith
 * @date  2018/09/26
 */
abstract class SilentUpdater {

	private val account = SharedAddress.getCurrentEOSAccount()
	private val chainID = SharedChain.getEOSCurrent().chainID

	fun star(context: Context) = launchDefault {
		val configDao = AppConfigTable.dao
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
		configDao.getAppConfig()?.let {
			when {
				Connectivity.isConnectedWifi(GoldStoneApp.appContext) -> checkMD5AndUpdateData()
				Connectivity.isConnectedMobile(GoldStoneApp.appContext) -> {
					launchUI {
						GoldStoneDialog(context).showMobile4GConfirm {
							launchDefault {
								checkMD5AndUpdateData()
								updateTokenInfo()
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

	private fun checkMD5AndUpdateData() {
		GoldStoneAPI.getMD5List { md5s, error ->
			if (md5s.isNotNull() && error.isNone()) {
				val localMD5 = MD5Table.dao.getAll()
				val allNames = md5s.names()
				(0 until allNames.length()).forEach { index ->
					val name = allNames.getString(index)
					val md5 = md5s.safeGet(name)
					if (name != "code" && localMD5.find { it.tableKey == name }?.md5Value != md5) {
						val table = MD5Table(name, md5)
						when {
							name.contains("token_list") -> updateLocalDefaultTokens {
								MD5Table.dao.updateValue(table)
							}
							name.contains("chain_nodes") -> updateNodeData {
								MD5Table.dao.updateValue(table)
							}
							name.contains("market_list") -> updateLocalExchangeData {
								MD5Table.dao.updateValue(table)
							}
							name.contains("agreement") -> updateAgreement {
								MD5Table.dao.updateValue(table)
							}
							name.contains("config_list") -> updateConfigListData {
								MD5Table.dao.updateValue(table)
							}
							name.contains("share_content") -> updateShareContent {
								MD5Table.dao.updateValue(table)
							}
							name.contains("dapp_recommend") -> updateRecommendedDAPP {
								MD5Table.dao.updateValue(table)
							}
							name.contains("dapps_md5") -> updateNewDAPP {
								MD5Table.dao.updateValue(table)
							}
							name.contains("js") -> updateDAPPJSCode {
								MD5Table.dao.updateValue(table)
							}
							name.contains("rank") -> updateQuotationRank {
								MD5Table.dao.updateValue(table)
							}
						}
					}
				}
			}
		}
	}

	private fun updateLocalExchangeData(callback: () -> Unit) {
		GoldStoneAPI.getMarketList { exchanges, error ->
			if (exchanges.isNotNull() && error.isNone()) {
				callback()
				val exchangeDao = ExchangeTable.dao
				val localData = exchangeDao.getAll()
				if (localData.isEmpty()) {
					exchangeDao.insertAll(exchanges)
				} else exchangeDao.apply {
					clearTable()
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
		val transactionDao = TransactionTable.dao
		val maxBlockNumber = transactionDao.getMyMaxBlockNumber(
			SharedAddress.getCurrentEthereum(),
			SharedChain.getCurrentETH().chainID.id
		)
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

	private fun updateNodeData(callback: () -> Unit) {
		GoldStoneAPI.getChainNodes { serverNodes, error ->
			val nodeDao = ChainNodeTable.dao
			if (serverNodes.isNotNull() && error.isNone() && serverNodes.isNotEmpty()) {
				nodeDao.deleteAll()
				nodeDao.insertAll(serverNodes)
				callback()
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

	private fun updateLocalDefaultTokens(callback: () -> Unit) {
		GoldStoneAPI.getDefaultTokens { serverTokens, error ->
			if (!serverTokens.isNullOrEmpty() && error.isNone()) {
				callback()
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

	private fun updateShareContent(callback: () -> Unit) {
		GoldStoneAPI.getShareContent { shareContent, error ->
			if (!shareContent.isNull() && error.isNone()) {
				callback()
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

	private fun updateAgreement(callback: () -> Unit) {
		GoldStoneAPI.getTerms { term, error ->
			if (!term.isNullOrBlank() && error.isNone()) {
				AppConfigTable.dao.updateTerms(term)
				callback()
			}
		}
	}

	private fun updateDAPPJSCode(callback: () -> Unit) {
		GoldStoneAPI.getDAPPJSCode { code, error ->
			if (code.isNotNull() && error.isNone()) {
				AppConfigTable.dao.updateJSCode(code)
				SharedValue.updateJSCode(code)
				callback()
			} else ErrorDisplayManager(error)
		}
	}

	/**
	 * `GetTransaction` 的 `RPC` 接口分别在, 进入账单 获取 `CPU NET` 消耗值,
	 * `Transfer Observer` 监听转账进度的时候需要调取, 同时有因为很多 `History`
	 * 节点经常挂掉. 导致这里有可能需要动态的有 `CMS` 更新可支持的节点.
	 * 故此增加了 `Config List` 的配置支持.
	 */
	private fun updateConfigListData(callback: () -> Unit) {
		GoldStoneAPI.getConfigList { models, error ->
			if (!models.isNullOrEmpty() && error.isNone()) {
				callback()
				models.forEach {
					when (it.name) {
						"eosKylinHistory" -> SharedValue.updateKylinHistoryURL(it.value)
						"eosJungleHistory" -> SharedValue.updateJungleHistoryURL(it.value)
						"eosMainnetHistory" -> SharedValue.updateMainnetHistoryURL(it.value)
					}
				}
			}
		}
	}

	companion object {
		fun updateQuotationRank(callback: () -> Unit) {
			GoldStoneAPI.getQuotationRankList(0) { data, error ->
				if (!data.isNullOrEmpty() && error.isNone()) {
					QuotationRankTable.dao.deleteAll()
					QuotationRankTable.dao.insertAll(data)
					callback()
				}
			}
		}

		fun updateRecommendedDAPP(@WorkerThread callback: () -> Unit) {
			GoldStoneAPI.getRecommendDAPPs(0) { dapps, error ->
				if (dapps.isNotNull() && error.isNone()) {
					DAPPTable.dao.deleteAllRecommend()
					DAPPTable.dao.insertAll(dapps)
					callback()
				} else ErrorDisplayManager(error)
			}
		}

		fun updateNewDAPP(@WorkerThread callback: () -> Unit) {
			GoldStoneAPI.getNewDAPPs(0) { dapps, error ->
				if (dapps.isNotNull() && error.isNone()) {
					DAPPTable.dao.deleteAllUnRecommended()
					DAPPTable.dao.insertAll(dapps)
					callback()
				} else ErrorDisplayManager(error)
			}
		}
	}
}