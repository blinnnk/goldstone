package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author KaySaith
 */

class QuotationSearchFragment :
	BaseRecyclerFragment<QuotationSearchPresenter, QuotationSelectionTable>() {

	override val pageTitle: String = "Quotation Search"
	override val presenter = QuotationSearchPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationSearchAdapter(asyncData.orEmptyArray()) { cell ->
			cell.quotationSearchModel?.let { model ->
				cell.switch.onClick { _ ->
					getMainActivity()?.showLoadingView()
					presenter.updateMyQuotation(model, cell.switch.isChecked) {
						GoldStoneAPI.context.runOnUiThread {
							this@QuotationSearchFragment.getMainActivity()?.removeLoadingView()
							if (it.hasError()) this@QuotationSearchFragment.context.alert(it.message)
						}
					}
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