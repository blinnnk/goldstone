package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.os.Bundle
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.daysAgoInMills
import io.goldstone.blockchain.crypto.toMills
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter.TransactionListPresenter

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */

class TokenDetailPresenter(
  override val fragment: TokenDetailFragment
) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {

  override fun updateData() {
    prepareTokenDetailData {
      isNotEmpty().isTrue {
        fragment.asyncData = this
        prepareTokenHistoryBalance(fragment.symbol!!) {
          it.updateChartAndHeaderData()
        }
      } otherwise {
        fragment.asyncData = arrayListOf()
        prepareTokenHistoryBalance(fragment.symbol!!) {
          it.updateChartAndHeaderData()
        }
      }
    }
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

  private fun prepareTokenDetailData(hold: ArrayList<TransactionListModel>.() -> Unit) {
    TransactionTable.getTransactionsByAddressAndSymbol(
      WalletTable.current.address, fragment.symbol!!
    ) { transactions ->
      transactions.isNotEmpty().isTrue {
        hold( transactions.map { TransactionListModel(it) }.toArrayList())
      } otherwise {
        // 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
        TransactionListPresenter.updateTransactions(fragment.getMainActivity()) {
          it.isNotEmpty().isTrue {
            // 拉取的防落数据已经存放在数据库为了速度直接先显示内存里的数据
            hold(it.filter { it.symbol == fragment.symbol }.toArrayList())
          } otherwise {
            // 链上和本地都没有数据就更新一个空数组作为默认
            hold(arrayListOf())
          }
        }
      }
    }
  }

  private fun ArrayList<TokenBalanceTable>.updateChartAndHeaderData() {
    fragment.recyclerView.getItemViewAtAdapterPosition<TokenDetailHeaderView>(0) {
      val maxChartCount = 6
      val chartArray = arrayListOf<Point>()
      val charCount = if (size > maxChartCount) maxChartCount else lastIndex
      forEach {
        chartArray.add(Point(CryptoUtils.dateInDay(it.date), it.balance.toFloat()))
        if (chartArray.size == charCount) {
          var maxY = maxYValue(chartArray)
          var unitY = Math.ceil((maxY / 10)).toFloat()
          if (maxY == 0.0) maxY = 10.0
          if (unitY == 0f) unitY = 1f
          setCharData(chartArray.reversed().toArrayList(), maxY.toFloat(), unitY)
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
    TokenBalanceTable.updateTodayBalanceBySymbol(
      WalletTable.current.address, symbol
    ) { todayBalance ->
      TokenBalanceTable.getBalanceBySymbol(WalletTable.current.address, symbol) { balances ->
        // 如果除今天以外的最近一条数据更新的时间是昨天的整体时间, 那么只更新今天的价格
        balances.apply {
          if (size > 2) {
            if (this[lastIndex - 1].date == 1.daysAgoInMills()) {
              callback(this)
              return@getBalanceBySymbol
            }
          }
        }
        // 计算过去7天的所有余额
        generateHistoryBalance(todayBalance) { history ->
          coroutinesTask({
            history.forEach {
              TokenBalanceTable.insertOrUpdate(
                symbol, WalletTable.current.address, it.date, it.balance
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
  }

  data class DateBalance(val date: Long, val balance: Double)

  private fun ArrayList<TransactionListModel>.generateHistoryBalance(
    todayBalance: Double, callback: (ArrayList<DateBalance>) -> Unit
  ) {

    coroutinesTask({
      val oneDayAgoBalance = todayBalance - filter {
        it.timeStamp.toMills() in 1.daysAgoInMills() .. 0.daysAgoInMills()
      }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceived) }

      val twoDaysAgoBalance = oneDayAgoBalance - filter {
        it.timeStamp.toMills() in 2.daysAgoInMills() .. 1.daysAgoInMills()
      }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceived) }

      val threeDaysAgoBalance = twoDaysAgoBalance - filter {
        it.timeStamp.toMills() in 3.daysAgoInMills() .. 2.daysAgoInMills()
      }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceived) }

      val fourDaysAgoBalance = threeDaysAgoBalance - filter {
        it.timeStamp.toMills() in 4.daysAgoInMills() .. 3.daysAgoInMills()
      }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceived) }

      val fiveDaysAgoBalance = fourDaysAgoBalance - filter {
        it.timeStamp.toMills() in 5.daysAgoInMills() .. 4.daysAgoInMills()
      }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceived) }

      val sixDaysAgoBalance = fiveDaysAgoBalance - filter {
        it.timeStamp.toMills() in 6.daysAgoInMills() .. 5.daysAgoInMills()
      }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceived) }

      arrayListOf(
        DateBalance(6.daysAgoInMills(), sixDaysAgoBalance),
        DateBalance(5.daysAgoInMills(), fiveDaysAgoBalance),
        DateBalance(4.daysAgoInMills(), fourDaysAgoBalance),
        DateBalance(3.daysAgoInMills(), threeDaysAgoBalance),
        DateBalance(2.daysAgoInMills(), twoDaysAgoBalance),
        DateBalance(1.daysAgoInMills(), oneDayAgoBalance)
      )

    }) {
      // 确保全部数据计算完毕进行回调
      callback(it)
    }
  }

  private fun modulusByReceiveStatus(isReceive: Boolean) = if (isReceive) -1 else 1

}