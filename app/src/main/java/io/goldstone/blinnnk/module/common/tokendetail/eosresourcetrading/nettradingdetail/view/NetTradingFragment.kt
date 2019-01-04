package io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.nettradingdetail.view

import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.nettradingdetail.presenter.NETTradingPresenter


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class NETTradingFragment : BaseTradingFragment() {
	override val presenter: BaseTradingPresenter = NETTradingPresenter(this)
	override val tradingType: TradingType = TradingType.NET
}