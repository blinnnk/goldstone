package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankAdapter(
	override val dataSet: ArrayList<RAMRankModel>,
	private val hold: RAMOccupyRankCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<RAMRankModel, RAMOccupyRankHeaderView, RAMOccupyRankCell, View>() {
	
	override fun generateHeader(context: Context) = RAMOccupyRankHeaderView(context)
	override fun generateCell(context: Context) = RAMOccupyRankCell(context)
	override fun generateFooter(context: Context) = View(context)
	
	override fun RAMOccupyRankCell.bindCell(
		data: RAMRankModel,
		position: Int
	) {
		model = data
		hold(this)
	}
}