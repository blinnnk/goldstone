package io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementAdapter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementPresenter(
  override val fragment: QuotationManagementFragment
) : BaseRecyclerPresenter<QuotationManagementFragment, QuotationSelectionTable>() {

  override fun updateData() {
    QuotationSelectionTable.getMySelections {
      it.sortedByDescending { it.orderID }.toArrayList().let { orderedData ->
        fragment.apply {
          asyncData.isNull() isTrue {
            asyncData = orderedData
          } otherwise {
            diffAndUpdateSingleCellAdapterData<QuotationManagementAdapter>(orderedData)
          }
        }
      }
    }
  }

  override fun afterUpdateAdapterDataset(recyclerView: BaseRecyclerView) {
    fragment.updateSelectionOrderID()
  }

  private fun QuotationManagementFragment.updateSelectionOrderID() {
    fragment.asyncData?.let {
      recyclerView.addDragEventAndReordering(it) { fromPosition, toPosition ->
        if (fromPosition != null && toPosition != null) {
          // 通过权重判断简单的实现了排序效果
          val newOrderID = when (toPosition) {
            0 -> it[toPosition + 1].orderID + 0.1
            it.lastIndex -> it[toPosition - 1].orderID - 0.1
            else -> (it[toPosition - 1].orderID + it[toPosition + 1].orderID) / 2.0
          }
          QuotationSelectionTable.updateSelectionOrderIDBy(it[toPosition].pair, newOrderID)
        }
      }
    }
  }

	override fun updateParentContentLayoutHeight(dataCount: Int?, cellHeight: Int, maxHeight: Int) {
		super.updateParentContentLayoutHeight(
			if(fragment.asyncData.isNullOrEmpty()) null else fragment.asyncData?.size,
			cellHeight,
			maxHeight
		)
	}

  override fun onFragmentShowFromHidden() {
    // 更新数据
    updateData()
    updateParentContentLayoutHeight(if(fragment.asyncData.isNullOrEmpty()) null else fragment.asyncData?.size)
  }

}