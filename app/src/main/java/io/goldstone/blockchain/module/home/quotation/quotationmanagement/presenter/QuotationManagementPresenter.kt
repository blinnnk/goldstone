package io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementAdapter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.runOnUiThread

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */
class QuotationManagementPresenter(
	override val fragment: QuotationManagementFragment
) : BaseRecyclerPresenter<QuotationManagementFragment, QuotationSelectionTable>() {

	override fun updateData() {
		updateSelectionsData()
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.getMainActivity()?.getQuotationFragment()?.presenter?.updateData()
	}

	private fun updateSelectionsData(callback: () -> Unit = {}) {
		QuotationSelectionTable.getMySelections { selections ->
			selections.sortedByDescending { it.orderID }.toArrayList().let { orderedData ->
				fragment.apply {
					context?.runOnUiThread {
						asyncData.isNull() isTrue {
							asyncData = orderedData
						} otherwise {
							diffAndUpdateSingleCellAdapterData<QuotationManagementAdapter>(orderedData)
						}
					}
				}
			}
			callback()
		}
	}

	override fun afterUpdateAdapterDataset(recyclerView: BaseRecyclerView) {
		fragment.updateSelectionOrderID()
	}

	private fun getCurrentAsyncData() =
		fragment.asyncData.orEmptyArray()

	private fun QuotationManagementFragment.updateSelectionOrderID() {
		recyclerView.addDragEventAndReordering(getCurrentAsyncData()) { fromPosition, toPosition ->
			val data = getCurrentAsyncData()
			if (fromPosition != null && toPosition != null) {
				// 通过权重判断简单的实现了排序效果
				val newOrderID = when (toPosition) {
					0 -> data[toPosition + 1].orderID + 0.1
					data.lastIndex -> data[toPosition - 1].orderID - 0.1
					else -> (data[toPosition - 1].orderID + data[toPosition + 1].orderID) / 2.0
				}
				QuotationSelectionTable.updateSelectionOrderIDBy(
					data[toPosition].pair, newOrderID
				) {
					// 更新完数据库后也需要同时更新一下缓存的数据, 解决用户一次更新多个缓存数据排序的情况
					fragment.asyncData?.find {
						it.baseSymbol == data[toPosition].baseSymbol
					}?.orderID = newOrderID
				}
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		// 更新数据
		updateSelectionsData()
	}
}