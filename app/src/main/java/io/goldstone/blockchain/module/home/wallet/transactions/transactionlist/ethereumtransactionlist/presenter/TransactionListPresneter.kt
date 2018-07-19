package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view.TransactionListAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view.TransactionListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */
var memoryTransactionListData: ArrayList<TransactionListModel>? = null

class TransactionListPresenter(
	override val fragment: TransactionListFragment
) : BaseRecyclerPresenter<TransactionListFragment, TransactionListModel>() {
	
	override fun updateData() {
		fragment.showLoadingView(LoadingText.transactionData)
		if (memoryTransactionListData.isNull()) {
			// 先显示一个空数据
			fragment.asyncData = arrayListOf()
			// 不让 `UI` 觉得卡顿, 等动画执行完毕后再发起数据处理业务
			AnimationDuration.Default timeUpThen {
				// 如果内存中没有数据那么, 先展示界面动画在加载数据, 防止线程堆积导致的界面卡顿.
				fragment.initData()
			}
		} else {
			fragment.asyncData = memoryTransactionListData
			NetworkUtil.hasNetworkWithAlert(fragment.context) isTrue {
				updateTransactionInAsync(memoryTransactionListData!!) { hasData ->
					hasData isTrue {
						hasUpdate = true
						fragment.initData()
					}
					fragment.removeLoadingView()
				}
			}
		}
	}
	
	private var hasUpdate = false
	private fun TransactionListFragment.initData() {
		TransactionTable.getERCTransactionsByAddress(Config.getCurrentEthereumAddress()) {
			checkAddressNameInContacts(it) {
				presenter.diffAndUpdateSingleCellAdapterData<TransactionListAdapter>(it)
				// Save a copy into memory for imporving the speed of next time to view
				memoryTransactionListData = it
				// Check and update the new data from chain in async thread
				if (!hasUpdate) {
					updateTransactionInAsync(it) {
						it isTrue {
							hasUpdate = true
							initData()
						}
						removeLoadingView()
					}
				}
			}
		}
	}
	
	private fun updateTransactionInAsync(
		localData: ArrayList<TransactionListModel>,
		callback: (hasData: Boolean) -> Unit
	) {
		val currentBlockNumber =
			localData.firstOrNull { it.blockNumber.isNotEmpty() }?.blockNumber ?: "0"
		// 本地若有数据获取本地最近一条数据的 `BlockNumber` 作为 StartBlock 尝试拉取最新的数据
		getTransactionsFromEtherScan(
			currentBlockNumber,
			{
				// ToDo 等自定义的 `Alert` 完成后应当友好提示
				fragment.context
					?.alert("${AlertText.getTransactionErrorPrefix} ${ChainID.getChainNameByID(Config.getCurrentChain())} ${AlertText.getTransactionErrorSuffix}")
				LogUtil.error("error in GetTransactionDataFromEtherScan $it")
			}
		) {
			callback(it)
		}
	}
	
	companion object {
		
		fun checkAddressNameInContacts(
			transactions: ArrayList<TransactionListModel>,
			callback: () -> Unit
		) {
			ContactTable.getAllContacts { contacts ->
				if (contacts.isEmpty()) {
					callback()
				} else {
					transactions.forEachOrEnd { item, isEnd ->
						item.addressName =
							contacts.find {
								it.ethERCAndETCAddress.equals(item.targetAddress, true)
							}?.name ?: item.targetAddress
						if (isEnd) {
							callback()
						}
					}
				}
			}
		}
		
		fun showTransactionDetail(
			fragment: TransactionFragment?,
			model: TransactionListModel?,
			isFromTransactionList: Boolean = false
		) {
			fragment?.apply {
				presenter.showTargetFragment<TransactionDetailFragment>(
					TransactionText.detail,
					TransactionText.transaction,
					Bundle().apply {
						putSerializable(ArgumentKey.transactionFromList, model)
					},
					if (isFromTransactionList) TransactionFragment.viewPagerSize else 0
				)
			}
		}
		
		fun getTokenTransactions(
			startBlock: String,
			errorCallback: (Exception) -> Unit,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			getTransactionsFromEtherScan(startBlock, errorCallback) {
				it isTrue {
					TransactionTable.getERCTransactionsByAddress(Config.getCurrentEthereumAddress()) {
						checkAddressNameInContacts(it) { hold(it) }
					}
				} otherwise {
					hold(arrayListOf())
				}
			}
		}
		
		// 默认拉取全部的 `EtherScan` 的交易数据
		private fun getTransactionsFromEtherScan(
			startBlock: String,
			errorCallback: (Exception) -> Unit,
			hold: (hasNewData: Boolean) -> Unit
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
			errorCallback: (Exception) -> Unit,
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
						) {
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
					chainData.plus(logData).apply {
					}.filter {
						it.to.isNotEmpty()
					}.distinctBy {
						it.hash
					}.sortedByDescending {
						it.timeStamp
					}.let {
						diffNewDataAndUpdateLocalData(it, hold)
					}
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
								txreceipt_status = new.txreceipt_status
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
			hold: (hasData: Boolean) -> Unit
		) {
			// 从 `Etherscan` 拉取下来的没有 `Symbol, Decimal` 的数据从链上获取信息插入到 `DefaultToken` 数据库
			data.getUnkonwTokenInfo {
				// 把拉取到的数据加工数据格式并插入本地数据库
				completeTransactionInfo(data) list@{
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
						
						override fun getResultInMainThread() = false
						override fun mergeCallBack() {
							this@list.insertMinerFeeToDatabase {
								hold(distinctBy { new ->
									data.any {
										it.hash.equals(new.hash, true)
									}
								}.isNotEmpty())
							}
						}
					}.start()
				}
			}
		}
		
		private fun List<TransactionTable>.insertMinerFeeToDatabase(
			hold: (List<TransactionTable>) -> Unit
		) {
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
					
					override fun mergeCallBack() = hold(this@list)
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
	}
}