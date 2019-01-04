package io.goldstone.blinnnk.module.home.quotation.quotationoverlay.view

import android.view.ViewGroup
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blinnnk.module.home.quotation.quotationoverlay.presenter.QuotationOverlayPresenter

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 */
class QuotationOverlayFragment : BaseOverlayFragment<QuotationOverlayPresenter>() {

	private val title by lazy {
		arguments?.getString(ArgumentKey.quotationOverlayTitle)
	}
	private val currencyInfo by lazy {
		arguments?.getSerializable(ArgumentKey.quotationOverlayInfo) as? QuotationModel
	}
	override val presenter = QuotationOverlayPresenter(this)
	override fun ViewGroup.initView() {
		when (title) {
			QuotationText.management -> {
				presenter.showQuotationManagementFragment()
				showSearchButton(true) {
					presenter.showQuotationSearchFragment()
				}
			}
			QuotationText.rank -> presenter.showQuotationRankFragment()
			else -> presenter.showMarketTokenCenter(currencyInfo)
		}

		headerTitle = title ?: currencyInfo?.pairDisplay.orEmpty()
	}
}