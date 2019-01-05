package io.goldstone.blinnnk.module.home.quotation.markettokendetail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.button.ButtonMenu
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blinnnk.module.home.quotation.markettokendetail.model.MarketTokenDetailChartType
import io.goldstone.blinnnk.module.home.quotation.markettokendetail.model.TokenInformationModel
import io.goldstone.blinnnk.module.home.quotation.markettokendetail.presenter.MarketTokenDetailPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */
class MarketTokenDetailFragment : BaseFragment<MarketTokenDetailPresenter>() {

	val currencyInfo by lazy {
		getParentFragment<MarketTokenCenterFragment>()?.currencyInfo
	}
	override val pageTitle: String = "Market Detail"
	lateinit var currentPriceInfo: CurrentPriceView
	private lateinit var menu: ButtonMenu
	private lateinit var candleChart: MarketTokenCandleChart
	private lateinit var priceHistory: PriceHistoryView
	private lateinit var tokenInfo: TokenInfoView
	private lateinit var tokenInformation: TokenInformation
	private lateinit var tokenInfoLink: TokenInfoLink
	private lateinit var tokenSocialMedia: TokenSocialMedia
	override val presenter = MarketTokenDetailPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				bottomPadding = 30.uiPX()
				topPadding = 15.uiPX()
				gravity = Gravity.CENTER_HORIZONTAL

				menu = ButtonMenu(context)
				menu.into(this)
				menu.titles = arrayListOf(
					MarketTokenDetailChartType.Hour.display,
					MarketTokenDetailChartType.DAY.display,
					MarketTokenDetailChartType.WEEK.display,
					MarketTokenDetailChartType.MONTH.display
				)
				menu.getButton { button ->
					button.onClick {
						presenter.updateChartByMenu(candleChart, button.id)
						menu.selected(button.id)
						button.preventDuplicateClicks()
					}
				}
				menu.selected(MarketTokenDetailChartType.Hour.code)

				candleChart = MarketTokenCandleChart(context)
				candleChart.into(this)
				// 默认加载小时的图标数据
				presenter.updateChartByMenu(candleChart, MarketTokenDetailChartType.Hour.code)

				currentPriceInfo = CurrentPriceView(context)
				currentPriceInfo.into(this)
				currentPriceInfo.setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}
				// 显示从上个界面带进来的值防止出现空数据
				currencyInfo?.let {
					currentPriceInfo.model = CurrentPriceModel(it)
				}

				priceHistory = PriceHistoryView(context)
				priceHistory.into(this)

				tokenInfo = TokenInfoView(context)
				tokenInfo.click {
					getParentFragment<MarketTokenCenterFragment>()
						?.getParentContainer()?.apply {
							presenter.showAllDescription(this)
						}
				}.into(this)
				tokenInformation = TokenInformation(context)
				tokenInformation.into(this)
				tokenSocialMedia = TokenSocialMedia(context) { url ->
					presenter.openSystemBrowser(url)
				}
				tokenSocialMedia.visibility = View.GONE
				tokenSocialMedia.into(this)

				tokenInfoLink = TokenInfoLink(context) { link, title ->
					presenter.showWebFragmentWithLink(link, title)
				}
				tokenInfoLink.into(this)
			}
		}
	}

	fun showCurrencyInfo(
		tokenInfoData: TokenInformationModel,
		priceModel: PriceHistoryModel
	) {
		tokenInformation.model = tokenInfoData
		priceHistory.model = priceModel
		tokenInfo.setTokenDescription(tokenInfoData.description)
		tokenInfoLink.model = tokenInfoData

		if (tokenInfoData.socialMedia.isNotEmpty()) {
			tokenSocialMedia.model = tokenInfoData
			tokenSocialMedia.visibility = View.VISIBLE
		}
	}

}