package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 21/04/2018 4:32 PM
 * @author KaySaith
 */

class QuotationSearchPresenter(
  override val fragment: QuotationSearchFragment
) : BaseRecyclerPresenter<QuotationSearchFragment, DefaultTokenTable>() {

  override fun updateData() {
    fragment.asyncData = arrayListOf(
      DefaultTokenTable(TokenSearchModel("", "", "BTC", "563.23", "ethereume", 18, 100), false),
      DefaultTokenTable(TokenSearchModel("", "", "EOS", "22.29", "ethereume", 18, 100), false),
      DefaultTokenTable(TokenSearchModel("", "", "TRX", "1.15", "ethereume", 18, 100), false),
      DefaultTokenTable(TokenSearchModel("", "", "QTM", "12.88", "ethereume", 18, 100), false)
    )
  }

}