package io.goldstone.blinnnk.module.home.quotation.quotationoverlay.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blinnnk.common.language.EmptyText
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blinnnk.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blinnnk.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blinnnk.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blinnnk.module.home.quotation.quotationrank.view.QuotationRankFragment
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.view.QuotationSearchFragment

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