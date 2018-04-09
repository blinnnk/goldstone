package io.goldstone.blockchain.module.home.profile.currency.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment

/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */

class CurrencyPresenter(
  override val fragment: CurrencyFragment
  ) : BaseRecyclerPresenter<CurrencyFragment, CurrencyModel>() {

}