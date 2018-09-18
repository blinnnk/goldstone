package io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ArgumentKey.quotationOverlayTitle
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.view.TraderMemoryOverlayFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryOverlayPresenter(
	override val fragment: TraderMemoryOverlayFragment
) : BaseOverlayPresenter<TraderMemoryOverlayFragment>() {

	fun showTraderMemoryDetailFragment() {
		fragment.addFragmentAndSetArgument<TraderMemoryDetailFragment>(ContainerID.content) {
		}
	}
}