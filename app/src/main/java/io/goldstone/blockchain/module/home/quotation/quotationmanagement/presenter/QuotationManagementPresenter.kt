package io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter

import com.blinnnk.extension.*
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

  override fun afterUpdateAdapterDataset() {
    fragment.recyclerView.addDragEventAndReordering(fragment.asyncData.orEmptyArray()) { fromPosition, toPosition ->
      if (fromPosition != null && toPosition != null) {
        updateSelectionsOrderID(fromPosition, toPosition) {
          updateSelectionsOrderID(toPosition, fromPosition)
        }
      }
    }
  }

  private fun updateSelectionsOrderID(firstID: Int, secondID: Int, callback: () -> Unit = {}) {
    fragment.asyncData?.let {
      QuotationSelectionTable.updateSelectionOrderIDBy(it[firstID].pair, secondID) {
        QuotationSelectionTable.updateSelectionOrderIDBy(it[secondID].pair, firstID) {
          callback()
        }
      }
    }
  }

  override fun onFragmentShowFromHidden() {
    updateData()
  }

}