package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.QuotationText
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

	override val pageTitle: String = QuotationText.management
	private var willDeletePair = listOf<String>()
	override val presenter = QuotationManagementPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationManagementAdapter(asyncData.orEmptyArray()) { cell ->
			cell.switch.onClick { _ ->
				cell.quotationSearchModel?.apply {
					// 更新内存里面的数据防止复用的时候出错
					asyncData?.find { selection ->
						selection.pair.equals(pair, true)
					}?.isSelecting = cell.switch.isChecked
					// 更新标记, 来在页面销毁的时候决定是否集中处理逻辑
					if (cell.switch.isChecked) {
						willDeletePair += pair
					} else {
						willDeletePair.filterNot { it.equals(pair, true) }
					}
				}
			}
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		presenter.checkAndUpdateQuotationData()
		// 从下一个界面返回的时候更新这个界面的 `UI` 数据
		getParentFragment<QuotationOverlayFragment> {
			if (hidden) {
				overlayView.header.showSearchButton(false)
			} else overlayView.header.showSearchButton(true) {
				presenter.showQuotationSearchFragment()
			}
		}
	}
}