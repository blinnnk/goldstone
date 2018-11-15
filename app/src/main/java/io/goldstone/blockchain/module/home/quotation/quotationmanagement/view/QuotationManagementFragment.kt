package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter.QuotationManagementPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */
class QuotationManagementFragment :
	BaseRecyclerFragment<QuotationManagementPresenter, QuotationSelectionTable>() {

	override val pageTitle: String = QuotationText.management
	override val presenter = QuotationManagementPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationManagementAdapter(asyncData.orEmptyArray()) { data, isChecked ->
			// 更新内存里面的数据防止复用的时候出错
			asyncData?.find { selection ->
				selection.pair.equals(data.pair, true)
			}?.isSelecting = isChecked
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		presenter.checkAndUpdateQuotationData()
		// 从下一个界面返回的时候更新这个界面的 `UI` 数据
		getParentFragment<QuotationOverlayFragment> {
			if (hidden) showSearchButton(false) {}
			else {
				showSearchButton(true) {
					presenter.showQuotationSearchFragment()
				}
				showCloseButton(true) {
					presenter.removeSelfFromActivity()
				}
			}
		}
	}
}