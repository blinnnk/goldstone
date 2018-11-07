package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class RecentTradingListView(context: Context): RecyclerView(context) {
	
	init {
		layoutManager = LinearLayoutManager(context)
		setHasFixedSize(true)
		isNestedScrollingEnabled = false
	}
	
	fun setData(buyList: List<TradingInfoModel>, sellList: List<TradingInfoModel>) {
		adapter = RecentTradingAdapter(buyList, sellList)
	}
	
}

