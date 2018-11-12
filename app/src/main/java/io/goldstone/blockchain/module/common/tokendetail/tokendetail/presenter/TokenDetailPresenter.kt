package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toArrayList
import com.blinnnk.extension.toMillisecond
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
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
		detailView.asyncData = arrayListOf()
		updateEmptyCharData()
		loadDataFromChain()
	}

	var totalCount: Int? = null
	var currentMaxCount: Int? = null

	override fun loadMore() {
		// 目前的翻页逻辑比较复杂, 暂时不支持分类 `Sort` 后的分页, 只在总类目下支持分页
		if (
			totalCount == null
			|| currentMaxCount == null
			|| currentMaxCount ?: 0 <= 0
			|| detailView.asyncData?.size == totalCount
			|| detailView.currentMenu != CommonText.all
		) return
		detailView.showBottomLoading(true)
		flipEOSPageData {
			launchUI {
				detailView.showBottomLoading(false)
				detailView.showLoading(false)
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
		if (token.contract.isEOSSeries()) {
			if (allData.isNull() || allData!!.isEmpty()) {
				currentMaxCount = totalCount
				detailView.getDetailAdapter()?.dataSet?.clear()
				flipEOSPageData { sortData() }
			} else sortData()
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
		if (token.contract.isEOSSeries()) {
			if (allData.isNull() || allData!!.isEmpty()) {
				currentMaxCount = totalCount
				detailView.getDetailAdapter()?.dataSet?.clear()
				flipEOSPageData {
					sortData()
				}
			} else sortData()
		} else sortData()
	}

	override fun showAllData() {
		fun sortData() {
			allData?.let {
				detailView.updateDataChange(it.toArrayList())
				if (it.isEmpty()) detailView.showBottomLoading(false)
			}
		}
		if (token.contract.isEOSSeries()) {
			currentMaxCount = totalCount
			detailView.getDetailAdapter()?.dataSet?.clear()
			flipEOSPageData { sortData() }
		} else sortData()
	}

	private var hasUpdateData = false
	@WorkerThread
	private fun loadDataFromChain() = GlobalScope.launch(Dispatchers.Default) {
		when {
			token.contract.isBTCSeries() -> loadLocalData { maxDataIndex ->
				if (!hasUpdateData) loadBTCSeriesData(token.contract.getChainType(), maxDataIndex)
				hasUpdateData = true
			}
			token.contract.isETHSeries() -> loadLocalData { startBlockNumber ->
				hasUpdateData = when {
					token.contract.isETC() -> {
						if (!hasUpdateData) loadETCChainData(startBlockNumber)
						true
					}
					token.contract.isETH() -> {
						if (!hasUpdateData) loadETHChainData(startBlockNumber)
						true
					}
					else -> {
						if (!hasUpdateData) loadERCChainData(startBlockNumber)
						true
					}
				}
			}
			token.contract.isEOSSeries() -> getEOSSeriesData()
		}
	}

	fun loadLocalData(callback: (parameter: Int) -> Unit = {}) {
		when {
			token.contract.isETHSeries() -> getETHSeriesData { callback(it) }
			token.contract.isBTCSeries() -> getBTCSeriesData(token.contract) { callback(it) }
			token.contract.isEOSSeries() -> getEOSSeriesData()
		}
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
					detailView.showLoading(false)
				}
			}
		} else EOSAPI.getTransactionCount(
			SharedChain.getEOSCurrent().chainID,
			SharedAddress.getCurrentEOSAccount(),
			codeName,
			token.symbol
		) { count, error ->
			if (count != null && error.isNone()) {
				totalCount = count
				currentMaxCount = count
				// 初次加载的时候, 这个逻辑会复用到监听转账的 Pending Data 的状态更改.
				// 当 `PendingData Observer` 调用这个方法的时候让数据重新加载显示, 来达到更新 `Pending Status` 的效果
				detailView.getDetailAdapter()?.dataSet?.clear()
				launchUI { loadMore() }
			}
		}
	}

	@WorkerThread
	private fun getETHSeriesData(callback: (blockNumber: Int) -> Unit) {
		val address = token.contract.getAddress()
		val transactionDao =
			GoldStoneDataBase.database.transactionDao()
		val transactions =
			if (token.contract.isETH()) transactionDao.getETHAndAllFee(
				address,
				token.contract.contract,
				token.chainID
			) else transactionDao.getDataWithFee(
				address,
				token.contract.contract,
				token.chainID
			)
		val listModel = transactions.map { TransactionListModel(it) }
		updatePageBy(listModel)
		callback(listModel.maxBy { it.blockNumber }?.blockNumber.orZero())
	}

	@WorkerThread
	private fun getBTCSeriesData(
		contract: TokenContract?,
		callback: (maxDataIndex: Int) -> Unit
	) {
		val transactions =
			BTCSeriesTransactionTable.dao.getTransactions(contract.getAddress(), contract.getChainType().id)
		if (transactions.isNotEmpty()) {
			updatePageBy(
				transactions.asSequence().map {
					TransactionListModel(it)
				}.sortedByDescending {
					it.timeStamp
				}.toList()
			)
		}
		callback(transactions.maxBy { it.dataIndex }?.dataIndex.orZero())
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
			data.prepareTokenHistoryBalance(token.contract) {
				it.updateChartAndHeaderData()
			}
		}
	}

	// 没数据或初始化的时候先生产默认值显示
	private fun updateEmptyCharData() = GlobalScope.launch(Dispatchers.Default) {
		listOf<TransactionListModel>()
			.prepareTokenHistoryBalance(token.contract) {
				it.updateChartAndHeaderData()
			}
	}

	@UiThread
	fun List<TokenBalanceTable>.updateChartAndHeaderData() {
		val maxChartCount = 7
		val chartArray = arrayListOf<ChartPoint>()
		val charCount = if (size > maxChartCount) maxChartCount else size
		forEach {
			chartArray.add(
				ChartPoint(CryptoUtils.dateInDay(it.date), it.balance.toBigDecimal().toFloat())
			)
			if (chartArray.size == charCount) launchUI {
				detailView.setChartData(chartArray.reversed().toArrayList())
				detailView.showLoading(false)
			}
		}
	}

	@WorkerThread
	fun List<TransactionListModel>.prepareTokenHistoryBalance(
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
				callback(it)
				TokenBalanceTable.dao.insertAll(it)
			}
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