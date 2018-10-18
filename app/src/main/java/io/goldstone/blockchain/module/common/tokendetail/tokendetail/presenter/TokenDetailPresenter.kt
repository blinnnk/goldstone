package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.os.Bundle
import android.support.annotation.UiThread
import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */
class TokenDetailPresenter(
	override val fragment: TokenDetailFragment
) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {

	private var allData: List<TransactionListModel>? = null
	val token by lazy {
		fragment.getParentFragment<TokenDetailCenterFragment>()?.token
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		// 从账单详情返回的时候重新加载本地数据, 账单详情界面可能是 `Observing` 状态恢复
		loadDataFromDatabaseOrElse()
	}

	override fun updateData() {
		fragment.asyncData = arrayListOf()
		updateEmptyCharData(fragment.token?.symbol.orEmpty())
		prepareTokenDetailData()
	}

	fun showOnlyReceiveData() {
		allData?.filter {
			it.isReceived
		}?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it.toArrayList())
		}
	}

	private var totalCount: Int? = null
	private var currentMaxCount: Int? = null
	private val pageCount = 10

	override fun loadMore() {
		super.loadMore()
		// 是否有合法的数据
		if (totalCount == null || currentMaxCount == null) {
			showBottomLoading(false)
			return
		} else if (currentMaxCount!! <= 0 || fragment.asyncData?.size == totalCount) {
			// 数据是否有效
			showBottomLoading(false)
			return
		} else when {
			token?.contract.isEOSSeries() -> {
				val account = SharedAddress.getCurrentEOSAccount()
				val codeName =
					if (token?.contract.isEOS()) EOSCodeName.EOSIOToken.value
					else token?.contract?.contract.orEmpty()
				doAsync {
					EOSTransactionTable.getRangeData(
						account,
						currentMaxCount!! - pageCount,
						currentMaxCount!!,
						token?.symbol.orEmpty(),
						codeName,
						false
					) { localData ->
						// 显示内存的数据后异步更新数据
						if (!fragment.asyncData.isNull() && fragment.asyncData!!.isEmpty()) localData.map {
							TransactionListModel(it)
						}.prepareTokenHistoryBalance(token?.contract!!, account.accountName) {
							it.updateChartAndHeaderData()
						}
						var endID = 0L
						var pageSize = pageCount
						fun loadTargetRangeData() {
							// 拉取指定范围和数量的账单
							EOSAPI.getEOSTransactions(
								SharedChain.getEOSCurrent(),
								account,
								pageSize,
								0L,
								endID,
								codeName,
								token?.symbol.orEmpty()
							) { data, error ->
								if (!data.isNull() && error.isNone()) {
									// 排序后插入数据库
									data!!.asSequence().sortedByDescending { it.serverID }.forEachIndexed { index, eosTransactionTable ->
										EOSTransactionTable.preventDuplicateInsert(account, eosTransactionTable.apply { dataIndex = currentMaxCount!! - index })
									}
									updateFlipPageData(data.plus(localData))
									currentMaxCount = currentMaxCount!! - pageSize
								}
							}
						}

						when {
							// 本地指定范围的数据是空的条件判断
							localData.isEmpty() -> {
								// 准备 `MongoDB` 格式的 `EndID`
								endID =
									if (currentMaxCount == totalCount) 0L // 本地无数据初次加载
									// 分页数据本地不存在此范围片段, 向上获取指定 ID
									else GoldStoneDataBase.database.eosTransactionDao()
										.getDataByDataIndex(
											account.accountName,
											currentMaxCount!! + 1,
											token?.symbol.orEmpty(),
											codeName
										)?.serverID ?: 0L
								loadTargetRangeData()
							}
							localData.size < pageCount -> {
								// 本地片段存在不足的情况
								endID = if (localData.maxBy { it.dataIndex }?.dataIndex.orZero() == currentMaxCount)
									localData.minBy { it.dataIndex }?.serverID ?: 0L
								else GoldStoneDataBase.database.eosTransactionDao().getDataByDataIndex(
									account.accountName,
									currentMaxCount!! + 1,
									token?.symbol.orEmpty(),
									codeName
								)?.serverID ?: 0L
								pageSize = pageCount - localData.size
								loadTargetRangeData()
							}
							// 本地有数据
							else -> {
								updateFlipPageData(localData)
								currentMaxCount = localData.minBy { it.dataIndex }?.dataIndex.orZero() - 1
							}
						}
					}
				}
			}
		}
	}


	@UiThread
	private fun updateFlipPageData(data: List<EOSTransactionTable>) {
		fragment.asyncData?.addAll(data.map { TransactionListModel(it) })
		fragment.getAdapter<TokenDetailAdapter>()?.dataSet = fragment.asyncData!!
		val totalCount = fragment.asyncData?.size.orZero()
		fragment.context?.runOnUiThread {
			fragment.removeEmptyView()
			fragment.recyclerView.adapter?.notifyItemRangeChanged(totalCount - data.size.orZero() + 1, totalCount)
			showBottomLoading(false)
		}
	}

	fun showOnlyFailedData() {
		allData?.filter {
			it.isFailed || it.hasError
		}?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it.toArrayList())
		}
	}

	fun showOnlySendData() {
		allData?.filter {
			!it.isReceived && !it.isFee
		}?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it.toArrayList())
		}
	}

	fun showAllData() {
		allData?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it.toArrayList())
		}
	}

	fun showAddressSelectionFragment() {
		fragment.getGrandFather<TokenDetailOverlayFragment>()
			?.presenter?.showAddressSelectionFragment()
	}

	fun showDepositFragment() {
		fragment.getGrandFather<TokenDetailOverlayFragment>()?.presenter?.showDepositFragment()
	}

	fun showTransactionDetailFragment(model: TransactionListModel) {
		val argument = Bundle().apply {
			putSerializable(ArgumentKey.transactionFromList, model)
		}
		fragment.getGrandFather<TokenDetailOverlayFragment>()?.apply {
			presenter.showTargetFragment<TransactionDetailFragment>(argument)
		}
	}

	private fun prepareTokenDetailData() {
		fragment.showLoadingView(LoadingText.tokenData)
		loadDataFromDatabaseOrElse { ethETHSeriesLocalData, localBTCSeriesData ->
			// 检查是否有网络
			if (!NetworkUtil.hasNetworkWithAlert(fragment.context)) return@loadDataFromDatabaseOrElse
			// `BTCSeries` 的拉取账单及更新账单需要使用 `localDataMaxIndex`
			// `ETHERC20OrETC` 需要使用到 `localData`
			when {
				token?.contract.isBTCSeries() -> {
					// This localDataMaxIndex is BTCSeries Transactions Only
					val localDataMaxIndex = localBTCSeriesData?.maxBy { it.dataIndex }?.dataIndex ?: 0
					fragment.loadDataFromChain(listOf(), localDataMaxIndex)
				}

				!ethETHSeriesLocalData.isNull() || !ethETHSeriesLocalData?.isEmpty().orFalse() -> {
					fragment.loadDataFromChain(ethETHSeriesLocalData!!, 0)
				}
			}
		}
	}

	private var hasUpdateData = false

	private fun TokenDetailFragment.loadDataFromChain(
		localETHERC20OrETCData: List<TransactionListModel>,
		localDataMaxIndex: Int
	) {
		when {
			token?.contract.isETC() -> {
				if (!hasUpdateData) loadETCChainData(localETHERC20OrETCData)
				hasUpdateData = true
			}

			token?.contract.isBTC() -> {
				if (!hasUpdateData) loadBTCChainData(localDataMaxIndex)
				hasUpdateData = true
			}

			token?.contract.isBCH() -> {
				if (!hasUpdateData) loadBCHChainData(localDataMaxIndex)
				hasUpdateData = true
			}

			token?.contract.isLTC() -> {
				if (!hasUpdateData) loadLTCChainData(localDataMaxIndex)
				hasUpdateData = true
			}

			token?.contract.isETH() -> {
				if (!hasUpdateData) loadETHChainData(localETHERC20OrETCData)
				hasUpdateData = true
			}

			else -> {
				if (!hasUpdateData) loadERCChainData(localETHERC20OrETCData)
				hasUpdateData = true
			}
		}
	}

	fun loadDataFromDatabaseOrElse(
		callback: (
			localETHSeriesData: List<TransactionListModel>?,
			localBTCSeriesData: List<BTCSeriesTransactionTable>?
		) -> Unit = { _, _ -> }
	) {
		val walletType = SharedWallet.getCurrentWalletType()
		when {
			walletType.isBIP44() || walletType.isMultiChain() -> {
				when {
					token?.contract.isETC() ->
						getETHSeriesData(token?.contract.getAddress()) {
							callback(it, null)
						}

					token?.contract.isBTCSeries() -> {
						getBTCSeriesData(token?.contract) {
							callback(null, it)
						}
					}
					token?.contract.isEOSSeries() -> {
						// 创建的时候准备相关的账单数据, 服务本地网络混合分页的逻辑
						val codeName =
							if (token?.contract.isEOS()) EOSCodeName.EOSIOToken.value
							else token?.contract?.contract.orEmpty()
						EOSAPI.getTransactionCount(
							SharedChain.getEOSCurrent(),
							SharedAddress.getCurrentEOSAccount(),
							codeName,
							token?.symbol.orEmpty()
						) { count, error ->
							if (!count.isNull() && error.isNone()) {
								totalCount = count
								currentMaxCount = count
								// 初次加载的时候, 这个逻辑会复用到监听转账的 Pending Data 的状态更改.
								// 当 `PendingData Observer` 调用这个方法的时候让数据重新加载显示, 来达到更新 `Pending Status` 的效果
								fragment.getAdapter<TokenDetailAdapter>()?.dataSet?.clear()
								// 初始化
								loadMore()
								GoldStoneAPI.context.runOnUiThread {
									fragment.removeLoadingView()
								}
							}
						}
					}
					else -> getETHSeriesData(token?.contract.getAddress()) {
						callback(it, null)
					}
				}
			}

			token?.contract.isBTCSeries() -> getBTCSeriesData(token?.contract) {
				callback(null, it)
			}

			walletType.isETHSeries() -> getETHSeriesData(SharedAddress.getCurrentEthereum()) {
				callback(it, null)
			}
		}
	}

	private fun getETHSeriesData(
		address: String,
		callback: (List<TransactionListModel>) -> Unit
	) {
		TransactionTable.getByAddressAndContract(
			address,
			fragment.token?.contract!!
		) { transactions ->
			transactions.isNotEmpty() isTrue {
				fragment.updatePageBy(transactions, address)
				fragment.removeLoadingView()
			}
			callback(transactions)
		}
	}

	private fun getBTCSeriesData(
		contract: TokenContract?,
		callback: (List<BTCSeriesTransactionTable>) -> Unit
	) {
		BTCSeriesTransactionTable
			.getTransactionsByAddressAndChainType(contract.getAddress(), contract.getChainType().id) { transactions ->
				transactions.isNotEmpty() isTrue {
					fragment.updatePageBy(
						transactions.asSequence().map {
							TransactionListModel(it)
						}.sortedByDescending {
							it.timeStamp
						}.toList(),
						contract.getAddress()
					)
					fragment.removeLoadingView()
				}
				callback(transactions)
			}
	}

	private fun TokenDetailFragment.updatePageBy(
		data: List<TransactionListModel>,
		ownerName: String
	) {
		allData = data
		checkAddressNameInContacts(data) {
			// 防止用户在加载数据过程中切换到别的 `Tab` 这里复位一下
			setAllSelectedStatus()
			diffAndUpdateAdapterData<TokenDetailAdapter>(data.toArrayList())
			// 显示内存的数据后异步更新数据
			data.prepareTokenHistoryBalance(token?.contract!!, ownerName) {
				it.updateChartAndHeaderData()
			}
		}
	}

	private fun updateEmptyCharData(symbol: String) {
		// 没网的时候返回空数据
		val now = System.currentTimeMillis()
		var emptyData = listOf<TokenBalanceTable>()
		for (index in 0 until 7) {
			emptyData += TokenBalanceTable(symbol, now)
		}
		emptyData.updateChartAndHeaderData()
	}

	private fun List<TokenBalanceTable>.updateChartAndHeaderData() {
		fragment.recyclerView.getItemAtAdapterPosition<TokenDetailHeaderView>(0) { header ->
			val maxChartCount = 7
			val chartArray = arrayListOf<ChartPoint>()
			val charCount = if (size > maxChartCount) maxChartCount else size
			forEach {
				chartArray.add(
					ChartPoint(
						CryptoUtils.dateInDay(it.date),
						it.balance.toBigDecimal().toFloat()
					)
				)
				if (chartArray.size == charCount) {
					header.setCharData(chartArray.reversed().toArrayList())
				}
			}
		}
	}

	private fun List<TransactionListModel>.prepareTokenHistoryBalance(
		contract: TokenContract,
		ownerName: String,
		callback: (List<TokenBalanceTable>) -> Unit
	) {
		// 首先更新此刻最新的余额数据到今天的数据
		MyTokenTable.getTokenBalance(contract, ownerName) { todayBalance ->
			if (todayBalance.isNull()) return@getTokenBalance
			// 计算过去7天的所有余额
			generateHistoryBalance(todayBalance.orZero()) { history ->
				load {
					history.forEach { data ->
						TokenBalanceTable.insertOrUpdate(
							contract.contract.orEmpty(),
							ownerName,
							data.date,
							data.balance
						)
					}
				} then { _ ->
					// 更新数据完毕后在主线程从新从数据库获取数据
					TokenBalanceTable.getBalanceByContract(contract.contract.orEmpty(), ownerName) {
						callback(it)
					}
				}
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
			override fun concurrentJobs() {
				(0 until maxCount).forEach { index ->
					val currentMills =
						if (index == 0) System.currentTimeMillis() else (index - 1).daysAgoInMills()
					(balance - filter {
						it.timeStamp.toMillisecond() in index.daysAgoInMills() .. currentMills
					}.sumByDouble {
						if (it.isFee) {
							it.minerFee.substringBefore(" ").toDouble() * -1
						} else {
							it.value.toDouble() * modulusByReceiveStatus(it.isReceived)
						}
					}).let {
						balance = it.toBigDecimal().toDouble()
						balances += DateBalance((index + 1).daysAgoInMills(), balance)
						completeMark()
					}
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