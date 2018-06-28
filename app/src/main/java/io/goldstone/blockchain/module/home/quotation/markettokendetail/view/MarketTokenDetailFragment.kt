package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ButtonMenu
import io.goldstone.blockchain.common.component.ContentScrollOverlayView
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.MarketTokenDetailChartType
import io.goldstone.blockchain.module.home.quotation.markettokendetail.presenter.MarketTokenDetailPresenter
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 6:52 AM
 * @author KaySaith
 */
class MarketTokenDetailFragment : BaseFragment<MarketTokenDetailPresenter>() {
	
	val currencyInfo by lazy {
		arguments?.getSerializable(ArgumentKey.quotationCurrencyDetail) as? QuotationModel
	}
	val currentPriceInfo by lazy { CurrentPriceView(context!!) }
	private val menu by lazy { ButtonMenu(context!!) }
	private val chartView by lazy { MarketTokenChart(context!!) }
	private val priceHistroy by lazy { PriceHistoryView(context!!) }
	private val tokenInfo by lazy { TokenInfoView(context!!) }
	private val tokenInformation by lazy { TokenInformation(context!!) }
	private val tokenInfoLink by lazy {
		TokenInfoLink(context!!) { link, title ->
			presenter.showWebfragumentWithLink(link, title, currencyInfo?.pairDisplay.orEmpty())
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
						presenter.updateChartByMenu(chartView, button.id)
						menu.selected(button.id)
						button.preventDuplicateClicks()
					}
				}
				menu.selected(MarketTokenDetailChartType.Hour.code)
				chartView.into(this)
				// 默认加载小时的图标数据
				presenter.updateChartByMenu(
					chartView, MarketTokenDetailChartType.Hour.code
				)
				
				currentPriceInfo.apply {
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 20.uiPX()
					}
				}.into(this)
				// 显示从上个界面带进来的值防止出现空数据
				currencyInfo?.let {
					currentPriceInfo.model = CurrentPriceModel(it)
				}
				
				priceHistroy.into(this)
				tokenInfo
					.click {
						getParentContainer()?.let {
							presenter.showAllDescription(it)
						}
					}
					.into(this)
				tokenInformation.into(this)
				tokenSocialMedia.into(this)
				tokenInfoLink.into(this)
				presenter.setCurrencyInfo(
					currencyInfo,
					tokenInformation,
					priceHistroy,
					tokenInfo,
					tokenInfoLink,
					tokenSocialMedia
				)
			}.lparams {
				width = ScreenSize.widthWithPadding
				height = matchParent
				leftMargin = PaddingSize.device
			}
		}
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		val overlay = getParentContainer()
			?.findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
		if (overlay.isNull()) {
			super.setBaseBackEvent(activity, parent)
			// 恢复回退事件
			activity?.getHomeFragment()
				?.findChildFragmentByTag<QuotationFragment>(FragmentTag.quotation)
				?.apply {
					updateBackEvent()
				}
		} else {
			// 如果存在悬浮层销毁悬浮层
			overlay?.remove()
		}
	}
}