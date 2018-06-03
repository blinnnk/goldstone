package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */
// save data in memory for the next showing speed
var localTransactions: ArrayList<TransactionListModel>? = null

class TransactionListPresenter(
	override val fragment: TransactionListFragment
) : BaseRecyclerPresenter<TransactionListFragment, TransactionListModel>() {
	
	override fun updateData() {
		fragment.showLoadingView(LoadingText.transactionData)
		if (!localTransactions.isNull()) {
			fragment.asyncData = localTransactions
			/** show memory data and at the same time update the chain data in async thread */
			fragment.updateTransactionInAsync(localTransactions!!)
		} else {
			// 先显示一个空数据
			fragment.asyncData = arrayListOf()
			// 不让 `UI` 觉得卡顿, 等动画执行完毕后再发起数据处理业务
			AnimationDuration.Default timeUpThen {
				// 如果内存中没有数据那么, 先展示界面动画在加载数据, 防止线程堆积导致的界面卡顿.
				fragment.initData()
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
	
	private fun TransactionListFragment.initData() {
		TransactionTable.getTransactionListModelsByAddress(WalletTable.current.address) {
			if (it.isNotEmpty()) {
				presenter.diffAndUpdateSingleCellAdapterData<TransactionListAdapter>(it)
				updateParentContentLayoutHeight(it.size, fragment.setSlideUpWithCellHeight().orZero())
				localTransactions = it
				removeLoadingView()
			} else {
				/**
				 * if there is none data in local then `StartBlock 0`
				 * and load data from `EtherScan`
				 **/
				getTransactionDataFromEtherScan("0") {
					presenter.diffAndUpdateSingleCellAdapterData<TransactionListAdapter>(it)
					updateParentContentLayoutHeight(it.size, fragment.setSlideUpWithCellHeight().orZero())
					localTransactions = it
					removeLoadingView()
				}
			}
		}
	}
	
	private fun TransactionListFragment.updateTransactionInAsync(
		localData: ArrayList<TransactionListModel>
	) {
		// 本地可能存在 `pending` 状态的账目, 所以获取最近的 `blockNumber` 先剥离掉 `pending` 的类型
		val currentBlockNumber =
			localData.firstOrNull { it.blockNumber.isNotEmpty() }?.blockNumber
			?: "0"
		// 本地若有数据获取本地最近一条数据的 `BlockNumber` 作为 StartBlock 尝试拉取最新的数据
		getTransactionDataFromEtherScan(currentBlockNumber) { newData ->
			/** chain data is empty then return and remove loading view */
			if (newData.isEmpty()) {
				removeLoadingView()
				return@getTransactionDataFromEtherScan
			}
			// 拉取到新数据后检查是否包含本地已有的部分, 这种该情况会出现在, 本地转账后插入临时数据的条目。
			newData.forEachOrEnd { item, isEnd ->
				localData.find {
					it.transactionHash == item.transactionHash
				}?.let {
					localData.remove(it)
					TransactionTable.deleteByTaxHash(it.transactionHash)
				}
				if (isEnd) {
					// when finish update ui in UI thread
					context?.runOnUiThread {
						localData.addAll(0, newData)
						// save data into the memory for the next time showing speed
						localTransactions = localData
						presenter.diffAndUpdateSingleCellAdapterData<TransactionListAdapter>(localData)
						removeLoadingView()
					}
				}
			}
		}
	}
	
	companion object {
		
		// 默认拉取全部的 `EtherScan` 的交易数据
		private fun BaseRecyclerFragment<*, *>.getTransactionDataFromEtherScan(
			startBlock: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			// 没有网络直接返回
			if (!NetworkUtil.hasNetworkWithAlert(getContext())) return
			// 请求所有链上的数据
			mergeNormalAndTokenIncomingTransactions(startBlock) {
				it.isNotEmpty() isTrue {
					// 因为进入这里之前外部已经更新了最近的 `BlockNumber`, 所以这里的数据可以直接理解为最新的本地没有的部分
					filterCompletedData(it, hold)
				} otherwise {
					this.getContext()?.runOnUiThread {
						// if data is empty then return an empty array
						hold(arrayListOf())
					}
				}
			}.start()
		}
		
		fun updateTransactions(
			fragment: BaseRecyclerFragment<*, *>,
			startBlock: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			fragment.getTransactionDataFromEtherScan(startBlock, hold)
		}
		
		private fun mergeNormalAndTokenIncomingTransactions(
			startBlock: String,
			hold: (ArrayList<TransactionTable>) -> Unit
		): ConcurrentAsyncCombine {
			return object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = 2
				// Get transaction data from `etherScan`
				var chainData = ArrayList<TransactionTable>()
				var logData = ArrayList<TransactionTable>()
				override fun concurrentJobs() {
					doAsync {
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
				}
				
				override fun mergeCallBack() {
					coroutinesTask(
						{
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
						}, hold
					)
				}
			}
		}
		
		private fun ArrayList<TransactionTable>.getUnkonwTokenInfoByTransactions(callback: () -> Unit) {
			filter {
				it.isERC20 && it.symbol.isEmpty()
			}.distinctBy {
				it.contractAddress
			}.apply {
				if (isEmpty()) callback()
				object : ConcurrentAsyncCombine() {
					override var asyncCount = size
					override fun concurrentJobs() {
						forEach {
							GoldStoneEthCall.apply {
								getTokenInfoByContractAddress(it.contractAddress) { symbol, name, decimal ->
									GoldStoneDataBase
										.database
										.defaultTokenDao()
										.insert(DefaultTokenTable(it.contractAddress, symbol, decimal, name))
									completeMark()
								}
							}
						}
					}
					
					override fun mergeCallBack() = callback()
				}.start()
			}
		}
		
		private fun BaseRecyclerFragment<*, *>.filterCompletedData(
			data: ArrayList<TransactionTable>,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			// 从 `Etherscan` 拉取下来的没有 `Symbol, Decimal` 的数据从链上获取信息插入到 `DefaultToken` 数据库
			data.getUnkonwTokenInfoByTransactions {
				// 把拉取到的数据加工数据格式并插入本地数据库
				completeTransactionInfo(data) {
					object : ConcurrentAsyncCombine() {
						override var asyncCount: Int = size
						override fun concurrentJobs() {
							forEach { transactionTable ->
								if (transactionTable.isERC20) {
									GoldStoneDataBase
										.database
										.transactionDao()
										.insert(transactionTable)
									completeMark()
								} else {
									GoldStoneDataBase
										.database
										.transactionDao()
										.insert(transactionTable)
									completeMark()
								}
							}
						}
						
						override fun mergeCallBack() {
							hold(map { TransactionListModel(it) }.toArrayList())
							removeLoadingView()
						}
					}.start()
				}
			}
		}
		
		/**
		 * 补全从 `EtherScan` 拉下来的账单中各种 `token` 的信息, 需要很多种线程情况, 这里使用异步并发观察结果
		 * 在汇总到主线程.
		 */
		private fun completeTransactionInfo(
			data: ArrayList<TransactionTable>,
			hold: ArrayList<TransactionTable>.() -> Unit
		) {
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = data.size
				override fun concurrentJobs() {
					data.forEach { transaction ->
						CryptoUtils.isERC20Transfer(transaction) {
							val contract = if (transaction.logIndex.isNotEmpty()) transaction.contractAddress
							else transaction.to
							var receiveAddress = ""
							var count = 0.0
							/** 首先从本地数据库检索 `contract` 对应的 `symbol` */
							DefaultTokenTable.getCurrentChainTokenByContract(contract) { tokenInfo ->
								transaction.logIndex.isNotEmpty() isTrue {
									count = CryptoUtils.toCountByDecimal(
										transaction.value.toDouble(),
										tokenInfo?.decimals.orElse(0.0)
									)
									receiveAddress = transaction.to
								} otherwise {
									// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
									val transactionInfo = CryptoUtils.loadTransferInfoFromInputData(transaction.input)
									count = CryptoUtils.toCountByDecimal(
										transactionInfo?.count.orElse(0.0),
										tokenInfo?.decimals.orElse(0.0)
									)
									receiveAddress = transactionInfo?.address!!
								}
								
								tokenInfo.isNull() isTrue {
									// 如果本地没有检索到 `contract` 对应的 `symbol` 则从链上查询
									TransactionTable.updateModelInfoFromChain(
										transaction,
										true,
										"",
										transaction.value,
										receiveAddress
									)
									completeMark()
								} otherwise {
									TransactionTable.updateModelInfoFromChain(
										transaction,
										true,
										tokenInfo?.symbol!!,
										count.toString(),
										receiveAddress
									)
									completeMark()
								}
							}
						} isFalse {
							/** 不是 ERC20 币种直接默认为 `ETH` */
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
				
				override fun mergeCallBack() = hold(data)
			}.start()
		}
	}
}