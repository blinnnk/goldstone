package io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.view

import android.view.ViewGroup
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.presenter.TraderMemoryOverlayPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecord.view.TraderMemorySalesRecordFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
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

	companion object {
		fun removeContentOverlayOrElse(
			fragment: TraderMemorySalesRecordFragment,
			callback: () -> Unit
		) {
			fragment.getParentContainer()
				?.findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
				.apply {
					if (isNull()) callback()
					else this?.remove()
				}
		}
	}
}