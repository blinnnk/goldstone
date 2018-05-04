package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ButtonMenu
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter.MarketTokenDetailPresenter
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */

enum class MarketTokenDetailChartType(
	val code: Int, val info: String
) {
	Hour(0, "1hour"), DAY(1, "1day"), WEEK(2, "1week"), MONTH(3, "1month")
}

class MarketTokenDetailFragment : BaseFragment<MarketTokenDetailPresenter>() {

	val currencyInfo by lazy {
		arguments?.getSerializable(ArgumentKey.quotationCurrencyDetail) as? QuotationModel
	}

	val currentPriceInfo by lazy { CurrentPriceView(context!!) }

	private val menu by lazy { ButtonMenu(context!!) }
	private val chartView by lazy { MarketTokenChart(context!!) }
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
				menu.titles = arrayListOf(
					MarketTokenDetailChartType.Hour.info,
					MarketTokenDetailChartType.DAY.info,
					MarketTokenDetailChartType.WEEK.info,
					MarketTokenDetailChartType.MONTH.info
				)
				menu.getButton { button ->
					button.onClick {
						presenter.updateChartByMenu(chartView, button.id)
						menu.selected(button.id)
						button.preventDuplicateClicks()
					}
				}
				menu.selected(MarketTokenDetailChartType.Hour.code)
				chartView.into(this)
				// 默认加载小时的图标数据
				presenter.updateChartByMenu(chartView, MarketTokenDetailChartType.Hour.code)

				currentPriceInfo.apply {
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 20.uiPX()
					}
				}.into(this)

				priceHistroy.into(this)
				tokenInfo.into(this)
				tokenInfomation.into(this)

				setCurrencyInf()
			}
		}
	}

	private fun setCurrencyInf() {
		currencyInfo?.let { info ->
			GoldStoneAPI.getQuotationCurrencyInfo(info.pair) {
				context?.runOnUiThread {
					tokenInfomation.model = TokenInfomationModel(it)
					priceHistroy.model = PriceHistoryModel(it, info.quoteSymbol)
				}
			}
		}
	}
}