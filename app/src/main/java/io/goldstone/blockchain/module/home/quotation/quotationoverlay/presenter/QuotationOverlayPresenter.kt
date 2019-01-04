package io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.language.EmptyText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationrank.view.QuotationRankFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 */
class QuotationOverlayPresenter(
	override val fragment: QuotationOverlayFragment
) : BaseOverlayPresenter<QuotationOverlayFragment>() {

	fun showQuotationManagementFragment() {
		fragment.addFragmentAndSetArgument<QuotationManagementFragment>(ContainerID.content)
	}

	fun showQuotationRankFragment() {
		fragment.addFragmentAndSetArgument<QuotationRankFragment>(ContainerID.content)
	}

	fun showMarketTokenCenter(model: QuotationModel?) {
		fragment.addFragmentAndSetArgument<MarketTokenCenterFragment>(ContainerID.content) {
			putSerializable(ArgumentKey.quotationCurrencyDetail, model)
		}
	}

	fun showQuotationSearchFragment() {
		showTargetFragment<QuotationSearchFragment>()
		fragment.apply {
			showBackButton(false) {}
			showSearchInput(
				cancelEvent = {
					popFragmentFrom<QuotationSearchFragment>()
					fragment.headerTitle = QuotationText.management
				},
				enterKeyEvent = {},
				hint = EmptyText.pairSearchInput
			)
		}
	}
}