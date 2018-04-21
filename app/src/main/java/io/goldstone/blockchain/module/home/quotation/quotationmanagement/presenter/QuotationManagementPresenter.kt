package io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementPresenter(
  override val fragment: QuotationManagementFragment
  ) : BaseRecyclerPresenter<QuotationManagementFragment, DefaultTokenTable>() {

  override fun updateData() {
    fragment.asyncData = arrayListOf(
      DefaultTokenTable(TokenSearchModel("", "", "ETH", "563.23", "ethereume", 18, 100), true),
      DefaultTokenTable(TokenSearchModel("", "", "EOS", "22.29", "ethereume", 18, 100), true),
      DefaultTokenTable(TokenSearchModel("", "", "TRX", "1.15", "ethereume", 18, 100), true),
      DefaultTokenTable(TokenSearchModel("", "", "GS", "12.88", "ethereume", 18, 100), true)
    )
  }

}