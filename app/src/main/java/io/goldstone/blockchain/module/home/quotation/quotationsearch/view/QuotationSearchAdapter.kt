package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable

/**
 * @date 21/04/2018 4:33 PM
 * @author KaySaith
 */

class QuotationSearchAdapter(
  override val dataSet: ArrayList<QuotationSelectionTable>,
  private val hold: (QuotationSearchCell) -> Unit
) : HoneyBaseAdapter<QuotationSelectionTable, QuotationSearchCell>() {

  override fun generateCell(context: Context) = QuotationSearchCell(context)

  override fun QuotationSearchCell.bindCell(data: QuotationSelectionTable, position: Int) {
    searchModel = data
    hold(this)
  }

}