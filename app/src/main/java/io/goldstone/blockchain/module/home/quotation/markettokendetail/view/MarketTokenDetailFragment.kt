package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ButtonMenu
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter.MarketTokenDetailPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */

enum class MarketTokenDetailChartType(val code: Int) {
  Hour(0), DAY(1), WEEK(2), MONTH(3)
}

class MarketTokenDetailFragment : BaseFragment<MarketTokenDetailPresenter>() {

  private val menu by lazy { ButtonMenu(context!!) }
  private val chartView by lazy { MarketTokenChart(context!!) }
  private val currentPriceInfo by lazy { CurrentPriceView(context!!) }
  private val priceHistroy by lazy { PriceHistoryView(context!!) }
  private val tokenInfo by lazy { TokenInfoView(context!!) }
  private val tokenInfomation by lazy { TokenInfomation(context!!) }

  override val presenter = MarketTokenDetailPresenter(this)
  override fun AnkoContext<Fragment>.initView() {
    scrollView {
      verticalLayout {

        lparams {
          width = ScreenSize.widthWithPadding
          height = matchParent
          leftMargin = PaddingSize.device
        }

        menu.apply {
          setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
        }.into(this)
        menu.titles = arrayListOf("HOUR", "DAY", "WEEK", "MONTH")
        menu.getButton { button ->
          button.onClick {
            presenter.updateChartByMenu(chartView, button.id)
            menu.selected(button.id)
            button.preventDuplicateClicks()
          }
        }
        menu.selected(MarketTokenDetailChartType.Hour.code)

        chartView.into(this)
        chartView.chartData = arrayListOf(
          Point("11", 10f),
          Point("12", 30f),
          Point("13", 50f),
          Point("14", 20f),
          Point("15", 70f),
          Point("16", 10f),
          Point("17", 30f),
          Point("18", 50f),
          Point("19", 20f),
          Point("20", 70f)
        )

        currentPriceInfo.apply {
          setMargins<LinearLayout.LayoutParams> {
            topMargin = 20.uiPX()
          }
        }.into(this)
        currentPriceInfo.model = CurrentPriceModel(15.872, "USDT", "+13.56%")

        priceHistroy.into(this)
        tokenInfo.into(this)
        tokenInfomation.into(this)

        tokenInfomation.model = TokenInfomationModel("5", "128,189,290,238", "$ 289,321,289,291")
      }
    }
  }


}