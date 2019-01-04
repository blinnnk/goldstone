package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.view

import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.presenter.CPUTradingPresenter


/**
 * @author KaySaith
 * @date  2018/09/18
 */
class CPUTradingFragment : BaseTradingFragment() {
	override val presenter: BaseTradingPresenter = CPUTradingPresenter(this)
	override val tradingType: TradingType = TradingType.CPU
}