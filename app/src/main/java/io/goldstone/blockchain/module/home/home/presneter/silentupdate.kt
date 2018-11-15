package io.goldstone.blockchain.module.home.home.presneter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.ConcurrentAsyncCombine
import com.blinnnk.util.Connectivity
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.BackupServerChecker
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
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

	fun star() = GlobalScope.launch(Dispatchers.Default) {
		// 数据量很小, 使用频发, 可以在 `4G` 下请求
		updateRAMUnitPrice()
		updateCPUUnitPrice()
		updateNETUnitPrice()
		// 共同需要 Config
		AppConfigTable.dao.getAppConfig()?.let {
			checkMD5Info(it) { hasNewDefaultTokens, hasNewChainNodes, hasNewExchanges ->
				fun updateData() {
					if (hasNewDefaultTokens) updateLocalDefaultTokens()
					if (hasNewChainNodes) updateNodeData()
					if (hasNewExchanges) updateLocalExchangeData()
				}
				when {
					Connectivity.isConnectedWifi(GoldStoneAPI.context) -> updateData()
					Connectivity.isConnectedMobile(GoldStoneAPI.context) -> {
						// TODO Show Confirm Dialog
					}
				}
			}
			updateCurrencyRateFromServer(it)
			updateAgreement(it)
		}
		// 是 WIFI 的情况下, 才执行静默更新
		if (Connectivity.isConnectedWifi(GoldStoneAPI.context)) {
			updateShareContentFromServer()
			updateERCDefaultTokenInfo()
			updateMyTokenCurrencyPrice()
			checkAvailableEOSTokenList()
			checkAvailableERC20TokenList()
		}
	}

	private fun checkMD5Info(
		config: AppConfigTable,
		hold: (
			hasNewDefaultTokens: Boolean,
			hasNewChainNodes: Boolean,
			hasNewExchanges: Boolean
		) -> Unit
	) {
		GoldStoneAPI.getMD5List { md5s, error ->
			if (md5s.isNotNull() && error.isNone()) {
				val newDefaultTokenListMD5 = md5s.safeGet("default_token_list_md5")
				val newChainNodesMD5 = md5s.safeGet("chain_nodes_md5")
				val newExchangeListMD5 = md5s.safeGet("market_list_md5")
				AppConfigTable.dao.updateMD5Info(newDefaultTokenListMD5, newChainNodesMD5, newExchangeListMD5)
				hold(
					config.defaultCoinListMD5 != newDefaultTokenListMD5,
					config.nodeListMD5 != newChainNodesMD5,
					config.exchangeListMD5 != newExchangeListMD5
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
			"result"
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
		val account = SharedAddress.getCurrentEOSAccount()
		val chainID = SharedChain.getEOSCurrent().chainID
		if (!account.isValid(false)) return
		EOSAPI.getEOSTokenList(chainID, account) { tokenList, error ->
			// 拉取潜在资产的 `Icon Url`
			if (tokenList.isNotNull() && error.isNone()) GoldStoneAPI.getIconURL(tokenList) { tokenIcons, getIconError ->
				if (tokenIcons.isNotNull() && getIconError.isNone()) tokenList.forEach { contract ->
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
						account.accountName,
						chainID.id
					)
					// 有可能用户本地已经插入并且被用户手动关闭了, 所以只有本地不存在的时候才插入
					// 插入 `MyTokenTable`
					if (targetToken.isNull()) MyTokenTable.dao.insert(
						MyTokenTable(
							account.accountName,
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
		EOSResourceUtil.getRAMPrice(EOSUnit.KB, false) { priceInEOS, error ->
			if (priceInEOS != null && error.isNone()) {
				SharedValue.updateRAMUnitPrice(priceInEOS)
			}
		}
	}

	private fun updateCPUUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getCPUPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (priceInEOS != null && error.isNone()) {
					SharedValue.updateCPUUnitPrice(priceInEOS)
				}
			}
		}
	}

	private fun updateNETUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getNETPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (priceInEOS != null && error.isNone()) {
					SharedValue.updateNETUnitPrice(priceInEOS)
				}
			}
		}
	}

	private fun updateNodeData() {
		// 拉取网络数据, 更新本地的选中状态后覆盖本地数据库 TODO 需要增加 MD5 校验减少网络请求
		GoldStoneAPI.getChainNodes { serverNodes, error ->
			val nodeDao = GoldStoneDataBase.database.chainNodeDao()
			if (serverNodes != null && error.isNone() && serverNodes.isNotEmpty()) {
				val localNodes = nodeDao.getAll()
				serverNodes.map { node ->
					node.apply {
						isUsed = localNodes.find {
							it.url.equals(node.url, true)
						}?.isUsed ?: 0
					}
				}.let {
					nodeDao.insertAll(it)
				}
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
			object : ConcurrentAsyncCombine() {
				val eosTokens =
					myTokens.filter {
						ChainID(it.chainID).isEOSMain() && !CoinSymbol(it.symbol).isEOS()
					}
				override val delayTime: Long? = 500
				override var asyncCount: Int = eosTokens.size
				override fun doChildTask(index: Int) {
					EOSAPI.updateLocalTokenPrice(
						TokenContract(eosTokens[index].contract, eosTokens[index].symbol, null)
					)
					completeMark()
				}
			}.start()
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
		val unManuallyData = localTokens.filter { it.serverTokenID.isNotEmpty() }
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
			val defaultDao =
				GoldStoneDataBase.database.defaultTokenDao()
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

	private fun updateShareContentFromServer() {
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

	// 获取当前的汇率
	@WorkerThread
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

	@WorkerThread
	private fun updateAgreement(config: AppConfigTable) {
		val md5 = config.terms.getObjectMD5HexString()
		GoldStoneAPI.getTerms(md5) { term, error ->
			if (!term.isNullOrBlank() && error.isNone()) {
				AppConfigTable.dao.updateTerms(term)
			}
		}
	}
}