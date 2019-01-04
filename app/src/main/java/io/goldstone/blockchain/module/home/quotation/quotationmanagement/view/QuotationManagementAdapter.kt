package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementAdapter(
	override val dataSet: ArrayList<QuotationSelectionTable>,
	private val switchEvent: (model: QuotationSelectionTable, isChecked: Boolean) -> Unit
) : HoneyBaseAdapter<QuotationSelectionTable, QuotationManagementCell>() {

	override fun generateCell(context: Context) =
		QuotationManagementCell(context)

	override fun QuotationManagementCell.bindCell(data: QuotationSelectionTable, position: Int) {
		quotationSearchModel = data
		with(switch) {
			onClick {
				switchEvent(data, isChecked)
				preventDuplicateClicks()
			}
		}
	}

}