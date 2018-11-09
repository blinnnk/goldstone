package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.view

import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view.BigTransactionFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.view.RAMTradePercentFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view.RAMOccupyRankFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.view.RAMStatisticsFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.viewPager
import java.util.*

/**
 * @date: 2018/11/2.
 * @author: yanglihai
 * @description:
 */
class QuotationViewPager(val fragment: RAMMarketDetailFragment): LinearLayout(fragment.context) {
	private val menuBar by lazy {
		ViewPagerMenu(context, (ScreenSize.Width - 40.uiPX()) / 4).apply {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 40.uiPX(), 45.uiPX())
			setMargins<LinearLayout.LayoutParams> { leftMargin = 20.uiPX() }
			setBorderLineColor(Spectrum.green)
			backgroundColor = Spectrum.white
			elevation = 0f
		}
	}
	private var fragmentList = ArrayList<SubFragment>()
	private val titles = arrayListOf(
		EOSRAMExchangeText.bigTransactions,
		EOSRAMExchangeText.occupyBig,
		EOSRAMExchangeText.ramStatistics,
		EOSRAMExchangeText.tradeStruct
	)
	
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 605.uiPX())
		fragmentList.add(SubFragment(BigTransactionFragment(), "bigOrder"))
		fragmentList.add(SubFragment(RAMOccupyRankFragment(), "ramRank"))
		fragmentList.add(SubFragment(RAMStatisticsFragment(), "ramStatistics"))
		fragmentList.add(SubFragment(RAMTradePercentFragment(), "distributed"))
		
		addView(menuBar)
		view {
			layoutParams = LinearLayout.LayoutParams(matchParent, 1)
			setMargins<LinearLayout.LayoutParams> {
				rightMargin = 20.uiPX()
				leftMargin = 20.uiPX()
			}
			backgroundColor = GrayScale.lightGray
		}
		viewPager {
			id = ViewPagerID.ramMarket
			offscreenPageLimit = 4
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
		}.apply {
			// `MenuBar` 点击选中动画和内容更换
			menuBar.setMemnuTitles(titles) { button, id ->
				button.onClick {
					currentItem = id
					menuBar.moveUnderLine(menuBar.getUnitWidth() * currentItem)
					button.preventDuplicateClicks()
				}
			}
			// `MenuBar` 滑动选中动画
			onPageChangeListener {
				onPageScrolled { position, percent, _ ->
					menuBar.moveUnderLine(menuBar.getUnitWidth() * (percent + position))
					for (index in 0 .. menuBar.childCount) {
						(menuBar.getChildAt(index) as? TextView)?.apply {
							textColor = if (index == position) Spectrum.green else GrayScale.midGray
						}
					}
				}
			}
		}
		
	}
	
}