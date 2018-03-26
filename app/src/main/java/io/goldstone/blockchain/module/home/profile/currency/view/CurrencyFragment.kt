package io.goldstone.blockchain.module.home.profile.currency.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel
import io.goldstone.blockchain.module.home.profile.currency.presenter.CurrencyPresenter

/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */

class CurrencyFragment : BaseRecyclerFragment<CurrencyPresenter, CurrencyModel>() {

  override val presenter = CurrencyPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<CurrencyModel>?) {
    recyclerView.adapter = CurrencyAdapter(asyncData.orEmptyArray()) { item, position ->

    }
  }

  override fun setSlideUpWithCellHeight() = 50.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      CurrencyModel("USD", true),
      CurrencyModel("EUR", true),
      CurrencyModel("RMB", false),
      CurrencyModel("JPY", true),
      CurrencyModel("HKD", true),
      CurrencyModel("EUR", true),
      CurrencyModel("RUB", true),
      CurrencyModel("USD", true),
      CurrencyModel("RMB", false),
      CurrencyModel("JPY", true),
      CurrencyModel("HKD", true),
      CurrencyModel("EUR", true),
      CurrencyModel("RUB", true)
    )

  }

}