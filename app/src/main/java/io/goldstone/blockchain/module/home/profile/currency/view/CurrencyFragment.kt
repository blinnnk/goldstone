package io.goldstone.blockchain.module.home.profile.currency.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel
import io.goldstone.blockchain.module.home.profile.currency.presenter.CurrencyPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */

class CurrencyFragment : BaseRecyclerFragment<CurrencyPresenter, CurrencyModel>() {

  override val presenter = CurrencyPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<CurrencyModel>?) {
    recyclerView.adapter = CurrencyAdapter(asyncData.orEmptyArray()) { item, _ ->
      item.apply {
        onClick {
          presenter.setCurrencyAlert(model.symbol) {
            setSwitchStatusBy(this)
          }
          preventDuplicateClicks()
        }
      }
    }
  }

  override fun setSlideUpWithCellHeight() = 50.uiPX()

}