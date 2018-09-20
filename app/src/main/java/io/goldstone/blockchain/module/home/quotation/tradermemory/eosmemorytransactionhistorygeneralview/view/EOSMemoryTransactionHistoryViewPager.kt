package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorygeneralview.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view.EOSMemoryTransactionHistoryListFragment
import java.util.*

@SuppressLint("ViewConstructor")
/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class EOSMemoryTransactionHistoryViewPager(val fragment: Fragment) : ViewPager(fragment.context!!) {

	private var fragmentList = ArrayList<SubFragment>()
	private val traderMemorySalesRecord by lazy {
		EOSMemoryTransactionHistoryListFragment(true)
	}
	private val traderMemoryLargeSingleRecord by lazy {
		EOSMemoryTransactionHistoryListFragment(false)
	}

	init {
		id = ViewPagerID.transactions
		fragmentList.apply {
			add(SubFragment(traderMemorySalesRecord, FragmentTag.traderMemorySalesRecord))
			add(SubFragment(traderMemoryLargeSingleRecord, FragmentTag.traderMemoryLargeSingleRecord))
		}
		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
	}

}