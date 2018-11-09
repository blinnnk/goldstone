package io.goldstone.blockchain.module.home.home.presneter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.Connectivity
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

/**
 * @author KaySaith
 * @date  2018/09/26
 */
abstract class SilentUpdater {
	fun star() {
		launch {
			withContext(CommonPool, CoroutineStart.LAZY) {
				// 是 WIFI 的情况下, 才执行静默更新
				if (Connectivity.isConnectedWifi(GoldStoneAPI.context)) {
					updateLocalDefaultTokens()
					updateERCDefaultTokenInfo()
					updateRAMUnitPrice()
					updateMyTokenCurrencyPrice()
					updateCPUUnitPrice()
					updateNETUnitPrice()
					checkAvailableEOSTokenList()
					updateNodeData()
					checkAvailableERC20TokenList()
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
				it.symbol.equals(CoinSymbol.eth, true)
			}.maxBy { it.blockNumber }?.blockNumber
		getERC20TokenTransactions(maxBlockNumber ?: "0")
	}

	private fun getERC20TokenTransactions(startBlock: String) {
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
					defaultDao.getToken(erc20.contract, erc20.tokenSymbol, chain.chainID.id)
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
			if (tokenList != null && error.isNone()) GoldStoneAPI.getIconURL(tokenList) { tokenIcons, getIconError ->
				val myTokenDao = GoldStoneDataBase.database.myTokenDao()
				if (tokenIcons != null && getIconError.isNone()) tokenList.forEach { contract ->
					//  这个接口只服务主网下的 `Token` 插入 `DefaultToken`
					DefaultTokenTable(
						contract,
						tokenIcons.get(contract.orEmpty())?.url.orEmpty()
					).preventDuplicateInsert()

					val targetToken = myTokenDao.getTokenByContractAndAddress(
						contract.contract.orEmpty(),
						contract.symbol,
						account.accountName,
						chainID.id
					)
					// 有可能用户本地已经插入并且被用户手动关闭了, 所以只有本地不存在的时候才插入
					// 插入 `MyTokenTable`
					if (targetToken.isNull()) myTokenDao.insert(
						MyTokenTable(
							0,
							account.accountName,
							SharedAddress.getCurrentEOS(),
							contract.symbol,
							0.0,
							contract.contract.orEmpty(),
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
						}?.isUsed.orElse(1)
					}
				}.let {
					nodeDao.insertAll(it)
				}
			}
		}
	}

	private fun updateMyTokenCurrencyPrice() {
		MyTokenTable.getMyTokens(false) { myTokens ->
			GoldStoneAPI.getPriceByContractAddress(
				// `EOS` 的 `Token` 价格在下面的方法从第三方获取, 这里过滤掉 `EOS` 的 `Token`
				myTokens.asSequence().filterNot {
					ChainID(it.chainID).isEOSMain() && !CoinSymbol(it.symbol).isEOS()
				}.map {
					"{\"address\":\"${it.contract}\",\"symbol\":\"${it.symbol}\"}"
				}.toList()
			) { newPrices, error ->
				if (newPrices != null && error.isNone()) {
					newPrices.forEach {
						DefaultTokenTable.updateTokenPrice(TokenContract(it.contract, it.symbol, null), it.price)
					}
				}
			}
			// 因为第三方接口 NewDex 没有列表查询的 API, 只能一个一个的请求, 这里可能会造成
			// 线程开启太多的内存溢出. 限制每 `500ms` 检查一个 `Symbol` 的价格
			object : ConcurrentAsyncCombine() {
				val eosTokens =
					myTokens.filter { ChainID(it.chainID).isEOSMain() }
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
		val defaultDao =
			GoldStoneDataBase.database.defaultTokenDao()
		GoldStoneAPI.getDefaultTokens { serverTokens, error ->
			if (serverTokens != null && serverTokens.isNotEmpty() && error.isNone()) {
				val localTokens = defaultDao.getAllTokens()
				// 开一个线程更新图片
				launch {
					updateLocalTokenIcon(serverTokens, localTokens)
				}
				// 移除掉一样的数据
				defaultDao.insertAll(
					serverTokens.filterNot { server ->
						localTokens.any { local ->
							local.chainID.equals(server.chainID, true)
								&& local.contract.equals(server.contract, true)
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
}