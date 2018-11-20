package io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementAdapter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */
class QuotationManagementPresenter(
	override val fragment: QuotationManagementFragment
) : BaseRecyclerPresenter<QuotationManagementFragment, QuotationSelectionTable>() {

	override fun updateData() {
		GlobalScope.launch(Dispatchers.Default) {
			val selections = QuotationSelectionTable.dao.getAll()
			selections.sortedByDescending { it.orderID }.toArrayList().let { orderedData ->
				launchUI {
					if (fragment.asyncData.isNull()) fragment.asyncData = orderedData
					else diffAndUpdateSingleCellAdapterData<QuotationManagementAdapter>(orderedData)
				}
			}
		}
	}

	override fun afterUpdateAdapterDataSet(recyclerView: BaseRecyclerView) {
		fragment.updateSelectionOrderID()
	}

	fun updateQuotationDataChanged(callback: () -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			val turnOffData = fragment.asyncData?.filter { !it.isSelecting }
			if (!turnOffData.isNullOrEmpty()) {
				QuotationSelectionTable.dao.deleteAll(turnOffData)
				callback()
			} else callback()
		}
	}


	private fun QuotationManagementFragment.updateSelectionOrderID() {
		val data = fragment.asyncData.orEmptyArray()
		recyclerView.addDragEventAndReordering(data) { _, toPosition ->
			// 通过权重判断简单的实现了排序效果
			val newOrderID = when (toPosition) {
				0 -> data[toPosition + 1].orderID + 0.1
				data.lastIndex -> data[toPosition - 1].orderID - 0.1
				else -> (data[toPosition - 1].orderID + data[toPosition + 1].orderID) / 2.0
			}
			QuotationSelectionTable.updateSelectionOrderIDBy(data[toPosition].pair, newOrderID) {
				// 更新完数据库后也需要同时更新一下缓存的数据, 解决用户一次更新多个缓存数据排序的情况
				fragment.asyncData?.find {
					it.baseSymbol == data[toPosition].baseSymbol
				}?.orderID = newOrderID
			}
		}
	}
}