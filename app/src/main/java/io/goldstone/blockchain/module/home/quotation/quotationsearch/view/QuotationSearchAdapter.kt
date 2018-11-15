package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 21/04/2018 4:33 PM
 * @author KaySaith
 */

class QuotationSearchAdapter(
	override val dataSet: ArrayList<QuotationSelectionTable>,
	private val cellClickEvent: (model: QuotationSelectionTable, isChecked: Boolean) -> Unit
) : HoneyBaseAdapter<QuotationSelectionTable, QuotationSearchCell>() {

	override fun generateCell(context: Context) = QuotationSearchCell(context)

	override fun QuotationSearchCell.bindCell(data: QuotationSelectionTable, position: Int) {
		quotationSearchModel = data
		switch.onClick {
			cellClickEvent(data, switch.isChecked)
			preventDuplicateClicks()
		}
	}

}