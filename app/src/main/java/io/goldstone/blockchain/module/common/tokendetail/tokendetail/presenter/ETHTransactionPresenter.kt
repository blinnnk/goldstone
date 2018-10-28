package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
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
	getTokenTransactions(blockNumber) {
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
						it.ethSeriesAddress.equals(item.addressName, true)
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
	@UiThread hold: (List<TransactionListModel>) -> Unit
) {
	getTransactionsFromEtherScan(startBlock) { hasData ->
		if (hasData) TransactionTable.getTokenTransactions(
			SharedAddress.getCurrentEthereum()
		) { transactions ->
			checkAddressNameInContacts(transactions) {
				hold(transactions)
			}
		} else GoldStoneAPI.context.runOnUiThread { hold(arrayListOf()) }
	}
}

// 默认拉取全部的 `EtherScan` 的交易数据
private fun getTransactionsFromEtherScan(
	startBlock: String,
	@WorkerThread hold: (hasData: Boolean) -> Unit
) {
	// 请求所有链上的数据
	mergeETHAndERC20Incoming(startBlock) { transactions, error ->
		if (!transactions.isNull() && error.isNone()) {
			if (transactions!!.isNotEmpty()) filterCompletedData(transactions, hold)
			else hold(false)
		} else hold(false)
	}.start()
}

private fun mergeETHAndERC20Incoming(
	startBlock: String,
	@WorkerThread hold: (transactions: List<TransactionTable>?, error: RequestError) -> Unit
): ConcurrentAsyncCombine {
	return object : ConcurrentAsyncCombine() {
		override var asyncCount: Int = 2
		// Get transaction data from `etherScan`
		var chainData = listOf<TransactionTable>()
		var logData = listOf<TransactionTable>()
		override fun concurrentJobs() {
			doAsync {
				GoldStoneAPI.getTransactionListByAddress(
					startBlock,
					SharedAddress.getCurrentEthereum()
				) { transactions, error ->
					if (!transactions.isNull() && error.isNone()) {
						chainData = transactions!!
					} else hold(null, error)
					completeMark()
				}
				GoldStoneAPI.getERC20TokenIncomingTransaction(
					startBlock,
					SharedAddress.getCurrentEthereum()
				) { erc20Data, error ->
					if (!erc20Data.isNull() && error.isNone()) {
						// 把请求回来的数据转换成 `TransactionTable` 格式
						logData = erc20Data!!.map {
							TransactionTable(ERC20TransactionModel(it))
						}
					}
					completeMark()
				}
			}
		}

		override fun getResultInMainThread() = false
		override fun mergeCallBack() {
			diffNewDataAndUpdateLocalData(chainData.asSequence().plus(logData).filter {
				it.to.isNotEmpty()
			}.distinctBy { it.hash }.toList()) {
				hold(this, RequestError.None)
			}
		}
	}
}

private fun diffNewDataAndUpdateLocalData(
	newData: List<TransactionTable>,
	@WorkerThread hold: List<TransactionTable>.() -> Unit
) {
	GoldStoneDataBase.database.transactionDao().apply {
		getTransactionsByAddress(
			SharedAddress.getCurrentEthereum(),
			SharedChain.getCurrentETH().id
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
	val unknownData =
		arrayListOf<DefaultTokenTable>()
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
							SharedChain.getCurrentETHName()
						) { symbol, decimal ->
							unknownData.add(
								DefaultTokenTable(
									transaction.contractAddress,
									symbol,
									decimal,
									SharedChain.getCurrentETH(),
									""
								)
							)
							completeMark()
						}
					}
				}

				override fun getResultInMainThread() = false
				override fun mergeCallBack() {
					GoldStoneDataBase.database.defaultTokenDao().insertAll(unknownData)
					// 把更新数据的 `DefaultToken` 和内存中待使用的 `DefaultToken List` 合并更新方便在后
					// 续缓解中使用最新的数据又不用重新开启数据库请求
					callback(localTokens.asSequence().plus(unknownData).filterNot { it.symbol.isEmpty() }.toList())
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
				if (CryptoUtils.isERC20Transfer(transaction.input)) {
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
				} else {
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
