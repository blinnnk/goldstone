package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
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

    asyncData = arrayListOf(QuotationModel(), QuotationModel(), QuotationModel(), QuotationModel())

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