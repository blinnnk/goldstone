package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
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
		loadDataFromDatabaseOrElse()
	}
}

fun checkAddressNameInContacts(
	transactions: List<TransactionListModel>,
	@UiThread callback: () -> Unit
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
	@UiThread hold: (List<TransactionListModel>) -> Unit
) {
	getTransactionsFromEtherScan(startBlock, errorCallback) { hasData ->
		hasData isTrue {
			TransactionTable.getERCTransactionsByAddress(Config.getCurrentEthereumAddress()) { transactions ->
				checkAddressNameInContacts(transactions) {
					hold(transactions)
				}
			}
		} otherwise {
			GoldStoneAPI.context.runOnUiThread { hold(arrayListOf()) }
		}
	}
}

// 默认拉取全部的 `EtherScan` 的交易数据
private fun getTransactionsFromEtherScan(
	startBlock: String,
	errorCallback: (Throwable) -> Unit,
	@WorkerThread hold: (hasData: Boolean) -> Unit
) {
	// 请求所有链上的数据
	mergeETHAndERC20Incoming(startBlock, errorCallback) {
		it.isNotEmpty() isTrue {
			filterCompletedData(it, hold)
		} otherwise {
			hold(false)
		}
	}.start()
}

private fun mergeETHAndERC20Incoming(
	startBlock: String,
	errorCallback: (Throwable) -> Unit,
	@WorkerThread hold: (List<TransactionTable>) -> Unit
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
					},
					Config.getCurrentEthereumAddress()
				) { erc20Data ->
					// 把请求回来的数据转换成 `TransactionTable` 格式
					logData = erc20Data.map { TransactionTable(ERC20TransactionModel(it)) }
					completeMark()
				}
			}
		}

		override fun getResultInMainThread() = false
		override fun mergeCallBack() {
			diffNewDataAndUpdateLocalData(chainData.asSequence().plus(logData)
				.filter {
					it.to.isNotEmpty()
				}.distinctBy {
					it.hash
				}.sortedByDescending {
					it.timeStamp
				}.toList(), hold)
		}
	}
}

private fun diffNewDataAndUpdateLocalData(
	newData: List<TransactionTable>,
	@WorkerThread hold: List<TransactionTable>.() -> Unit
) {
	GoldStoneDataBase.database.transactionDao().apply {
		getTransactionsByAddress(
			Config.getCurrentEthereumAddress(),
			Config.getCurrentChain().id
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
			}.let(hold)
		}
	}
}

private fun List<TransactionTable>.getUnknownTokenInfo(callback: (List<DefaultTokenTable>) -> Unit) {
	DefaultTokenTable.getCurrentChainTokens { localTokens ->
		filter { transaction ->
			transaction.isERC20Token && transaction.symbol.isEmpty()
		}.asSequence().distinctBy {
			it.contractAddress
		}.filter { unknownData ->
			localTokens.find {
				it.contract.equals(unknownData.contractAddress, true)
			}.isNull()
		}.toList().let { filterData ->
			if (filterData.isEmpty()) {
				callback(localTokens)
				return@getCurrentChainTokens
			}
			object : ConcurrentAsyncCombine() {
				override var asyncCount = filterData.size
				override fun concurrentJobs() {
					filterData.forEach { transaction ->
						GoldStoneEthCall.getSymbolAndDecimalByContract(
							transaction.contractAddress,
							{
								completeMark()
								LogUtil.error("getUnknownTokenInfo ", it)
							},
							Config.getCurrentChainName()
						) { symbol, decimal ->
							GoldStoneDataBase.database.defaultTokenDao().insert(DefaultTokenTable(transaction.contractAddress, symbol, decimal))
							completeMark()
						}
					}
				}

				override fun getResultInMainThread() = false
				override fun mergeCallBack() {
					callback(localTokens)
				}
			}.start()
		}
	}
}

private fun filterCompletedData(
	data: List<TransactionTable>,
	hold: (hasData: Boolean) -> Unit
) {
	// 从 `EtherScan` 拉取下来的没有 `Symbol, Decimal` 的数据从链上获取信息插入到 `DefaultToken` 数据库
	data.getUnknownTokenInfo { localTokens ->
		// 把拉取到的数据加工数据格式并插入本地数据库
		completeTransactionInfo(data, localTokens) {
			GoldStoneDataBase.database.transactionDao().insertAll(this)
			insertMinerFeeToDatabase {
				hold(isNotEmpty())
			}
		}
	}
}

private fun List<TransactionTable>.insertMinerFeeToDatabase(callback: () -> Unit) {
	// 抽出燃气费的部分单独插入
	filter {
		if (!it.isReceive) it.isFee = true
		!it.isReceive
	}.apply {
		GoldStoneDataBase.database.transactionDao().insertAll(this)
		callback()
	}
}

/**
 * 补全从 `EtherScan` 拉下来的账单中各种 `token` 的信息, 需要很多种线程情况, 这里使用异步并发观察结果
 * 在汇总到主线程.
 */
private fun completeTransactionInfo(
	data: List<TransactionTable>,
	localTokens: List<DefaultTokenTable>,
	@WorkerThread hold: List<TransactionTable>.() -> Unit
) {
	object : ConcurrentAsyncCombine() {
		override var asyncCount: Int = data.size
		override fun concurrentJobs() {
			data.forEach { transaction ->
				CryptoUtils.isERC20Transfer(transaction.input, transaction.logIndex) {
					val contract =
						if (transaction.logIndex.isNotEmpty()) transaction.contractAddress
						else transaction.to
					var receiveAddress: String? = null
					var count = 0.0
					/** 从本地数据库检索 `contract` 对应的 `symbol` */
					val targetToken = localTokens.find { it.contract.equals(contract, true) }
					if (targetToken.isNull()) completeMark() // 如果找不到对应的数据就标记完成一次查询
					targetToken?.let { tokenInfo ->
						transaction.logIndex.isNotEmpty() isTrue {
							count = CryptoUtils.toCountByDecimal(transaction.value.toBigInteger(), tokenInfo.decimals.orZero())
							receiveAddress = transaction.to
						} otherwise {
							// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
							val transactionInfo = CryptoUtils.getTransferInfoFromInputData(transaction.input)
							count = CryptoUtils.toCountByDecimal(transactionInfo?.amount!!, tokenInfo.decimals.orZero())
							receiveAddress = transactionInfo.address
						}

						transaction.updateModelInfo(
							true,
							tokenInfo.symbol,
							count.toString(),
							receiveAddress
						)
						completeMark()
					}
				} isFalse {
					/** 不是 ERC20 币种直接默认为 `ETH` */
					transaction.updateModelInfo(
						false,
						CoinSymbol.eth,
						transaction.value.toBigInteger().toEthCount().toString(),
						transaction.to
					)
					completeMark()
				}
			}
		}

		override fun getResultInMainThread() = false
		override fun mergeCallBack() {
			hold(data)
		}
	}.start()
}
