package io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
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
	
	fun showQutationManagementFragment() {
		fragment.addFragmentAndSetArgument<QuotationManagementFragment>(ContainerID.content)
	}
	
	fun showMarketTokenDetailFragment(model: QuotationModel?) {
		fragment.addFragmentAndSetArgument<MarketTokenDetailFragment>(ContainerID.content) {
			putSerializable(ArgumentKey.quotationCurrencyDetail, model)
		}
	}
	
	fun showQutationSearchFragment() {
		showTargetFragment<QuotationSearchFragment>(
			QuotationText.search,
			QuotationText.management
		)
		fragment.overlayView.header.apply {
			showBackButton(false)
			showSearchInput {
				popFragmentFrom<QuotationSearchFragment>()
				fragment.headerTitle = QuotationText.management
			}
		}
	}
}