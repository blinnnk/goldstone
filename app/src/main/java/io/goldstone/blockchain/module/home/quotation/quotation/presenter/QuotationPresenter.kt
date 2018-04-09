package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
  override val fragment: QuotationFragment
  ) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

}