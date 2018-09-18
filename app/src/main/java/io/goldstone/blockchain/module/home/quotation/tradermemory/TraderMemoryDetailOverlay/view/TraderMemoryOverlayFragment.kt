package io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter.QuotationOverlayPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.presenter.TraderMemoryOverlayPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 */
class TraderMemoryOverlayFragment : BaseOverlayFragment<TraderMemoryOverlayPresenter>() {

	private val title by lazy { arguments?.getString("内存交易") }
	private val currencyInfo by lazy {
		arguments?.getSerializable(ArgumentKey.quotationOverlayInfo) as? QuotationModel
	}
	override val presenter = TraderMemoryOverlayPresenter(this)
	override fun ViewGroup.initView() {
		when (title) {
			"内存交易" -> {
				presenter.showTraderMemoryDetailFragment()
			}
		}
		headerTitle = title ?: currencyInfo?.pairDisplay.orEmpty()
	}
}