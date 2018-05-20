package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.QuotationSize
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter.QuotationManagementPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementFragment :
	BaseRecyclerFragment<QuotationManagementPresenter, QuotationSelectionTable>() {

	override val presenter = QuotationManagementPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationManagementAdapter(asyncData.orEmptyArray()) { cell ->
			cell.switch.onClick {
				cell.searchModel?.apply {
					QuotationSelectionTable.removeSelectionBy(pair)
				}
			}
		}
	}

	override fun setSlideUpWithCellHeight() =
		QuotationSize.cellHeight

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		// 从下一个界面返回的时候更新这个界面的 `UI` 数据
		getParentFragment<QuotationOverlayFragment> {
			if (hidden) {
				overlayView.header.showSearchButton(false)
			} else overlayView.header.showSearchButton(true) {
				presenter.showQutationSearchFragment()
			}
		}
	}

}