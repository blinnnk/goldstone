package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.findChildFragmentByTag
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ButtonMenu
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
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
	override val presenter = MarketTokenDetailPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
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
				
				priceHistroy.into(this)
				tokenInfo.into(this)
				tokenInformation.into(this)
				
				presenter.setCurrencyInf(
					currencyInfo, tokenInformation, priceHistroy, tokenInfo
				)
			}.lparams {
				width = ScreenSize.widthWithPadding
				height = matchParent
				leftMargin = PaddingSize.device
			}
		}
	}
	
	override fun setBackEvent(
		activity: MainActivity,
		parent: Fragment?
	) {
		super.setBackEvent(activity, parent)
		// 恢复回退事件
		activity.getHomeFragment()?.findChildFragmentByTag<QuotationFragment>(FragmentTag.quotation)
			?.apply {
				updateBackEvent()
			}
	}
}