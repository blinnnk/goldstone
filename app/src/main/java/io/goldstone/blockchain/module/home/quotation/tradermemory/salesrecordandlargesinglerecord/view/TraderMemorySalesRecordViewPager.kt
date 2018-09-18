package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecord.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import java.util.*

@SuppressLint("ViewConstructor")
/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemorySalesRecordViewPager(val fragment: Fragment) : ViewPager(fragment.context!!) {

	private var fragmentList = ArrayList<SubFragment>()
	private val marketDetail by lazy {
		MarketTokenDetailFragment()
	}

	init {
		id = ViewPagerID.transactions
		fragmentList.apply {
			add(SubFragment(marketDetail, FragmentTag.tokenMarketDetail))
		}
		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
	}

}