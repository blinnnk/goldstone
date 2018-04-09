package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
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

  private val symbol by lazy { fragment.arguments?.getString(ArgumentKey.tokenDetail) }

  override fun updateData(asyncData: ArrayList<TransactionListModel>?) {
    prepareTokenDetailData {
      isNotEmpty().isTrue {
        fragment.asyncData = map { TransactionListModel(it) }.toArrayList()
        prepareTokenHistoryBalance(symbol!!) {
          fragment.updateChartAndHeaderData()
        }
      } otherwise {
        fragment.asyncData = arrayListOf() // TODO 还需要显示空的占位图
        prepareTokenHistoryBalance(symbol!!) {
          fragment.updateChartAndHeaderData()
        }
      }
    }
  }

  fun showAddressSelectionFragment() {
    WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
      shoTargetFragment<AddressSelectionFragment>(TokenDetailText.address)
    }
  }

  fun showTransactionDetailFragment() {
    shoTargetFragment<TransactionDetailFragment>(TransactionText.detail)
  }

  fun showDepositFragment() {
    WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
      //
    }
  }

  private var singleRunMark: Boolean? = null
  private fun prepareTokenDetailData(hold: ArrayList<TransactionTable>.() -> Unit = {}) {
    TransactionTable.getTransactionsByAddressAndSymbol(
      WalletTable.current.address,
      symbol!!
    ) { transactions ->
      transactions.isNotEmpty().isTrue {
        hold(transactions)
      } otherwise {
        // 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
        TransactionListPresenter.getTransactionDataFromEtherScan(fragment.getMainActivity()!!) {
          it.isNotEmpty().isTrue {
            singleRunMark.isNull {
              prepareTokenDetailData(hold)
              singleRunMark = true
            }
          }
        }
        // 若更新了链上数据后还是没有筛选出交易记录返回空数组
        if (singleRunMark == true) hold(arrayListOf())
      }
    }
  }

  private fun TokenDetailFragment.updateChartAndHeaderData() {
    recyclerView.getItemViewAtAdapterPosition<TokenDetailHeaderView>(0) {
      TokenBalanceTable.getTokenBalanceBySymbol(WalletTable.current.address, symbol!!) {
        val maxChartCount = 6
        val chartArray = arrayListOf<Point>()
        val charCount = if (size > maxChartCount) maxChartCount else lastIndex
        forEachIndexed { index, it ->
          chartArray.add(Point(CryptoUtils.dateInDay(it.date), it.balance.toFloat()))
          if (index == charCount) {
            var maxY = Math.ceil(chartArray.maxBy { it.value }?.value!!.toDouble()) * 1.5
            var unitY = Math.ceil((maxY / 10)).toFloat()
            if (maxY == 0.0) maxY = 10.0
            if (unitY == 0f) unitY = 1f
            setCharData(chartArray, maxY.toFloat(), unitY)
            return@getTokenBalanceBySymbol
          }
        }
      }
    }
  }

  private fun ArrayList<TransactionTable>.prepareTokenHistoryBalance(
    symbol: String, callback: () -> Unit
  ) {
    // 首先更新此刻最新的余额数据到今天的数据
    TokenBalanceTable.updateTodayBalanceBySymbol(
      WalletTable.current.address,
      symbol
    ) { todayBalance ->

      TokenBalanceTable.getTokenBalanceBySymbol(WalletTable.current.address, symbol) {
        // 如果除今天以外的最近一条数据更新的时间是昨天的整体时间, 那么只更新今天的价格
        if (size > 2) {
          if (this[lastIndex - 1].date == 1.daysAgoInMills()) {
            callback()
            return@getTokenBalanceBySymbol
          }
        }
        // 计算过去7天的所有余额
        generateHistoryBalance(todayBalance) {
          coroutinesTask({
            it.forEach {
              TokenBalanceTable.insertOrUpdate(
                symbol,
                WalletTable.current.address,
                it.date,
                it.balance
              )
            }
          }) {
            callback()
          }
        }
      }
    }
  }

  data class DateBalance(val date: Long, val balance: Double)

  private fun ArrayList<TransactionTable>.generateHistoryBalance(
    todayBalance: Double, callback: (ArrayList<DateBalance>) -> Unit
  ) {

    val oneDayAgoBalance = todayBalance - filter {
      it.timeStamp.toMills() in 1.daysAgoInMills() .. 0.daysAgoInMills()
    }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceive) }

    val twoDaysAgoBalance = oneDayAgoBalance - filter {
      it.timeStamp.toMills() in 2.daysAgoInMills() .. 1.daysAgoInMills()
    }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceive) }

    val threeDaysAgoBalance = twoDaysAgoBalance - filter {
      it.timeStamp.toMills() in 3.daysAgoInMills() .. 2.daysAgoInMills()
    }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceive) }

    val fourDaysAgoBalance = threeDaysAgoBalance - filter {
      it.timeStamp.toMills() in 4.daysAgoInMills() .. 3.daysAgoInMills()
    }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceive) }

    val fiveDaysAgoBalance = fourDaysAgoBalance - filter {
      it.timeStamp.toMills() in 5.daysAgoInMills() .. 4.daysAgoInMills()
    }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceive) }

    val sixDaysAgoBalance = fiveDaysAgoBalance - filter {
      it.timeStamp.toMills() in 6.daysAgoInMills() .. 5.daysAgoInMills()
    }.sumByDouble { it.value.toDouble() * modulusByReceiveStatus(it.isReceive) }

    callback(
      arrayListOf(
        DateBalance(6.daysAgoInMills(), sixDaysAgoBalance),
        DateBalance(5.daysAgoInMills(), fiveDaysAgoBalance),
        DateBalance(4.daysAgoInMills(), fourDaysAgoBalance),
        DateBalance(3.daysAgoInMills(), threeDaysAgoBalance),
        DateBalance(2.daysAgoInMills(), twoDaysAgoBalance),
        DateBalance(1.daysAgoInMills(), oneDayAgoBalance)
      )
    )
  }

  private fun modulusByReceiveStatus(isReceive: Boolean) = if (isReceive) 1 else -1

  private inline fun <reified T : Fragment> shoTargetFragment(title: String) {
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      hideChildFragment(fragment)
      addFragmentAndSetArgument<T>(ContainerID.content) {
        // Send Arguments
      }
      overlayView.header.apply {
        showBackButton(true) {
          presenter.setValueHeader(title)
          presenter.popFragmentFrom<T>()
          setHeightMatchParent()
        }
        showCloseButton(false)
      }
      presenter.resetHeader()
      headerTitle = title
    }
  }

}