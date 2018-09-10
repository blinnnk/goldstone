package io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.view.TokenAssetFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import java.util.*


@SuppressLint("ViewConstructor")
/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenDetailCenterViewPager(val fragment: Fragment) : ViewPager(fragment.context!!) {

	private var fragmentList = ArrayList<SubFragment>()
	private val tokenDetail by lazy { TokenDetailFragment() }
	private val tokenAsset by lazy { TokenAssetFragment() }

	init {
		id = ViewPagerID.transactions
		fragmentList.apply {
			add(SubFragment(tokenDetail, FragmentTag.tokenDetail))
			add(SubFragment(tokenAsset, FragmentTag.tokenAsset))
		}
		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
	}

}