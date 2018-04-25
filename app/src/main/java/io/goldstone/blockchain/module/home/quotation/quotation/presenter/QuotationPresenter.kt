package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import com.blinnnk.extension.addFragmentAndSetArguments
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
  override val fragment: QuotationFragment
  ) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

  override fun updateData() {
    fragment.asyncData = arrayListOf(
      QuotationModel("ETH", "ethereum coin", "$ 565.23", "+2.56", arrayListOf(Point("11", 10f), Point("12", 30f), Point("13", 50f), Point("14", 20f), Point("15", 70f), Point("16", 10f), Point("17", 30f), Point("18", 50f), Point("19", 20f), Point("20", 70f)), "Bitffnex"),
      QuotationModel("EOS", "eos io", "$ 12.23", "+8.93", arrayListOf(Point("a", 5f), Point("b", 30f), Point("c", 50f), Point("d", 70f), Point("e", 73f), Point("a", 80f), Point("b", 88f), Point("c", 90f), Point("d", 92f), Point("e", 95f)), "Bibox"),
      QuotationModel("TRX", "tripple lady", "$ 0.273", "-1.52", arrayListOf(Point("a", 80f), Point("b", 30f), Point("c", 50f), Point("d", 40f), Point("e", 40f), Point("a", 20f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 15f)), "Huobi Pro"),
      QuotationModel("QTM", "ethereum coin", "$ 565.23", "+2.56", arrayListOf(Point("a", 10f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 70f), Point("a", 10f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 70f)), "Liqui"),
      QuotationModel("EOS", "eos io", "$ 12.23", "+8.93", arrayListOf(Point("a", 5f), Point("b", 30f), Point("c", 50f), Point("d", 70f), Point("e", 73f), Point("a", 80f), Point("b", 88f), Point("c", 90f), Point("d", 92f), Point("e", 95f)), "Bitffnex"),
      QuotationModel("TRX", "tripple lady", "$ 0.273", "-1.52", arrayListOf(Point("a", 80f), Point("b", 30f), Point("c", 50f), Point("d", 40f), Point("e", 40f), Point("a", 20f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 15f)), "Huobi Pro")
    )
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