package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.os.Bundle
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
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

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
		loadDataFromDatabaseOrElse { ethETHSeriesLocalData, localBTCSeriesData, localEOSSeriesData ->
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
				token?.contract.isEOS() -> {
					// This localDataMaxIndex is EOSSeries Transactions Only
					val localDataMaxIndex = localEOSSeriesData?.maxBy { it.dataIndex }?.dataIndex ?: 0
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

			token?.contract.isEOS() -> {
				if (!hasUpdateData) loadEOSDataFromChain(localDataMaxIndex)
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
			localBTCSeriesData: List<BTCSeriesTransactionTable>?,
			localEOSSeriesData: List<EOSTransactionTable>?
		) -> Unit = { _, _, _ -> }
	) {
		val walletType = SharedWallet.getCurrentWalletType()
		when {
			walletType.isBIP44() || walletType.isMultiChain() -> {
				when {
					token?.contract.isETC() ->
						getETHSeriesData(token?.contract.getAddress()) {
							callback(it, null, null)
						}

					token?.contract.isBTCSeries() -> {
						getBTCSeriesData(token?.contract) {
							callback(null, it, null)
						}
					}

					token?.contract.isEOS() -> getEOSSeriesData {
						callback(null, null, it)
					}

					else -> getETHSeriesData(token?.contract.getAddress()) {
						callback(it, null, null)
					}
				}
			}

			token?.contract.isBTCSeries() ->
				getBTCSeriesData(token?.contract) {
					callback(null, it, null)
				}

			token?.contract.isEOS() -> getEOSSeriesData {
				callback(null, null, it)
			}

			walletType.isETHSeries() ->
				getETHSeriesData(SharedAddress.getCurrentEthereum()) {
					callback(it, null, null)
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

	private fun getEOSSeriesData(callback: (List<EOSTransactionTable>) -> Unit) {
		val account = SharedAddress.getCurrentEOSAccount()
		EOSTransactionTable.getTransactionByAccountName(
			account.accountName,
			SharedChain.getEOSCurrent()
		) { transactions ->
			transactions.isNotEmpty() isTrue {
				fragment.updatePageBy(
					transactions.asSequence().filter {
						// EOS 会存在一些 `FromName` 或 `ToName` 都为空的坏账这里过滤一下
						it.transactionData.fromName.isNotEmpty()
					}.map {
						TransactionListModel(it)
					}.sortedByDescending {
						it.timeStamp
					}.toList(),
					account.accountName
				)
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