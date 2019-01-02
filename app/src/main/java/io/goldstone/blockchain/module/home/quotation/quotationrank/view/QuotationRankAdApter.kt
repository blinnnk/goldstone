package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankModel


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankAdapter(
	override val dataSet: ArrayList<QuotationRankModel>,
	private val holdClickAction: QuotationRankModel.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<QuotationRankModel, QuotationRankHeaderView, QuotationRankCell, View>() {
	override fun generateCell(context: Context) = QuotationRankCell(context)
	override fun generateFooter(context: Context) = View(context)
	override fun generateHeader(context: Context) =
		QuotationRankHeaderView(context)
	override fun QuotationRankCell.bindCell(data: QuotationRankModel, position: Int) {
		model = data
		click {
			holdClickAction(data)
		}
	}

}