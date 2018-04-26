package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSearchModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author KaySaith
 */

class QuotationSearchFragment : BaseRecyclerFragment<QuotationSearchPresenter, QuotationSearchModel>() {

  override val presenter = QuotationSearchPresenter(this)
  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView, asyncData: ArrayList<QuotationSearchModel>?
  ) {
    recyclerView.adapter = QuotationSearchAdapter(asyncData.orEmptyArray())
  }
}