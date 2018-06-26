package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.crypto.utils.toMills
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.presenter.ClassicTransactionListPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */
class TokenDetailPresenter(
	override val fragment: TokenDetailFragment
) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {
	
	private var allData: ArrayList<TransactionListModel>? = null
	
	override fun onFragmentHiddenChanged(isHidden: Boolean) {
		loadDataFromDatabaseOrElse()
	}
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
		updateEmptyCharData(fragment.token?.symbol.orEmpty())
		// 错开动画和数据读取的时间, 避免 `UI` 可能的卡顿
		AnimationDuration.Default timeUpThen {
			prepareTokenDetailData()
		}
	}
	
	fun showOnlyReceiveData() {
		allData?.filter {
			it.isReceived
		}?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it.toArrayList())
		}
	}
	
	fun showOnlySendData() {
		allData?.filter {
			!it.isReceived
		}?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it.toArrayList())
		}
	}
	
	fun showAllData() {
		allData?.let {
			diffAndUpdateAdapterData<TokenDetailAdapter>(it)
		}
	}
	
	fun showAddressSelectionFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.showAddressSelectionFragment()
		}
	}
	
	fun showDepositFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.showDepositFragment()
		}
	}
	
	fun showTransactionDetailFragment(model: TransactionListModel) {
		val argument = Bundle().apply {
			putSerializable(ArgumentKey.transactionFromList, model)
		}
		fragment
			.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter
			?.showTargetFragment<TransactionDetailFragment>(
				TransactionText.detail, TokenDetailText.tokenDetail, argument
			)
	}
	
	private fun prepareTokenDetailData() {
		fragment.showLoadingView(LoadingText.tokenData)
		loadDataFromDatabaseOrElse {
			NetworkUtil.hasNetworkWithAlert(fragment.context) isTrue {
				fragment.loadDataFromChain()
			}
		}
	}
	
	private var hasUpdateETCData = false
	private var hasUpdateERCData = false
	
	private fun TokenDetailFragment.loadDataFromChain() {
		if (token?.symbol.equals(CryptoSymbol.etc, true)) {
			if (!hasUpdateETCData) loadETCChainData()
			hasUpdateETCData = true
		} else {
			if (!hasUpdateERCData) loadERCChainData()
			hasUpdateERCData = true
		}
	}
	
	private fun loadDataFromDatabaseOrElse(
		withoutLocalDataCallback: () -> Unit = {}
	) {
		// 内存里面没有数据首先从本地数据库查询数据
		TransactionTable.getCurrentChainByAddressAndContract(
			Config.getCurrentAddress(),
			fragment.token?.contract.orEmpty(),
			ChainID.getChainIDBySymbol(fragment.token?.symbol.orEmpty())
		) { transactions ->
			transactions.isNotEmpty() isTrue {
				fragment.updateChartBy(transactions)
				fragment.removeLoadingView()
			} otherwise {
				withoutLocalDataCallback()
				LogUtil.debug(this.javaClass.simpleName, "reason: There isn't Local Transaction Data")
			}
		}
	}
	
	private fun TokenDetailFragment.loadERCChainData() {
		doAsync {
			// 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
			TransactionListPresenter.getTransactionDataFromEtherScan(
				this@loadERCChainData,
				"0",
				{
					// ToDo 等自定义的 `Alert` 完成后应当友好提示
					LogUtil.error("error in getTransactionDataFromEtherScan $it")
				}
			) {
				// 返回的是交易记录, 筛选当前的 `Symbol` 如果没有就返回空数组
				it.find {
					it.contract.equals(token?.contract, true)
				}.isNotNull {
					// 有数据后重新执行从数据库拉取数据
					loadDataFromDatabaseOrElse()
				} otherwise {
					context?.runOnUiThread {
						// 链上和本地都没有数据就更新一个空数组作为默认
						updateChartBy(arrayListOf())
						removeLoadingView()
					}
				}
			}
		}
	}
	
	private fun TokenDetailFragment.loadETCChainData() {
		showLoadingView(LoadingText.transactionData)
		ClassicTransactionListPresenter
			.getInvalidETCTransactionsFromChain(arrayListOf()) {
				fragment.removeLoadingView()
				loadDataFromDatabaseOrElse()
			}
	}
	
	private fun TokenDetailFragment.updateChartBy(data: ArrayList<TransactionListModel>) {
		allData = data
		TransactionListPresenter.checkAddressNameInContacts(data) {
			diffAndUpdateAdapterData<TokenDetailAdapter>(data)
			// 显示内存的数据后异步更新数据
			NetworkUtil.hasNetworkWithAlert(context) isTrue {
				data.prepareTokenHistoryBalance(token?.contract!!) {
					it.updateChartAndHeaderData()
				}
			} otherwise {
				updateEmptyCharData(token?.symbol!!)
			}
		}
	}
	
	private fun updateEmptyCharData(symbol: String) {
		// 没网的时候返回空数据
		val now = System.currentTimeMillis()
		arrayListOf(
			TokenBalanceTable(0, symbol, now, 0, 0.0, ""),
			TokenBalanceTable(0, symbol, now, 0, 0.0, ""),
			TokenBalanceTable(0, symbol, now, 0, 0.0, ""),
			TokenBalanceTable(0, symbol, now, 0, 0.0, ""),
			TokenBalanceTable(0, symbol, now, 0, 0.0, ""),
			TokenBalanceTable(0, symbol, now, 0, 0.0, ""),
			TokenBalanceTable(0, symbol, now, 0, 0.0, "")
		).updateChartAndHeaderData()
	}
	
	private fun ArrayList<TokenBalanceTable>.updateChartAndHeaderData() {
		fragment.recyclerView.getItemAtAdapterPosition<TokenDetailHeaderView>(0) { header ->
			val maxChartCount = 6
			val chartArray = arrayListOf<ChartPoint>()
			val charCount = if (size > maxChartCount) maxChartCount else size
			forEach {
				chartArray.add(ChartPoint(CryptoUtils.dateInDay(it.date), it.balance.toFloat()))
				if (chartArray.size == charCount) {
					header?.setCharData(chartArray.reversed().toArrayList())
				}
			}
		}
	}
	
	private fun ArrayList<TransactionListModel>.prepareTokenHistoryBalance(
		contract: String,
		callback: (ArrayList<TokenBalanceTable>) -> Unit
	) {
		// 首先更新此刻最新的余额数据到今天的数据
		MyTokenTable.getCurrentChainTokenBalanceByContract(contract) { todayBalance ->
			if (todayBalance.isNull()) return@getCurrentChainTokenBalanceByContract
			// 计算过去7天的所有余额
			generateHistoryBalance(todayBalance!!) { history ->
				coroutinesTask(
					{
						history.forEachIndexed { index, data ->
							TokenBalanceTable.insertOrUpdate(
								contract,
								Config.getCurrentAddress(),
								data.date,
								// 插入今日的余额数据
								if (index == 0) todayBalance else data.balance
							)
						}
					}) {
					// 更新数据完毕后在主线程从新从数据库获取数据
					TokenBalanceTable.getBalanceByContract(contract) {
						callback(it)
					}
				}
			}
		}
	}
	
	data class DateBalance(val date: Long, val balance: Double)
	
	private fun ArrayList<TransactionListModel>.generateHistoryBalance(
		todayBalance: Double,
		callback: (ArrayList<DateBalance>) -> Unit
	) {
		val maxCount = 6
		val balances = arrayListOf<DateBalance>()
		var balance = todayBalance
		object : ConcurrentAsyncCombine() {
			override var asyncCount: Int = maxCount
			override fun concurrentJobs() {
				(0 until maxCount).forEach { index ->
					val currentMills =
						if (index == 0) System.currentTimeMillis() else (index - 1).daysAgoInMills()
					(balance - filter {
						it.timeStamp.toMills() in index.daysAgoInMills() .. currentMills
					}.sumByDouble {
						it.value.toDouble() * modulusByReceiveStatus(it.isReceived)
					}).let {
						balance = it
						balances.add(DateBalance(index.daysAgoInMills(), balance))
						completeMark()
					}
				}
			}
			
			override fun mergeCallBack() = callback(balances)
		}.start()
	}
	
	private fun modulusByReceiveStatus(isReceived: Boolean) = if (isReceived) 1 else -1
}