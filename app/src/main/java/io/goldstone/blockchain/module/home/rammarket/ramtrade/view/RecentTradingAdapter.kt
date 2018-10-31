package io.goldstone.blockchain.module.home.rammarket.ramtrade.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class RecentTradingAdapter: RecyclerView.Adapter<RecentTradingAdapter.TradingHolder>() {
	override fun onCreateViewHolder(
		parent: ViewGroup,
		p1: Int
	): TradingHolder {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
	override fun getItemCount(): Int = 12
	
	override fun onBindViewHolder(
		holder: TradingHolder,
		position: Int
	) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
	class TradingHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}