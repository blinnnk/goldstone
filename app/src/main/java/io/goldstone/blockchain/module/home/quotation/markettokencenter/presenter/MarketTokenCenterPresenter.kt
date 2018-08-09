package io.goldstone.blockchain.module.home.quotation.markettokencenter.presenter

import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment

/**
 * @date 2018/8/9 5:02 PM
 * @author KaySaith
 */

class MarketTokenCenterPresenter(
	override val fragment: MarketTokenCenterFragment
) : BasePresenter<MarketTokenCenterFragment>() {

	fun showAddButtonOrElse(isShown: Boolean, callback: () -> Unit) {
		fragment.getParentFragment<QuotationOverlayFragment> {
			overlayView.header.showAddButton(isShown, true, callback)
		}
	}
}