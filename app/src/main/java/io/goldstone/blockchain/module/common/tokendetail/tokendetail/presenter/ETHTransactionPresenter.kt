package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/20 2:51 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadETHChainData(localData: List<TransactionListModel>) {
	val blockNumber = localData.maxBy {
		it.blockNumber
	}?.blockNumber ?: "0"
	fragment.showLoadingView(LoadingText.transactionData)
	getTokenTransactions(
		blockNumber,
		{
			fragment.removeLoadingView()
			LogUtil.error("getTokenTransactions", it)
		}
	) {
		fragment.removeLoadingView()
		loadDataFromDatabaseOrElse { _, _ -> }
	}
}

fun checkAddressNameInContacts(
	transactions: List<TransactionListModel>,
	callback: () -> Unit
) {
	ContactTable.getAllContacts { contacts ->
		if (contacts.isEmpty()) {
			callback()
		} else {
			transactions.forEachOrEnd { item, isEnd ->
				item.addressName =
					contacts.find {
						// `BTC` 的 `toAddress` 可能是多地址, 所以采用了包含关系判断.
						it.ethERCAndETCAddress.equals(item.addressName, true)
							|| it.btcSeriesTestnetAddress.contains(item.addressName, true)
							|| it.btcMainnetAddress.contains(item.addressName, true)
					}?.name ?: item.addressName
				if (isEnd) {
					callback()
				}
			}
		}
	}
}

fun getTokenTransactions(
	startBlock: String,
	errorCallback: (Throwable) -> Unit,
	hold: (ArrayList<TransactionListModel>) -> Unit
) {
	getTransactionsFromEtherScan(startBlock, errorCallback) { hasData ->
		hasData.isNotEmpty() isTrue {
			TransactionTable.getERCTransactionsByAddress(Config.getCurrentEthereumAddress()) { transactions ->
				checkAddressNameInContacts(transactions) {
					hold(transactions)
				}
			}
		} otherwise {
			hold(arrayListOf())
		}
	}
}

// 默认拉取全部的 `EtherScan` 的交易数据
private fun getTransactionsFromEtherScan(
	startBlock: String,
	errorCallback: (Throwable) -> Unit,
	hold: (newData: List<TransactionListModel>) -> Unit
) {
	// 请求所有链上的数据
	mergeETHAndERC20Incoming(startBlock, errorCallback) {
		it.isNotEmpty() isTrue {
			filterCompletedData(it, hold)
		} otherwise {
			hold(listOf())
		}
	}.start()
}

private fun mergeETHAndERC20Incoming(
	startBlock: String,
	errorCallback: (Throwable) -> Unit,
	hold: (List<TransactionTable>) -> Unit
): ConcurrentAsyncCombine {
	return object : ConcurrentAsyncCombine() {
		override var asyncCount: Int = 2
		// Get transaction data from `etherScan`
		var chainData = listOf<TransactionTable>()
		var logData = listOf<TransactionTable>()
		var hasError = false
		override fun concurrentJobs() {
			doAsync {
				GoldStoneAPI.getTransactionListByAddress(
					startBlock,
					Config.getCurrentEthereumAddress(),
					{
						// 只弹出一次错误信息
						if (!hasError) {
							errorCallback(it)
							hasError = true
						}
						completeMark()
					}
				) {
					chainData = this
					completeMark()
				}

				GoldStoneAPI.getERC20TokenIncomingTransaction(
					startBlock,
					{
						//error callback
						// 只弹出一次错误信息
						if (!hasError) {
							errorCallback(it)
							hasError = true
						}
						completeMark()
					}
				) { it ->
					// 把请求回来的数据转换成 `TransactionTable` 格式
					logData = it.map {
						TransactionTable(ERC20TransactionModel(it))
					}
					completeMark()
				}
			}
		}

		override fun getResultInMainThread() = false
		override fun mergeCallBack() {
			diffNewDataAndUpdateLocalData(chainData.plus(logData)
				.filter {
					it.to.isNotEmpty()
				}.distinctBy {
					it.hash
				}.sortedByDescending {
					it.timeStamp
				}, hold)
		}
	}
}

private fun diffNewDataAndUpdateLocalData(
	newData: List<TransactionTable>,
	hold: List<TransactionTable>.() -> Unit
) {
	GoldStoneDataBase.database.transactionDao().apply {
		getTransactionsByAddress(
			Config.getCurrentEthereumAddress(),
			Config.getCurrentChain()
		).let { localData ->
			newData.filterNot { new ->
				localData.any {
					update(it.apply {
						transactionIndex = new.transactionIndex
						hasError = new.hasError
						txReceiptStatus = new.txReceiptStatus
						gasUsed = new.gasUsed
						blockHash = new.blockHash
						cumulativeGasUsed = new.cumulativeGasUsed
					})
					it.hash == new.hash
				}
			}.let {
				GoldStoneAPI.context.runOnUiThread {
					hold(it)
				}
			}
		}
	}
}

