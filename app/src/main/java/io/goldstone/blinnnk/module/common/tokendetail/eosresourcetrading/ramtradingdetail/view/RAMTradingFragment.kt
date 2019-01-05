package io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.ramtradingdetail.view

import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.ramtradingdetail.presenter.RAMTradingPresenter


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class RAMTradingFragment : BaseTradingFragment() {
	override val presenter: BaseTradingPresenter = RAMTradingPresenter(this)
	override val tradingType: TradingType = TradingType.RAM
}