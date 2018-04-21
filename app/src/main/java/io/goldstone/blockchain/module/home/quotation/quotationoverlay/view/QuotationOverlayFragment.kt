package io.goldstone.blockchain.module.home.quotation.quotationoverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter.QuotationOverlayPresenter

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 */

class QuotationOverlayFragment : BaseOverlayFragment<QuotationOverlayPresenter>() {

  override val presenter = QuotationOverlayPresenter(this)

  override fun ViewGroup.initView() {
    headerTitle = QuotationText.management
    presenter.showQutationManagementFragment()

    overlayView.header.apply {
      showSearchButton(true) {
        presenter.showQutationSearchFragment()
      }
    }
  }

}