package io.goldstone.blockchain.module.home.quotation.quotationsearch.contract

import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable


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
		fun updateLocalQuotation(
			model: QuotationSelectionTable,
			isSelect: Boolean,
			callback: (error: GoldStoneError) -> Unit
		)
	}
}