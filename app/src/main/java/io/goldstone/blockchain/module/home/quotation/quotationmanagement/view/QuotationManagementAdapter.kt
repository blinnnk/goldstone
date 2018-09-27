package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementAdapter(
  override val dataSet: ArrayList<QuotationSelectionTable>,
  private val hold: (QuotationManagementCell) -> Unit
  ) : HoneyBaseAdapter<QuotationSelectionTable, QuotationManagementCell>() {

  override fun generateCell(context: Context) = QuotationManagementCell(context)

  override fun QuotationManagementCell.bindCell(data: QuotationSelectionTable, position: Int) {
		quotationSearchModel = data
    hold(this)
  }

}