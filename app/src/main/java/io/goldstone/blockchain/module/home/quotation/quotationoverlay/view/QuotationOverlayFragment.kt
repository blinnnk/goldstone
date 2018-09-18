package io.goldstone.blockchain.module.home.quotation.quotationoverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter.QuotationOverlayPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 */
class QuotationOverlayFragment : BaseOverlayFragment<QuotationOverlayPresenter>() {

	private val title by lazy { arguments?.getString(ArgumentKey.quotationOverlayTitle) }
	private val currencyInfo by lazy {
		arguments?.getSerializable(ArgumentKey.quotationOverlayInfo) as? QuotationModel
	}
	override val presenter = QuotationOverlayPresenter(this)
	override fun ViewGroup.initView() {
		when (title) {
			QuotationText.management -> {
				presenter.showQutationManagementFragment()
				overlayView.header.showSearchButton(true) {
					presenter.showQutationSearchFragment()
				}
			}
			else -> {
				presenter.showMarketTokenCenter(currencyInfo)
				overlayView.header.showAddButton(true) {
					presenter.showTraderMemoryDetailOverlayFragment()
				}
			}
			//presenter.showMarketTokenDetailFragment(currencyInfo)
		}

		headerTitle = title ?: currencyInfo?.pairDisplay.orEmpty()
	}
}