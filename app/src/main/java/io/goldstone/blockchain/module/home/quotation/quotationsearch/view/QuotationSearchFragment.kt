package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.view.ViewGroup
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author KaySaith
 */

class QuotationSearchFragment :
	BaseRecyclerFragment<QuotationSearchPresenter, QuotationSelectionTable>() {

	override val presenter = QuotationSearchPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationSearchAdapter(asyncData.orEmptyArray()) { cell ->
			cell.searchModel?.let { model ->
				cell.switch.onClick {
					getMainActivity()?.showLoadingView()
					presenter.setQuotationSelfSelection(model, cell.switch.isChecked) {
						getMainActivity()?.removeLoadingView()
					}
				}
			}
		}
	}
	
	override fun recoveryBackEvent() {
		getMainActivity()?.apply {
			backEvent = Runnable {
				val overlayView = findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
				if (overlayView.isNull()) {
					setBackEvent(this)
				}else {
					overlayView.remove()
				}
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<QuotationOverlayFragment> {
			headerTitle = QuotationText.management
			presenter.popFragmentFrom<QuotationSearchFragment>()
			overlayView.header.showSearchInput(false)
		}
	}
	
}