private fun List<TransactionTable>.getUnkonwTokenInfo(callback: () -> Unit) {
	DefaultTokenTable.getCurrentChainTokens { localTokens ->
		filter {
			it.isERC20Token && it.symbol.isEmpty()
		}.distinctBy {
			it.contractAddress
		}.filter { unknowData ->
			localTokens.find {
				it.contract.equals(unknowData.contractAddress, true)
			}.isNull()
		}.let { filterData ->
			if (filterData.isEmpty()) {
				callback()
				return@getCurrentChainTokens
			}
			object : ConcurrentAsyncCombine() {
				override var asyncCount = filterData.size
				override fun concurrentJobs() {
					filterData.forEach {
						GoldStoneEthCall.getSymbolAndDecimalByContract(
							it.contractAddress,
							{ error, reason ->
								completeMark()
								LogUtil.error("getUnkonwTokenInfo $reason", error)
							},
							Config.getCurrentChainName()
						) { symbol, decimal ->
							GoldStoneDataBase
								.database
								.defaultTokenDao()
								.insert(DefaultTokenTable(it.contractAddress, symbol, decimal))
							completeMark()
						}
					}
				}

				override fun getResultInMainThread() = false
				override fun mergeCallBack() = callback()
			}.start()
		}
	}
}

private fun filterCompletedData(
	data: List<TransactionTable>,
	hold: (newData: List<TransactionListModel>) -> Unit
) {
	// 从 `Etherscan` 拉取下来的没有 `Symbol, Decimal` 的数据从链上获取信息插入到 `DefaultToken` 数据库
	data.getUnkonwTokenInfo {
		// 把拉取到的数据加工数据格式并插入本地数据库
		completeTransactionInfo(data) list@{
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = size
				override fun concurrentJobs() {
					forEach {
						GoldStoneDataBase.database.transactionDao().insert(it)
						completeMark()
					}
				}

				override fun getResultInMainThread() = false
				override fun mergeCallBack() {
					this@list.afterInsertingMinerFeeToDatabase {
						hold(this@list.map { TransactionListModel(it) })
					}
				}
			}.start()
		}
	}
}

private fun List<TransactionTable>.afterInsertingMinerFeeToDatabase(callback: () -> Unit) {
	// 抽出燃气费的部分单独插入
	filter {
		if (!it.isReceive) it.isFee = true
		!it.isReceive
	}.apply list@{
		object : ConcurrentAsyncCombine() {
			override var asyncCount: Int = size
			override fun concurrentJobs() {
				forEach {
					GoldStoneDataBase
						.database
						.transactionDao()
						.insert(it)
					completeMark()
				}
			}

			override fun mergeCallBack() = callback()
		}.start()
	}
}

/**
 * 补全从 `EtherScan` 拉下来的账单中各种 `token` 的信息, 需要很多种线程情况, 这里使用异步并发观察结果
 * 在汇总到主线程.
 */
private fun completeTransactionInfo(
	data: List<TransactionTable>,
	hold: List<TransactionTable>.() -> Unit
) {
	DefaultTokenTable.getCurrentChainTokens { localTokens ->
		object : ConcurrentAsyncCombine() {
			override var asyncCount: Int = data.size
			override fun concurrentJobs() {
				data.forEach { transaction ->
					CryptoUtils.isERC20Transfer(transaction) {
						val contract =
							if (transaction.logIndex.isNotEmpty()) transaction.contractAddress
							else transaction.to
						var receiveAddress: String? = null
						var count = 0.0
						/** 从本地数据库检索 `contract` 对应的 `symbol` */
						localTokens.find {
							it.contract.equals(contract, true)
						}?.let { tokenInfo ->
							transaction.logIndex.isNotEmpty() isTrue {
								count = CryptoUtils.toCountByDecimal(
									transaction.value.toDouble(),
									tokenInfo.decimals.orZero()
								)
								receiveAddress = transaction.to
							} otherwise {
								// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
								val transactionInfo = CryptoUtils
									.loadTransferInfoFromInputData(transaction.input)
								count = CryptoUtils.toCountByDecimal(
									transactionInfo?.count.orElse(0.0),
									tokenInfo.decimals.orZero()
								)
								receiveAddress = transactionInfo?.address
							}

							TransactionTable.updateModelInfo(
								transaction,
								true,
								tokenInfo.symbol,
								count.toString(),
								receiveAddress
							)
							completeMark()
						}
					} isFalse {
						/** 不是 ERC20 币种直接默认为 `ETH` */
						TransactionTable.updateModelInfo(
							transaction,
							false,
							CryptoSymbol.eth,
							transaction.value.toDouble().toEthCount().toString(),
							transaction.to
						)
						completeMark()
					}
				}
			}

			override fun getResultInMainThread() = false
			override fun mergeCallBack() = hold(data)
		}.start()
	}
}
