package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.view

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view.BigTransactionFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.view.RAMTradePercentFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view.RAMOccupyRankFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.viewPager
import java.util.ArrayList

/**
 * @date: 2018/11/2.
 * @author: yanglihai
 * @description:
 */
class QuotationViewPager(val fragment: RAMMarketDetailFragment): LinearLayout(fragment.context) {
	private val menuBar by lazy { ViewPagerMenu(context) }
	private var fragmentList = ArrayList<SubFragment>()
	private val titles = arrayListOf<String>(
		"大单交易",
		"持仓大户",
		"成交结构"
	)
	
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 605.uiPX())
		fragmentList.add(SubFragment(BigTransactionFragment(), "bigOrder"))
		fragmentList.add(SubFragment(RAMOccupyRankFragment(), "ramRank"))
		fragmentList.add(SubFragment(RAMTradePercentFragment(), "distributed"))
		
		addView(menuBar)
		view {
			layoutParams = LinearLayout.LayoutParams(matchParent, 1.uiPX())
			backgroundColor = GrayScale.lightGray
		}
		viewPager {
			id = ElementID.contentScrollview
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
				}
			}
		}
		
	}
	
}