package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.presenter.RAMTransactionSearchPresenter
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author yangLiHai
 */

class RAMTransactionSearchFragment:
	BaseRecyclerFragment<RAMTransactionSearchPresenter, TradingInfoModel>() {

	override val pageTitle: String = "Quotation Search"
	override val presenter = RAMTransactionSearchPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TradingInfoModel>?
	) {
		recyclerView.adapter = TransactionsOfNameAdapter(asyncData.orEmptyArray()) { }
	}

	override fun recoveryBackEvent() {
		getMainActivity()?.apply {
			backEvent = Runnable {
				val overlayView =
					findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
				if (overlayView.isNull()) setBackEvent(this)
				else overlayView.remove()
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<RAMMarketOverlayFragment> {
			headerTitle = QuotationText.management
			presenter.popFragmentFrom<RAMTransactionSearchFragment>()
			getOverlayHeader().showSearchInput(false) {}
		}
	}
}





