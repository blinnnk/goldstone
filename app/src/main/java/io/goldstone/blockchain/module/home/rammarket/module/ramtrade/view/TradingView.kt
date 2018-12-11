package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import org.jetbrains.anko.*

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
@SuppressLint("ViewConstructor")
class TradingView(context: Context, val fragment: RAMMarketDetailFragment): LinearLayout(context) {
	private val tradingDashboardView = TradingDashboardView(context).apply {
    setShowHistoryEvent(Runnable {
      fragment.getParentFragment<RAMMarketOverlayFragment> {
        presenter.showTransactionHistoryFragment()
      }
    })
  }
	
	private val recentTradingListView  = RecentTradingListView(context) {
    fragment.getParentFragment<RAMMarketOverlayFragment> {
      presenter.showTransactionHistoryFragment(account)
    }
	}
	
	fun getRAMEditText(): String {
		return tradingDashboardView.ramEditText.text.toString().trim()
	}
	
	fun setRAMEditText(text: String) {
		tradingDashboardView.ramEditText.setText(text)
	}
	
	fun setRAMEditTextTitle(text: String) {
		tradingDashboardView.ramEditText.title = text
	}
	
	fun isRAMEditTextFocus(): Boolean {
		return tradingDashboardView.ramEditText.hasFocus()
	}
	
	fun getEOSEditText(): String {
		return tradingDashboardView.eosEditText.text.toString().trim()
	}
	
	fun setEOSEditText(text: String) {
		tradingDashboardView.eosEditText.setText(text)
	}
	fun isEOSEditTextFocus(): Boolean {
		return tradingDashboardView.eosEditText.hasFocus()
	}
	
	fun setRAMTextChangedRunnable(runnable: Runnable) {
		tradingDashboardView.ramEditText.afterTextChanged = runnable
	}
	
	fun setEOSTextChangedRunnable(runnable: Runnable) {
		tradingDashboardView.eosEditText.afterTextChanged = runnable
	}
	
	fun setRAMBalance(text: String) {
		tradingDashboardView.ramBalance.text = text
	}
	
	fun setEOSBalance(text: String) {
		tradingDashboardView.eosBalance.text = text
	}
	
	fun setConfirmEvent(runnable: Runnable) {
		tradingDashboardView.setConfirmEvent(runnable)
	}
	
	fun getStakeType(): StakeType {
		return tradingDashboardView.stakeType
	}
	
	fun setTradingListData(buyList: List<TradingInfoModel>, sellList: List<TradingInfoModel>) {
		recentTradingListView.setData(buyList, sellList)
	}
	
	fun notifyTradingListData() {
		recentTradingListView.adapter?.notifyDataSetChanged()
	}
	
	
	init {
		orientation = LinearLayout.VERTICAL
		rightPadding = RAMMarketPadding
		view {
			layoutParams = LinearLayout.LayoutParams(matchParent, 1)
			backgroundColor = GrayScale.lightGray
		}.setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
				leftMargin = RAMMarketPadding
		}
		
	  linearLayout {
			tradingDashboardView.into(this)
			recentTradingListView.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 290.uiPX())
			}.into(this)
		}
		recentTradingListView.setMargins<LinearLayout.LayoutParams> {
			topMargin = 7.uiPX()
			leftMargin = 5.uiPX()
		}
	}
}