package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecord.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.MarketTokenDetailFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.largesinglerecord.view.TraderMemoryLargeSingleRecordFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.view.TraderMemorySalesRecordFragment
import java.util.*

@SuppressLint("ViewConstructor")
/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemorySalesRecordAndLargeSingleRecordViewPager(val fragment: Fragment) : ViewPager(fragment.context!!) {

	private var fragmentList = ArrayList<SubFragment>()
	private val traderMemorySalesRecord by lazy {
		TraderMemorySalesRecordFragment()
	}
	private val traderMemoryLargeSingleRecord by lazy {
		TraderMemoryLargeSingleRecordFragment()
	}

	init {
		id = ViewPagerID.transactions
		fragmentList.apply {
			add(SubFragment(traderMemorySalesRecord, "traderMemorySalesRecord"))
			add(SubFragment(traderMemoryLargeSingleRecord, "traderMemoryLargeSingleRecord"))
		}
		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
	}

}