package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.viewPager
import java.util.ArrayList

/**
 * @date: 2018/11/2.
 * @author: yanglihai
 * @description:
 */
class QuotationViewPager(val fragment: RAMMarketDetailFragment): LinearLayout(fragment.context) {
	private val titleTabLayout by lazy { ViewPagerMenu(context) }
	private var fragmentList = ArrayList<SubFragment>()
	
	init {
	  addView(titleTabLayout)
		view {
			layoutParams = LinearLayout.LayoutParams(matchParent, 1.uiPX())
			backgroundColor = GrayScale.lightGray
		}
		viewPager {
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
		}
		
	}
	
}