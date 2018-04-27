package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import android.text.format.DateUtils
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
import org.json.JSONArray

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
  override val fragment: QuotationFragment
) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

  override fun updateData() {
    QuotationSelectionTable.getMySelections { selections ->
      selections.map {
        QuotationModel(it, "$ 565.23", "+2.56", convertDataToChartData(it.lineChart))
      }.sortedByDescending {
        it.orderID
      }.toArrayList().let {
        fragment.asyncData.isNull() isTrue {
          fragment.asyncData = it
        } otherwise {
          if (fragment.asyncData.orEmptyArray().isEmpty()) fragment.removeEmptyView()
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

  private fun convertDataToChartData(data: String): ArrayList<Point> {
    val jsonarray = JSONArray(data)
    (0 until jsonarray.length()).map {
      val timeStamp = jsonarray.getJSONObject(it)["time"].toString().toLong()
      val date = DateUtils.formatDateTime(fragment.context, timeStamp, DateUtils.FORMAT_NO_YEAR)
      Point(date, jsonarray.getJSONObject(it)["price"].toString().toFloat())
    }.reversed().let {
      return it.toArrayList()
    }
  }

}