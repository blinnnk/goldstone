package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.view.*
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.module.home.quotation.rank.model.RankHeaderModel
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable

/**
 * @date: 2018/8/14.
 * @author: yanglihai
 * @description: rank列表的适配器
 */
class RankListAdapter(override val dataSet: ArrayList<RankTable>) :
	HoneyBaseAdapterWithHeaderAndFooter<RankTable, View, RankItemCell, View>() {
	
	
	lateinit var rankHeaderView: RankHeaderView
	
	override fun generateCell(context: Context) = RankItemCell(context)
	
	override fun generateFooter(context: Context) = View(context)
	
	
	override fun generateHeader(context: Context): View {
		rankHeaderView = RankHeaderView(context)
		return rankHeaderView
	}
	
	override fun RankItemCell.bindCell(
		data: RankTable,
		position: Int
	) {
		rankModel = data
	}
	
	fun updateRankHeaderViewData(rankHeaderModel: RankHeaderModel) {
		rankHeaderView.updateHeaderData(rankHeaderModel)
	}
	
	
}