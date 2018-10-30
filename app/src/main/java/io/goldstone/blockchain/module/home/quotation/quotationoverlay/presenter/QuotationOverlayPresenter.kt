package io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter

import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketFragment

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

	fun showMarketTokenCenter(model: QuotationModel?) {
		fragment.addFragmentAndSetArgument<MarketTokenCenterFragment>(ContainerID.content) {
			putSerializable(ArgumentKey.quotationCurrencyDetail, model)
		}
	}

	fun showMarketTokenDetailFragment(model: QuotationModel?) {
		fragment.addFragmentAndSetArgument<MarketTokenDetailFragment>(ContainerID.content) {
			putSerializable(ArgumentKey.quotationCurrencyDetail, model)
		}
	}

	fun showTraderMemoryDetailOverlayFragment() {
		fragment.activity?.addFragmentAndSetArguments<RAMMarketFragment>(ContainerID.main) {
			putString(
				"内存交易",
				"内存交易"
			)
		}
	}

	fun showQuotationSearchFragment() {
		showTargetFragment<QuotationSearchFragment>()
		fragment.overlayView.header.apply {
			showBackButton(false)
			showSearchInput {
				popFragmentFrom<QuotationSearchFragment>()
				fragment.headerTitle = QuotationText.management
			}
		}
	}
}