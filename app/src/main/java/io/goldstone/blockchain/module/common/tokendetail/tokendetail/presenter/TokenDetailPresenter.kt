package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.os.Bundle
import android.util.Log
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.daysAgoInMills
import io.goldstone.blockchain.crypto.toMills
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
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

var tokenDetailData: ArrayList<TransactionListModel>? = null

class TokenDetailPresenter(
	override val fragment: TokenDetailFragment
) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {

	override fun updateData() {
		prepareTokenDetailData()
	}

	fun showAddressSelectionFragment() {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
				presenter.showTargetFragment<AddressSelectionFragment>(
					TokenDetailText.address, TokenDetailText.tokenDetail
				)
			}
		}
	}

	fun showTransactionDetailFragment(model: TransactionListModel) {
		val argument = Bundle().apply {
			putSerializable(ArgumentKey.transactionFromList, model)
		}
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.showTargetFragment<TransactionDetailFragment>(
				TransactionText.detail, TokenDetailText.tokenDetail, argument
			)
		}
	}

	fun showDepositFragment() {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			//
		}
	}

	private fun prepareTokenDetailData() {
		// 优先检查内存里面是否有符合的数据, 如果有直加载内存中的数据, 不同的 `TokenDetail` 会存储不同 `Symbol` 的账单
		// 这里要判断当前的内存的交易账单是否是对应的 `Symbol`
		if (!tokenDetailData.isNull() && tokenDetailData!!.any { it.symbol == fragment.symbol }) {
			fragment.updateDataByAsyncDataStatus(tokenDetailData!!)
			// 在异步检查更新最新的数据
			NetworkUtil.hasNetwork(fragment.context) isTrue {
				loadDataFromChain()
			}
		} else {
			loadDataFromDatabaseOrElse {
				NetworkUtil.hasNetwork(fragment.context) isTrue {
					loadDataFromChain()
				}
			}
		}
	}

	private fun loadDataFromDatabaseOrElse(withoutLocalDataCallback: () -> Unit = {}) {
		// 内存里面没有数据首先从本地数据库查询数据
		TransactionTable.getTransactionsByAddressAndSymbol(
			WalletTable.current.address, fragment.symbol!!
		) { transactions ->
			transactions.isNotEmpty() isTrue {
				fragment.updateDataByAsyncDataStatus(transactions.map { TransactionListModel(it) }.toArrayList())
			} otherwise {
				withoutLocalDataCallback()
				Log.d("DEBUG", "Without Local Transaction Data")
			}
		}
	}

	private fun loadDataFromChain() {
		doAsync {
			TransactionTable.getMyLatestStartBlock { blockNumber ->
				// 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
				TransactionListPresenter.updateTransactions(fragment.getMainActivity(), blockNumber) {
					fragment.context?.runOnUiThread {
						// 返回的是交易记录, 筛选当前的 `Symbol` 如果没有就返回空数组
						it.find { it.symbol == fragment.symbol }.isNotNull {
							// 有数据后重新执行从数据库拉取数据
							loadDataFromDatabaseOrElse()
						} otherwise {
							// 链上和本地都没有数据就更新一个空数组作为默认
							fragment.asyncData.isNull() isTrue {
								fragment.updateDataByAsyncDataStatus(arrayListOf())
							}
						}
					}
				}
			}
		}
	}

	private fun TokenDetailFragment.updateDataByAsyncDataStatus(data: ArrayList<TransactionListModel>) {
		tokenDetailData = data
		asyncData.isNull() isTrue {
			asyncData = data
		} otherwise {
			diffAndUpdateAdapterData<TokenDetailAdapter>(data)
		}
		// 显示内存的数据后异步更新数据
		NetworkUtil.hasNetwork(context) isTrue {
			data.prepareTokenHistoryBalance(fragment.symbol!!) {
				it.updateChartAndHeaderData()
			}
		} otherwise {
			// 没网的时候返回空数据
			val now = System.currentTimeMillis()
			arrayListOf(
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, ""),
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, ""),
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, ""),
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, ""),
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, ""),
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, ""),
				TokenBalanceTable(0, symbol!!, now, 0, 0.0, "")
			).updateChartAndHeaderData()
		}
	}

	private fun ArrayList<TokenBalanceTable>.updateChartAndHeaderData() {
		fragment.recyclerView.getItemViewAtAdapterPosition<TokenDetailHeaderView>(0) { header ->
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
		symbol: String, callback: (ArrayList<TokenBalanceTable>) -> Unit
	) {
		// 首先更新此刻最新的余额数据到今天的数据
		TokenBalanceTable.getTodayBalance(
			WalletTable.current.address, symbol
		) { todayBalance ->
			// 计算过去7天的所有余额
			generateHistoryBalance(todayBalance) { history ->
				coroutinesTask({
					history.forEachIndexed { index, data ->
						TokenBalanceTable.insertOrUpdate(
							symbol,
							WalletTable.current.address,
							data.date,
							if (index == 0) todayBalance else data.balance
						)
					}
				}) {
					// 更新数据完毕后在主线程从新从数据库获取数据
					TokenBalanceTable.getBalanceBySymbol(WalletTable.current.address, symbol) {
						callback(it)
					}
				}
			}
		}
	}

	data class DateBalance(val date: Long, val balance: Double)

	private fun ArrayList<TransactionListModel>.generateHistoryBalance(
		todayBalance: Double, callback: (ArrayList<DateBalance>) -> Unit
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