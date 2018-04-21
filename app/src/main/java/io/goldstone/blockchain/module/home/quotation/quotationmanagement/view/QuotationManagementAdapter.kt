package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementAdapter(
  override val dataSet: ArrayList<DefaultTokenTable>
  ) : HoneyBaseAdapter<DefaultTokenTable, QuotationManagementCell>() {

  override fun generateCell(context: Context) = QuotationManagementCell(context)

  override fun QuotationManagementCell.bindCell(data: DefaultTokenTable, position: Int) {
    model = data
  }

}