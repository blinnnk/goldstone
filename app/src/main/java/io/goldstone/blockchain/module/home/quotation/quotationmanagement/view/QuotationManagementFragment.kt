package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.QuotationSize
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter.QuotationManagementPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementFragment : BaseRecyclerFragment<QuotationManagementPresenter, DefaultTokenTable>() {

  override val presenter = QuotationManagementPresenter(this)
  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView, asyncData: ArrayList<DefaultTokenTable>?
  ) {
    recyclerView.adapter = QuotationManagementAdapter(asyncData.orEmptyArray())
  }

  override fun setSlideUpWithCellHeight() = QuotationSize.cellHeight

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recyclerView.addDragEventAndReordering(asyncData.orEmptyArray()) { fromPosition, toPosition ->
      System.out.println("from $fromPosition to $toPosition")
    }
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    getParentFragment<QuotationOverlayFragment> {
      if(hidden) {
        overlayView.header.showSearchButton(false)
      }
      else overlayView.header.showSearchButton(true) {
        presenter.showQutationSearchFragment()
      }
    }
  }
}