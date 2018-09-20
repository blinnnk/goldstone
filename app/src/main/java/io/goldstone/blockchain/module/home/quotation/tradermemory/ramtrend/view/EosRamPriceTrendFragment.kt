package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view

import android.support.v4.app.Fragment
import android.text.format.DateUtils
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenCandleChart
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.presenter.EosRamPriceTrendPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description:
 */
class EosRamPriceTrendFragment : BaseFragment<EosRamPriceTrendPresenter>() {
	
	private val candleChart by lazy { MarketTokenCandleChart(context!!) }
	
	override val presenter: EosRamPriceTrendPresenter = EosRamPriceTrendPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			addView(candleChart)
			presenter.updateCandleData(candleChart, DateUtils.FORMAT_SHOW_TIME)
		}
	}
}