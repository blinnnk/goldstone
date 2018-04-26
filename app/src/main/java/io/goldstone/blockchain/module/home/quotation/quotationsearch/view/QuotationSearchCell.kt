package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListCell

/**
 * @date 21/04/2018 4:33 PM
 * @author KaySaith
 */

class QuotationSearchCell(context: Context) : TokenManagementListCell(context) {


  init {
    switch.isChecked = false
    hideIcon()
  }

}