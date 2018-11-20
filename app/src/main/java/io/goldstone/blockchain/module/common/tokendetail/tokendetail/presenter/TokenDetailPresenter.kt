package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.contract.TokenDetailContract
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.getContactName
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

	override fun start() {
		detailView.showLoading(true)
		updateEmptyCharData()
		checkNewDataFromChain()
	}

	override fun refreshData() {
		detailView.asyncData = arrayListOf()
		loadLocalData(true)
	}

	private var allData: List<TransactionListModel>? = null
	var totalCount: Int? = null
	var currentMaxCount: Int? = null

	override fun loadMore() {
		if (detailView.currentMenu != CommonText.all) return
		detailView.showBottomLoading(true)
		loadLocalData(false)
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
	fun getMaxBlockNumber(): Int {
		return TransactionTable.dao.getMaxBlockNumber(
			token.contract.getAddress(),
			token.contract.contract,
			token.chainID
		)?.blockNumber ?: 99999999
	}

	private fun loadLocalData(isRefresh: Boolean) {
		with(token.contract) {
			GlobalScope.launch(Dispatchers.Default) {
				when {
					isBTCSeries() -> getBTCSeriesData()
					isETHSeries() -> getETHSeriesData()
					isEOSSeries() -> if (isRefresh) getEOSSeriesData() else  {
						if (
							totalCount == null
							|| currentMaxCount == null
							|| currentMaxCount ?: 0 <= 0
							|| detailView.asyncData?.size == totalCount
						) {
							detailView.showBottomLoading(false)
						} else flipEOSPage {
							detailView.showBottomLoading(false)
							detailView.showLoading(false)
						}
					}
				}
			}
		}
	}

	private fun getMaxDataIndex(): Int {
		return BTCSeriesTransactionTable.dao.getMaxDataIndex(
			token.contract.getAddress(),
			token.contract.getChainType().id
		)?.dataIndex.orZero()
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
			if (chartArray.size == charCount) {
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
			}.let { data ->
				callback(data.sortedByDescending { it.date })
				TokenBalanceTable.dao.insertAll(data)
			}
		}
	}

	@WorkerThread
	fun <T : List<*>> flipPage(data: T, callback: () -> Unit) {
		val newData = try {
			(data as List<BTCSeriesTransactionTable>).asSequence().map {
				TransactionListModel(it)
			}.toList()
		} catch (error: Exception) {
			try {
				(data as List<EOSTransactionTable>).map { TransactionListModel(it) }
			} catch (error: Exception) {
				(data as List<TransactionTable>).map { TransactionListModel(it) }
			} catch (error: Exception) {
				listOf<TransactionListModel>()
			}
		}

		checkAddressNameInContacts(newData) {
			detailView.asyncData?.addAll(newData)
			detailView.getDetailAdapter()?.dataSet = detailView.asyncData.orEmptyArray()
			val totalCount = detailView.asyncData?.size.orZero()
			allData = detailView.asyncData
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

	private var allContacts: List<ContactTable>? = null
	@WorkerThread
	private fun checkAddressNameInContacts(transactions: List<TransactionListModel>, callback: () -> Unit) {
		if (allContacts.isNull()) allContacts = ContactTable.dao.getAllContacts()
		if (allContacts?.isNotEmpty() == true) transactions.forEach { transaction ->
			transaction.addressName = allContacts!!.getContactName(transaction.addressName)
		}
		callback()
	}

	private fun modulusByReceiveStatus(isReceived: Boolean) = if (isReceived) 1 else -1
}