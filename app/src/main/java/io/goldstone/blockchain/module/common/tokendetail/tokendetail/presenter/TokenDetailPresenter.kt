package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.daysAgoInMills
import io.goldstone.blockchain.crypto.toMills
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */
class TokenDetailPresenter(
	override val fragment: TokenDetailFragment
) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {
	
	override fun onFragmentHiddenChanged(isHidden: Boolean) {
		loadDataFromDatabaseOrElse()
	}
	
	override fun updateData() {
		// 详情页面直接全屏高度
		setHeightMatchParent {
			fragment.asyncData = arrayListOf()
			updateEmptyCharData(fragment.token?.symbol!!)
			prepareTokenDetailData()
		}
	}
	
	override fun updateParentContentLayoutHeight(
		dataCount: Int?,
		cellHeight: Int,
		maxHeight: Int
	) {
		// 详情页面直接全屏高度
		setHeightMatchParent()
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
	
	private fun loadDataFromDatabaseOrElse(withoutLocalDataCallback: () -> Unit = {}) {
		// 内存里面没有数据首先从本地数据库查询数据
		TransactionTable.getTransactionsByAddressAndContract(
			WalletTable.current.address,
			fragment.token?.contract.orEmpty()
		) { transactions ->
			transactions.isNotEmpty() isTrue {
				fragment.updateChartBy(transactions.map { TransactionListModel(it) }.toArrayList())
				fragment.removeLoadingView()
			} otherwise {
				withoutLocalDataCallback()
				LogUtil.debug(this.javaClass.simpleName, "reason: There isn't Local Transaction Data")
			}
		}
	}
	
	private fun TokenDetailFragment.loadDataFromChain() {
		doAsync {
			TransactionTable.getMyLatestStartBlock { blockNumber ->
				// 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
				TransactionListPresenter.updateTransactions(
					this@loadDataFromChain,
					blockNumber, {
						context?.alert(it.toString())
					}) {
					context?.runOnUiThread {
						// 返回的是交易记录, 筛选当前的 `Symbol` 如果没有就返回空数组
						it.find { it.contract.equals(token?.contract, true) }.isNotNull {
							// 有数据后重新执行从数据库拉取数据
							loadDataFromDatabaseOrElse()
						} otherwise {
							// 链上和本地都没有数据就更新一个空数组作为默认
							updateChartBy(arrayListOf())
							fragment.removeLoadingView()
						}
					}
				}
			}
		}
	}
	
	private fun TokenDetailFragment.updateChartBy(data: ArrayList<TransactionListModel>) {
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
			val chartArray = arrayListOf<Point>()
			val charCount = if (size > maxChartCount) maxChartCount else size
			forEach {
				chartArray.add(Point(CryptoUtils.dateInDay(it.date), it.balance.toFloat()))
				if (chartArray.size == charCount) {
					var maxY = maxYValue(chartArray)
					var unitY = Math.ceil((maxY / 10)).toFloat()
					if (maxY == 0.0) maxY = 10.0
					if (unitY == 0f) unitY = 1f
					header?.setCharData(chartArray.reversed().toArrayList(), maxY.toFloat(), unitY)
				}
			}
		}
	}
	
	// 计算出 `chartView` Y 轴的最大值
	private val maxYValue: (ArrayList<Point>) -> Double = {
		val maxValue = Math.ceil(it.maxBy { it.value }?.value!!.toDouble())
		if (maxValue > 10) maxValue * 1.5 else maxValue + 5
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
								WalletTable.current.address,
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
	
	data class DateBalance(
		val date: Long,
		val balance: Double
	)
	
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