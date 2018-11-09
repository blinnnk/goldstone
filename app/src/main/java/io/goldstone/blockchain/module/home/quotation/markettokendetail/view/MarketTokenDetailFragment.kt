package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter.MarketTokenDetailPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */
class MarketTokenDetailFragment : BaseFragment<MarketTokenDetailPresenter>() {

	val currencyInfo by lazy {
		getParentFragment<MarketTokenCenterFragment>()?.currencyInfo
	}
	override val pageTitle: String = "Market Detail"
	val currentPriceInfo by lazy { CurrentPriceView(context!!) }
	private val menu by lazy { ButtonMenu(context!!) }
	private val candleChart by lazy { MarketTokenCandleChart(context!!) }
	private val priceHistory by lazy { PriceHistoryView(context!!) }
	private val tokenInfo by lazy { TokenInfoView(context!!) }
	private val tokenInformation by lazy { TokenInformation(context!!) }
	private val tokenInfoLink by lazy {
		TokenInfoLink(context!!) { link, title ->
			presenter.showWebFragmentWithLink(link, title)
		}
	}
	private val tokenSocialMedia by lazy {
		TokenSocialMedia(context!!) { url ->
			presenter.openSystemBrowser(url)
		}
	}
	override val presenter = MarketTokenDetailPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				menu.apply {
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 15.uiPX()
					}
				}.into(this)
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
				candleChart.into(this)
				// 默认加载小时的图标数据
				presenter.updateChartByMenu(candleChart, MarketTokenDetailChartType.Hour.code)

				currentPriceInfo.apply {
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 20.uiPX()
					}
				}.into(this)
				// 显示从上个界面带进来的值防止出现空数据
				currencyInfo?.let {
					currentPriceInfo.model = CurrentPriceModel(it)
				}

				priceHistory.into(this)
				tokenInfo
					.click {
						getParentFragment<MarketTokenCenterFragment>()
							?.getParentContainer()?.apply {
								presenter.showAllDescription(this)
							}
					}
					.into(this)
				tokenInformation.into(this)
				tokenSocialMedia.into(this)
				tokenInfoLink.into(this)
				presenter.setCurrencyInfo(
					currencyInfo,
					tokenInformation,
					priceHistory,
					tokenInfo,
					tokenInfoLink,
					tokenSocialMedia
				)
			}.lparams {
				width = matchParent
				height = matchParent
			}
		}
	}

	companion object {
		fun removeContentOverlayOrElse(
			fragment: MarketTokenCenterFragment,
			callback: () -> Unit
		) {
			fragment.getParentContainer()
				?.findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
				.apply {
					if (isNull()) callback()
					else this?.remove()
				}
		}
	}
}