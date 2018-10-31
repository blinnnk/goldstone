package io.goldstone.blockchain.module.home.rammarket.ramtrade.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class RecentTradingListView(context: Context): RecyclerView(context) {
	
	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		layoutManager = LinearLayoutManager(context)
	}
	
}

