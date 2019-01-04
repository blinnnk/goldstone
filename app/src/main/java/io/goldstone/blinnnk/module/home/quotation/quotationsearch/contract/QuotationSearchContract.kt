package io.goldstone.blinnnk.module.home.quotation.quotationsearch.contract

import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.QuotationSelectionTable


/**
 * @author KaySaith
 * @date  2018/11/17
 */
interface QuotationSearchContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		var asyncData: ArrayList<QuotationSelectionTable>?
		fun showLoading(status: Boolean)
		fun showFilterDescription(data: List<ExchangeTable>)
		fun updateUI(data: List<QuotationSelectionTable>)
	}

	interface GSPresenter : GoldStonePresenter {
		fun searchToken(symbol: String)
		fun getSelectedExchange(hold: List<ExchangeTable>.() -> Unit)
		fun updateSelectedExchangeID(ids: List<Int>)
		fun updateLocalQuotation(
			model: QuotationSelectionTable,
			isSelect: Boolean,
			callback: (error: GoldStoneError) -> Unit
		)
	}
}