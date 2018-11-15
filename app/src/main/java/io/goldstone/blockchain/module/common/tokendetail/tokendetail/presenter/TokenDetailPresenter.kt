package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.contract.TokenDetailContract
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */
@Suppress("UNCHECKED_CAST")
class TokenDetailPresenter(
	val token: WalletDetailCellModel,
	val detailView: TokenDetailContract.GSView
) : TokenDetailContract.GSPresenter {

	var allData: List<TransactionListModel>? = null

	override fun start() {
		detailView.showLoading(true)
		updateEmptyCharData()
		checkNewDataFromChain()
	}

	var totalCount: Int? = null
	var currentMaxCount: Int? = null

	override fun loadMore() {
		if (detailView.currentMenu != CommonText.all) return
		detailView.showBottomLoading(true)
		with(token.contract) {
			GlobalScope.launch(Dispatchers.Default) {
				when {
					isBTCSeries() -> getBTCSeriesData()
					isETHSeries() -> getETHSeriesData()
					isEOSSeries() -> {
						if (
							totalCount == null
							|| currentMaxCount == null
							|| currentMaxCount ?: 0 <= 0
							|| detailView.asyncData?.size == totalCount
						) launchUI {
							detailView.showBottomLoading(false)
						} else flipEOSPage {
							launchUI {
								detailView.showBottomLoading(false)
								detailView.showLoading(false)
							}
						}
					}
				}
			}
		}
	}

	override fun showOnlyReceiveData() {
		fun sortData() {
			allData?.filter { it.isReceived }?.let {
				detailView.updateDataChange(it.toArrayList())
				if (it.isEmpty()) detailView.showBottomLoading(false)
			}
		}
		if (token.contract.isEOSSeries() && allData.isNullOrEmpty()) {
			GlobalScope.launch(Dispatchers.Default) {
				currentMaxCount = totalCount
				detailView.getDetailAdapter()?.dataSet?.clear()
				flipEOSPage { sortData() }
			}
		} else sortData()
	}

	override fun showOnlyFailedData() {
		allData?.filter { it.hasError }?.let {
			detailView.updateDataChange(it.toArrayList())
			if (it.isEmpty()) detailView.showBottomLoading(false)
		}
	}

	override fun showOnlySentData() {
		fun sortData() {
			allData?.filter { !it.isReceived && !it.isFee }?.let {
				detailView.updateDataChange(it.toArrayList())
				if (it.isEmpty()) detailView.showBottomLoading(false)
			}
		}
		if (token.contract.isEOSSeries() && allData.isNullOrEmpty()) {
			GlobalScope.launch(Dispatchers.Default) {
				currentMaxCount = totalCount
				detailView.getDetailAdapter()?.dataSet?.clear()
				flipEOSPage { sortData() }
			}
		} else sortData()
	}

	override fun showAllData() {
		fun sortData() {
			allData?.let {
				detailView.updateDataChange(it.toArrayList())
				if (it.isEmpty()) detailView.showBottomLoading(false)
			}
		}
		if (token.contract.isEOSSeries()) GlobalScope.launch(Dispatchers.Default) {
			currentMaxCount = totalCount
			detailView.getDetailAdapter()?.dataSet?.clear()
			flipEOSPage { sortData() }
		} else sortData()
	}

	@WorkerThread
	private fun checkNewDataFromChain() = GlobalScope.launch(Dispatchers.Default) {

		with(token.contract) {
			when {
				isBTCSeries() -> loadBTCSeriesData(getChainType(), getMaxDataIndex(), true)
				isETHSeries() -> when {
					isETC() -> loadETCChainData(getMaxBlockNumber())
					isETH() -> loadETHChainData(getMaxBlockNumber())
					isERC20Token() -> loadERCChainData(getMaxBlockNumber())
				}
				isEOSSeries() -> getEOSSeriesData()
			}
		}
	}

	/**
	 * EtherScan 的分页逻辑是依靠 BlockNumber 做分页依据的.
	 * ( 因为 EtherScan 没有对应的 Index 所以很难在本地管理)
	 * 当本地没有数据的时候其实的 EndIndex 设为一个超大数, 配合 Offset 就可以
	 * 拉取到最新的 Offset 的数据. 之后用本地的最大 BlockNumber 或 页内最小 BlockNumber
	 * 来作为拉取最新或拉取历史数据的依据参数.
	 */
	private fun getMaxBlockNumber(): Int {
		return TransactionTable.dao.getMaxBlockNumber(
			token.contract.getAddress(),
			token.contract.contract,
			token.chainID
		)?.blockNumber ?: 99999999
	}

	private fun getMaxDataIndex(): Int {
		return BTCSeriesTransactionTable.dao.getMaxDataIndex(
			token.contract.getAddress(),
			token.contract.getChainType().id
		)?.dataIndex.orZero()
	}

	private fun getEOSSeriesData() {
		// 创建的时候准备相关的账单数据, 服务本地网络混合分页的逻辑
		val codeName = token.contract.contract
		if (!NetworkUtil.hasNetwork(GoldStoneAPI.context)) {
			EOSTransactionTable.getMaxDataIndexTable(
				SharedAddress.getCurrentEOSAccount(),
				token.contract,
				SharedChain.getEOSCurrent().chainID
			) {
				launchUI {
					totalCount = it?.dataIndex
					currentMaxCount = it?.dataIndex
					// 初次加载的时候, 这个逻辑会复用到监听转账的 Pending Data 的状态更改.
					// 当 `PendingData Observer` 调用这个方法的时候让数据重新加载显示, 来达到更新 `Pending Status` 的效果
					detailView.getDetailAdapter()?.dataSet?.clear()
					// 初始化
					loadMore()
				}
			}
		} else EOSAPI.getTransactionCount(
			SharedChain.getEOSCurrent().chainID,
			SharedAddress.getCurrentEOSAccount(),
			codeName,
			token.symbol
		) { count, error ->
			if (count.hasValue() && error.isNone()) {
				totalCount = count
				currentMaxCount = count
				// 初次加载的时候, 这个逻辑会复用到监听转账的 Pending Data 的状态更改.
				// 当 `PendingData Observer` 调用这个方法的时候让数据重新加载显示, 来达到更新 `Pending Status` 的效果
				detailView.getDetailAdapter()?.dataSet?.clear()
				launchUI { loadMore() }
			} else launchUI {
				detailView.showLoading(false)
				detailView.showBottomLoading(false)
			}
		}
	}

	@WorkerThread
	fun getETHSeriesData() {
		val address = token.contract.getAddress()
		val transactionDao = TransactionTable.dao
		val endBlock = if (detailView.asyncData.isNullOrEmpty()) {
			getMaxBlockNumber()
		} else detailView.asyncData?.minBy { it.blockNumber }?.blockNumber!! - 1
		if (endBlock.hasValue()) {
			val transactions =
				if (token.contract.isETH()) transactionDao.getETHAndAllFee(
					address,
					token.contract.contract,
					endBlock,
					token.chainID
				) else transactionDao.getDataWithFee(
					address,
					token.contract.contract,
					token.chainID,
					endBlock
				)
			when {
				transactions.isNotEmpty() -> flipPage(transactions) {
					detailView.showBottomLoading(false)
					detailView.showLoading(false)
				}
				else -> when {
					token.contract.isETH() -> loadETHChainData(endBlock)
					else -> launchUI {
						detailView.showBottomLoading(false)
					}
				}
			}
		} else launchUI {
			detailView.showBottomLoading(false)
		}
	}

	@WorkerThread
	fun getBTCSeriesData() {
		val btcSeriesDao = BTCSeriesTransactionTable.dao
		with(token.contract) {
			val startDataIndex =
				if (detailView.asyncData.isNullOrEmpty()) {
					btcSeriesDao.getMaxDataIndex(
						getAddress(),
						getChainType().id
					)?.dataIndex
				} else detailView.asyncData?.minBy { it.dataIndex }?.dataIndex!! - 1
			if (startDataIndex.hasValue()) {
				val transactions =
					btcSeriesDao.getDataByRange(
						getAddress(),
						getChainType().id,
						startDataIndex - DataValue.pageCount,
						startDataIndex
					)
				when {
					transactions.isNotEmpty() -> flipPage(transactions) {
						detailView.showBottomLoading(false)
						detailView.showLoading(false)
					}
					else -> loadBTCSeriesData(getChainType(), startDataIndex + 1, false)
				}
			} else launchUI {
				detailView.showBottomLoading(false)
			}
		}
	}

	private fun updatePageBy(data: List<TransactionListModel>) {
		allData = data
		checkAddressNameInContacts(data) {
			launchUI {
				// 防止用户在加载数据过程中切换到别的 `Tab` 这里复位一下
				detailView.setAllMenu()
				detailView.updateDataChange(data.toArrayList())
			}
			// 显示内存的数据后异步更新数据
			data.generateBalanceList(token.contract) {
				it.updateHeaderData(false)
			}
		}
	}

	// 没数据或初始化的时候先生产默认值显示
	private fun updateEmptyCharData() = GlobalScope.launch(Dispatchers.Default) {
		listOf<TransactionListModel>()
			.generateBalanceList(token.contract) {
				it.updateHeaderData(true)
			}
	}

	@UiThread
	fun List<TokenBalanceTable>.updateHeaderData(isPlaceholderData: Boolean) {
		val maxChartCount = 7
		val chartArray = arrayListOf<ChartPoint>()
		val charCount = if (size > maxChartCount) maxChartCount else size
		forEach {
			chartArray.add(
				ChartPoint(TimeUtils.formatMdDate(it.date), it.balance.toBigDecimal().toFloat())
			)
			if (chartArray.size == charCount) launchUI {
				detailView.setChartData(chartArray.reversed().toArrayList())
				if (!isPlaceholderData) detailView.showLoading(false)
			}
		}
	}

	@WorkerThread
	fun List<TransactionListModel>.generateBalanceList(
		contract: TokenContract,
		callback: (List<TokenBalanceTable>) -> Unit
	) {
		val ownerName = contract.getAddress(true)
		// 首先更新此刻最新的余额数据到今天的数据
		val todayBalance = MyTokenTable.dao.getTokenByContractAndAddress(
			contract.contract,
			contract.symbol,
			contract.getAddress(true),
			contract.getCurrentChainID().id
		)?.balance.orZero()
		// 计算过去7天的所有余额
		generateHistoryBalance(todayBalance) { history ->
			history.map {
				TokenBalanceTable(
					contract.contract,
					it.date,
					System.currentTimeMillis(),
					it.balance,
					ownerName
				)
			}.let {
				callback(it.sortedByDescending { it.date })
				TokenBalanceTable.dao.insertAll(it)
			}
		}
	}

	fun <T : List<*>> flipPage(data: T, @UiThread callback: () -> Unit) {
		// TODO ETHSeries
		try {
			detailView.asyncData?.addAll(
				(data as List<BTCSeriesTransactionTable>).asSequence().map { TransactionListModel(it) }.sortedByDescending { it.dataIndex }
			)
		} catch (error: Exception) {
			try {
				detailView.asyncData?.addAll(
					(data as List<EOSTransactionTable>).map { TransactionListModel(it) }
				)
			} catch (error: Exception) {
				try {
					detailView.asyncData?.addAll(
						(data as List<TransactionTable>).map { TransactionListModel(it) }
					)
				} catch (error: Exception) {
					return
				}
			}
		}
		detailView.getDetailAdapter()?.dataSet = detailView.asyncData.orEmptyArray()
		val totalCount = detailView.asyncData?.size.orZero()
		allData = detailView.asyncData
		launchUI {
			detailView.removeEmptyView()
			val startPosition = totalCount - data.size.orZero() + 1
			detailView.notifyDataRangeChanged(if (startPosition < 1) 1 else startPosition, totalCount)
			callback()
		}
	}

	data class DateBalance(val date: Long, val balance: Double)

	private fun List<TransactionListModel>.generateHistoryBalance(
		todayBalance: Double,
		callback: (List<DateBalance>) -> Unit
	) {
		val maxCount = 6
		var balances = listOf<DateBalance>()
		var balance = todayBalance
		object : ConcurrentAsyncCombine() {
			override var asyncCount: Int = maxCount
			override val completeInUIThread: Boolean = false
			override fun doChildTask(index: Int) {
				val currentMills =
					if (index == 0) System.currentTimeMillis() else (index - 1).daysAgoInMills()
				(balance - filter {
					it.timeStamp.toMillisecond() in index.daysAgoInMills() .. currentMills
				}.sumByDouble {
					if (it.isFee) {
						it.minerFee.substringBefore(" ").toDouble() * -1
					} else {
						it.count * modulusByReceiveStatus(it.isReceived)
					}
				}).let {
					balance = it.toBigDecimal().toDouble()
					balances += DateBalance((index + 1).daysAgoInMills(), balance)
					completeMark()
				}
			}

			override fun mergeCallBack() {
				balances += DateBalance(0.daysAgoInMills(), todayBalance)
				callback(balances)
			}
		}.start()
	}

	private fun modulusByReceiveStatus(isReceived: Boolean) = if (isReceived) 1 else -1
}