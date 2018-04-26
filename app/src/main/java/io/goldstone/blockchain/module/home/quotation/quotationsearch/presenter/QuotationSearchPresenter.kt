package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSearchModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment
import org.jetbrains.anko.runOnUiThread

/**
 * @date 21/04/2018 4:32 PM
 * @author KaySaith
 */

class QuotationSearchPresenter(
  override val fragment: QuotationSearchFragment
) : BaseRecyclerPresenter<QuotationSearchFragment, QuotationSearchModel>() {

  override fun updateData() {
    fragment.asyncData = arrayListOf()
  }

  override fun onFragmentViewCreated() {
    super.onFragmentViewCreated()
    setHeightMatchParent()
    fragment.getParentFragment<QuotationOverlayFragment> {
      overlayView.header.setKeyboardConfirmEvent {
        getMainActivity()?.showLoadingView()
        searchTokenBy(text.toString())
      }
    }
  }

  private fun searchTokenBy(symbol: String) {
    GoldStoneAPI.getMarketSearchList(symbol) {
      fragment.apply {
        context?.runOnUiThread {
          getMainActivity()?.removeLoadingView()
          diffAndUpdateSingleCellAdapterData<QuotationSearchAdapter>(it.map { QuotationSearchModel(it) }.toArrayList())
        }
      }
    }
  }

}