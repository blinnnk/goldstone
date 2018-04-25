package io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter

import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenChart
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */

class MarketTokenDetailPresenter(
  override val fragment: MarketTokenDetailFragment
) : BasePresenter<MarketTokenDetailFragment>() {
  override fun onFragmentViewCreated() {
    super.onFragmentViewCreated()
    fragment.getParentFragment<QuotationOverlayFragment>()?.apply {
      overlayView.contentLayout.updateHeightAnimation(context?.getRealScreenHeight().orZero())
    }
  }

  fun updateChartByMenu(chartView: MarketTokenChart, buttonID: Int) {
    chartView.chartData = when (buttonID) {
      MarketTokenDetailChartType.WEEK.code -> {
        arrayListOf(
          Point("88", 30f),
          Point("12", 30f),
          Point("13", 50f),
          Point("14", 20f),
          Point("15", 90f),
          Point("16", 10f),
          Point("17", 30f),
          Point("18", 10f),
          Point("19", 20f),
          Point("20", 70f)
        )
      }

      MarketTokenDetailChartType.DAY.code -> {
        arrayListOf(
          Point("88", 90f),
          Point("12", 10f),
          Point("13", 50f),
          Point("14", 20f),
          Point("15", 70f),
          Point("16", 10f),
          Point("17", 30f),
          Point("18", 50f),
          Point("19", 20f),
          Point("20", 90f)
        )
      }

      MarketTokenDetailChartType.MONTH.code -> {
        arrayListOf(
          Point("88", 30f),
          Point("12", 30f),
          Point("13", 50f),
          Point("14", 20f),
          Point("15", 70f),
          Point("16", 30f),
          Point("17", 30f),
          Point("18", 90f),
          Point("19", 20f),
          Point("20", 60f)
        )
      }

      else -> {
        arrayListOf(
          Point("88", 20f),
          Point("12", 30f),
          Point("13", 80f),
          Point("14", 20f),
          Point("15", 70f),
          Point("16", 10f),
          Point("17", 10f),
          Point("18", 50f),
          Point("19", 20f),
          Point("20", 50f)
        )
      }
    }
  }
}