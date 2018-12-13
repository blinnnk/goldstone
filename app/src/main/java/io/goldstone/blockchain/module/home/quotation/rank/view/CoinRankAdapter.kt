package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankAdapter(
	override val dataSet: ArrayList<CoinRankModel>,
	private val holdHeader: CoinRankHeader.() -> Unit,
	private val hold: CoinRankCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<CoinRankModel, CoinRankHeader, CoinRankCell, View>() {
	override fun generateCell(context: Context) = CoinRankCell(context)
	
	override fun generateFooter(context: Context) = View(context)
	
	override fun generateHeader(context: Context) = CoinRankHeader(context).apply(holdHeader)
	
	override fun CoinRankCell.bindCell(
		data: CoinRankModel,
		position: Int
	) {
		model = data
		hold(this)
	
	}
}