package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankTable


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankAdapter(
	override val dataSet: ArrayList<QuotationRankTable>,
	private val holdHeader: QuotationRankHeaderView.() -> Unit,
	private val holdFooter: BottomLoadingView.() -> Unit,
	private val holdClickAction: QuotationRankCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<QuotationRankTable, QuotationRankHeaderView, QuotationRankCell, View>() {
	override fun generateCell(context: Context) = QuotationRankCell(context)
	override fun generateFooter(context: Context) = BottomLoadingView(context).apply(holdFooter)
	override fun generateHeader(context: Context) = QuotationRankHeaderView(context).apply(holdHeader)
	override fun QuotationRankCell.bindCell(data: QuotationRankTable, position: Int) {
		model = data
		click(holdClickAction)
	}
	
}
