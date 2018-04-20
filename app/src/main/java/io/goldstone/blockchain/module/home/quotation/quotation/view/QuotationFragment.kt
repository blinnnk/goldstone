package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationFragment : BaseRecyclerFragment<QuotationPresenter, QuotationModel>() {

  private val slideHeader by lazy { QuotationSlideHeader(context!!) }

  override val presenter = QuotationPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<QuotationModel>?
  ) {
    recyclerView.adapter = QuotationAdapter(asyncData.orEmptyArray())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    wrapper.addView(slideHeader)
    asyncData = arrayListOf(
      QuotationModel("ETH", "ethereum coin", "$ 565.23", "+2.56", arrayListOf(Point("11", 10f), Point("12", 30f), Point("13", 50f), Point("14", 20f), Point("15", 70f), Point("16", 10f), Point("17", 30f), Point("18", 50f), Point("19", 20f), Point("20", 70f))),
      QuotationModel("EOS", "eos io", "$ 12.23", "+8.93", arrayListOf(Point("a", 5f), Point("b", 30f), Point("c", 50f), Point("d", 70f), Point("e", 73f), Point("a", 80f), Point("b", 88f), Point("c", 90f), Point("d", 92f), Point("e", 95f))),
      QuotationModel("TRX", "tripple lady", "$ 0.273", "-1.52", arrayListOf(Point("a", 80f), Point("b", 30f), Point("c", 50f), Point("d", 40f), Point("e", 40f), Point("a", 20f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 15f))),
      QuotationModel("QTM", "ethereum coin", "$ 565.23", "+2.56", arrayListOf(Point("a", 10f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 70f), Point("a", 10f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 70f))),
      QuotationModel("EOS", "eos io", "$ 12.23", "+8.93", arrayListOf(Point("a", 5f), Point("b", 30f), Point("c", 50f), Point("d", 70f), Point("e", 73f), Point("a", 80f), Point("b", 88f), Point("c", 90f), Point("d", 92f), Point("e", 95f))),
      QuotationModel("TRX", "tripple lady", "$ 0.273", "-1.52", arrayListOf(Point("a", 80f), Point("b", 30f), Point("c", 50f), Point("d", 40f), Point("e", 40f), Point("a", 20f), Point("b", 30f), Point("c", 50f), Point("d", 20f), Point("e", 15f)))
      )
  }

  private var isShow = false
  private val headerHeight = 50.uiPX()

  override fun observingRecyclerViewVerticalOffset(offset: Int) {
    if (offset >= headerHeight && !isShow) {
      slideHeader.onHeaderShowedStyle()
      isShow = true
    }

    if (offset < headerHeight && isShow) {
      slideHeader.onHeaderHidesStyle()
      isShow = false
    }
  }

}