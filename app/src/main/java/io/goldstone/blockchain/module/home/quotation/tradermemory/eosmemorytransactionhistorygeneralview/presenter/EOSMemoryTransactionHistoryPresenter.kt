package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorygeneralview.presenter

import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorygeneralview.view.EOSMemoryTransactionHistoryFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class EOSMemoryTransactionHistoryPresenter(
	override val fragment: EOSMemoryTransactionHistoryFragment
) : BasePresenter<EOSMemoryTransactionHistoryFragment>() {

	fun showAddButtonOrElse(isShown: Boolean, callback: () -> Unit) {
		fragment.getParentFragment<QuotationOverlayFragment> {
			overlayView.header.showAddButton(isShown, true, callback)
		}
	}
}