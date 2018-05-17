package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
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
}