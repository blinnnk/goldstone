package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toArrayList
import com.blinnnk.extension.toMillisecond
import com.blinnnk.util.ConcurrentAsyncCombine
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.NetworkUtil
import io.goldstone.blinnnk.common.utils.TimeUtils
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.utils.daysAgoInMills
import io.goldstone.blinnnk.kernel.commontable.BTCSeriesTransactionTable
import io.goldstone.blinnnk.kernel.commontable.EOSTransactionTable
import io.goldstone.blinnnk.kernel.commontable.MyTokenTable
import io.goldstone.blinnnk.kernel.commontable.TransactionTable
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.contract.TokenDetailContract
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.getContactName
import io.goldstone.blinnnk.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailCellModel
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
	
	var page = 1

	override fun start() {
		updateEmptyCharData()
		if (!NetworkUtil.hasNetwork()) {
			detailView.showNetworkAlert()
			loadLocalData(false)
		} else {
			checkNewDataFromChain()
			detailView.showLoading(true)
		}
	}

	override fun refreshData() {
		detailView.asyncData = arrayListOf()
		loadLocalData(true)
	}

	var totalCount: Int? = null
	var currentMaxCount: Int? = null

	override fun loadMore() {
		detailView.showBottomLoading(true)
		loadLocalData(false)
	}

	@WorkerThread
	private fun checkNewDataFromChain() = GlobalScope.launch(Dispatchers.Default) {
		with(token.contract) {
			when {
				isBTCSeries() -> loadBTCSeriesData(getChainType(), getMaxDataIndex(), true)
				isETHSeries() -> when {
					isETC() -> loadETCChainData()
					isETH() -> loadETHChainData(getMaxBlockNumber())
					isERC20Token() -> loadERCChainData(getMaxBlockNumber())
				}
				isEOSSeries() -> getCountInfoFromChain()
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
		) ?: 99999999
	}

	private fun loadLocalData(isRefresh: Boolean) {
		with(token.contract) {
			launchDefault {
				when {
					isBTCSeries() -> getBTCSeriesData()
					isETHSeries() -> if (isETC()) loadETCChainData() else getETHSeriesData()
					isEOSSeries() -> if (isRefresh) getCountInfoFromChain() else {
						if (totalCount.isNull() && !NetworkUtil.hasNetwork()) getCountInfoFromLocal()
						else if (
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
	private fun updateEmptyCharData() = launchDefault {
		listOf<TransactionListModel>().generateBalanceList(token.contract) {
			it.updateHeaderData(true)
		}
	}

	@WorkerThread
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
			with(detailView) {
				asyncData?.addAll(newData)
				// 这个 Filter Data 是服务筛选数据用的, 只修改显示数据不修改内存数据
				val showData = filterData(asyncData)
				val showNewData = filterData(newData)
				launchUI {
					if (showNewData.isEmpty()) detailView.showFilterLoadMoreAttention(asyncData?.size.orZero())
					getDetailAdapter()?.dataSet = showData.toArrayList()
					val totalCount = showData.size
					if (showData.isNotEmpty()) removeEmptyView()
					val startPosition = totalCount - showNewData.size.orZero() + 1
					notifyDataRangeChanged(if (startPosition < 1) 1 else startPosition, totalCount)
					callback()
				}
			}
		}
	}

	data class DateBalance(val date: Long, val balance: Double)

	private fun List<TransactionListModel>.generateHistoryBalance(
		todayBalance: Double,
		@WorkerThread callback: (List<DateBalance>) -> Unit
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