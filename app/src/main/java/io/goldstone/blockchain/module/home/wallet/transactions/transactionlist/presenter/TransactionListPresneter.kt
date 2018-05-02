package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter

import android.os.Bundle
import android.util.Log
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListFragment

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */

// 从数据库去除交易记录存放到内存里, 提升用户体验.
var localTransactions: ArrayList<TransactionListModel>? = null

class TransactionListPresenter(
	override val fragment: TransactionListFragment
) : BaseRecyclerPresenter<TransactionListFragment, TransactionListModel>() {

	override fun updateData() {
		fragment.apply {
			localTransactions.isNotNull {
				asyncData = localTransactions
				// 更新显示数据后, 异步继续更新新的数据.并动态刷新到界面
				getMainActivity()?.updateTransactionInAsync(localTransactions!!)
			} otherwise {
				TransactionTable.getTransactionListModelsByAddress(WalletTable.current.address) { localData ->
					localData.isNotEmpty() isTrue {
						asyncData = localData
						localTransactions = localData
					} otherwise {
						// 如果本地一条数据都没有就从 `StartBlock 0` 的位置从 `EtherScan` 上查询
						getMainActivity()?.getTransactionDataFromEtherScan("0") {
							asyncData = it
							localTransactions = it
						}
					}
					updateParentContentLayoutHeight(fragment.asyncData?.size)
				}
			}
		}
	}

	fun showTransactionDetail(model: TransactionListModel?) {
		fragment.getParentFragment<TransactionFragment>()?.apply {
			Bundle().apply {
				putSerializable(ArgumentKey.transactionFromList, model)
				presenter.showTargetFragment<TransactionDetailFragment>(
					TransactionText.detail, TransactionText.transaction, this
				)
			}
		}
	}

	private fun MainActivity.updateTransactionInAsync(localData: ArrayList<TransactionListModel>) {
		// 本地可能存在 `pending` 状态的账目, 所以获取最近的 `blockNumber` 先剥离掉 `pending` 的类型
		val lastBlockNumber = localData.first { it.blockNumber.isNotEmpty() }.blockNumber + 1
		// 本地若有数据获取本地最近一条数据的 `BlockNumber` 作为 StartBlock 尝试拉取最新的数据
		getTransactionDataFromEtherScan(lastBlockNumber) { newData ->
			// 拉取到新数据后检查是否包含本地已有的部分, 这种该情况会出现在, 本地转账后插入临时数据的条目。
			newData.forEachOrEnd { item, isEnd ->
				localData.find {
					it.transactionHash == item.transactionHash
				}?.let {
					localData.remove(it)
					TransactionTable.deleteByTaxHash(it.transactionHash)
				}
				if (isEnd) {
					// 数据清理干净后在主线程更新 `UI`
					runOnUiThread {
						// 拉取到后, 把最新获取的数据合并本地数据更新到界面
						localData.addAll(0, newData)
						// 把数据存到内存里面, 下次打开直接使用内存, 不用再度数据库，提升用户体验.
						localTransactions = localData
						fragment.asyncData = localTransactions
						fragment.recyclerView.adapter.notifyDataSetChanged()
					}
				}
			}
			Log.d("DEBUG", "updated new transaction data")
		}
	}

	companion object {

		// 默认拉取全部的 `EtherScan` 的交易数据
		private fun MainActivity.getTransactionDataFromEtherScan(
			startBlock: String, hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			// Show loading view
			showLoadingView()
			mergeNormalAndTokenIncomingTransactions(startBlock) {
				it.isNotEmpty() isTrue {
					// 因为进入这里之前外部已经更新了最近的 `BlockNumber`, 所以这里的数据可以直接理解为最新的本地没有的部分
					filterCompletedData(it, hold)
					Log.d("DEBUG", "update the new data from chain")
				} otherwise {
					runOnUiThread {
						removeLoadingView()
						// 没有数据返回空数组
						hold(arrayListOf())
					}
				}
			}.start()
		}

		fun updateTransactions(
			activity: MainActivity?, startBlock: String, hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			activity?.getTransactionDataFromEtherScan(startBlock, hold)
		}

		private fun mergeNormalAndTokenIncomingTransactions(
			startBlock: String, hold: (ArrayList<TransactionTable>) -> Unit
		): ConcurrentAsyncCombine {
			return object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = 2
				// Get transaction data from `etherScan`
				var chainData = ArrayList<TransactionTable>()
				var logData = ArrayList<TransactionTable>()
				override fun concurrentJobs() {
					GoldStoneAPI.getTransactionListByAddress(startBlock) {
						chainData = this
						completeMark()
					}
					GoldStoneAPI.getERC20TokenIncomingTransaction(startBlock) {
						// 把请求回来的数据转换成 `TransactionTable` 格式
						logData = it.map { TransactionTable(ERC20TransactionModel(it)) }.toArrayList()
						completeMark()
					}
				}

				override fun mergeCallBack() {
					coroutinesTask({
						arrayListOf<TransactionTable>().apply {
							addAll(chainData)
							addAll(logData)
						}.filter {
							it.to.isNotEmpty() && it.value.toDouble() > 0.0
						}.distinctBy {
							it.hash
						}.sortedByDescending {
							it.timeStamp
						}.toArrayList()
					}) {
						hold(it)
					}
				}
			}
		}

		private fun MainActivity.filterCompletedData(
			data: ArrayList<TransactionTable>, hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			// 把拉取到的数据加工数据格式并插入本地数据库
			completeTransactionInfo(data) {
				object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = size
					override fun concurrentJobs() {
						forEach {
							GoldStoneDataBase.database.transactionDao().insert(it)
							completeMark()
						}
					}

					override fun mergeCallBack() {
						removeLoadingView()
						hold(map { TransactionListModel(it) }.toArrayList())
					}
				}.start()
			}
		}

		/**
		 * 补全从 `EtherScan` 拉下来的账单中各种 `token` 的信息, 需要很多种线程情况, 这里使用异步并发观察结果
		 * 在汇总到主线程.
		 */
		private fun completeTransactionInfo(
			data: ArrayList<TransactionTable>, hold: ArrayList<TransactionTable>.() -> Unit
		) {
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = data.size
				override fun concurrentJobs() {
					data.forEach { transaction ->
						CryptoUtils.isERC20Transfer(transaction) {
							val contract =
								if (transaction.logIndex.isNotEmpty()) transaction.contractAddress else transaction.to
							var receiveAddress = ""
							var count = 0.0
							// 首先从本地数据库检索 `contract` 对应的 `symbol`
							DefaultTokenTable.getTokenByContractAddress(contract) { tokenInfo ->
								transaction.logIndex.isNotEmpty() isTrue {
									count = CryptoUtils.toCountByDecimal(
										transaction.value.toDouble(), tokenInfo?.decimals.orElse(0.0)
									)
									receiveAddress = transaction.to
								} otherwise {
									// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
									val transactionInfo = CryptoUtils.loadTransferInfoFromInputData(transaction.input)
									count = CryptoUtils.toCountByDecimal(
										transactionInfo?.count.orElse(0.0), tokenInfo?.decimals.orElse(0.0)
									)
									receiveAddress = transactionInfo?.address!!
								}

								tokenInfo.isNull() isTrue {
									// 如果本地没有检索到 `contract` 对应的 `symbol` 则从链上查询
									GoldStoneEthCall.getTokenSymbol(contract) { tokenSymbol ->
										TransactionTable.updateModelInfoFromChain(
											transaction, true, tokenSymbol, count.toString(), receiveAddress
										)
										completeMark()
									}
								} otherwise {
									TransactionTable.updateModelInfoFromChain(
										transaction, true, tokenInfo!!.symbol, count.toString(), receiveAddress
									)
									completeMark()
								}
							}
						}.isFalse {
							TransactionTable.updateModelInfoFromChain(
								transaction,
								false,
								CryptoSymbol.eth,
								CryptoUtils.toCountByDecimal(transaction.value.toDouble()).toString(),
								transaction.to
							)
							completeMark()
						}
					}
				}

				override fun mergeCallBack() {
					hold(data)
				}
			}.start()
		}
	}
}