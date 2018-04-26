package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import com.blinnnk.extension.*
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationAdapter
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
  override val fragment: QuotationFragment
  ) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

  override fun updateData() {
    QuotationSelectionTable.getMySelections { selections ->
      selections
        .map {
          QuotationModel(it, "$ 565.23", "+2.56", arrayListOf(Point("11", 10f), Point("12", 30f), Point("13", 50f), Point("14", 20f), Point("15", 70f), Point("16", 10f), Point("17", 30f), Point("18", 50f), Point("19", 20f), Point("20", 70f)))
        }
        .sortedByDescending { it.orderID }
        .toArrayList()
        .let {
          System.out.println("___+${it.map { it.orderID }}")
          fragment.asyncData.isNull() isTrue {
            fragment.asyncData = it
          } otherwise {
            diffAndUpdateAdapterData<QuotationAdapter>(it)
          }
        }
    }
  }

  fun showQuotationManagement() {
    fragment.activity?.addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
      putString(ArgumentKey.quotationOverlayTitle, QuotationText.management)
    }
  }

  fun showMarketTokenDetailFragment(symbol: String) {
    fragment.activity?.addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
      putString(ArgumentKey.quotationOverlayTitle, symbol)
    }
  }

}