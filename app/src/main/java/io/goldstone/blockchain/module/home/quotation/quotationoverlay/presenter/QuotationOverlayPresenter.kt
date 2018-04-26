package io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter

import com.blinnnk.extension.findChildFragmentByTag
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 */

class QuotationOverlayPresenter(
  override val fragment: QuotationOverlayFragment
  ) : BaseOverlayPresenter<QuotationOverlayFragment>() {

  override fun removeSelfFromActivity() {
    super.removeSelfFromActivity()
    fragment.getMainActivity()?.apply {
      supportFragmentManager.findFragmentByTag(FragmentTag.home)?.apply {
        findChildFragmentByTag<QuotationFragment>(FragmentTag.quotation)?.let {
          it.presenter.updateData()
        }
      }
    }
  }

  fun showQutationManagementFragment() {
    fragment.addFragmentAndSetArgument<QuotationManagementFragment>(ContainerID.content) {
      //
    }
  }

  fun showMarketTokenDetailFragment() {
    fragment.addFragmentAndSetArgument<MarketTokenDetailFragment>(ContainerID.content) {
      //
    }
  }

  fun showQutationSearchFragment() {
    showTargetFragment<QuotationSearchFragment>(QuotationText.search, QuotationText.management)
    fragment.overlayView.header.apply {
      showBackButton(false)
      showSearchInput {
        popFragmentFrom<QuotationSearchFragment>()
        fragment.headerTitle = QuotationText.management
      }
    }
  }

}