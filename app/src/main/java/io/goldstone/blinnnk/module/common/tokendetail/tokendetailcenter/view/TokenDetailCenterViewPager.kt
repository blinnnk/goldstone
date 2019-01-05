package io.goldstone.blinnnk.module.common.tokendetail.tokendetailcenter.view

import android.annotation.SuppressLint
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blinnnk.common.value.FragmentTag
import io.goldstone.blinnnk.common.value.ViewPagerID
import io.goldstone.blinnnk.crypto.multichain.isEOS
import io.goldstone.blinnnk.module.common.tokendetail.tokenasset.view.TokenAssetFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.event.FilterButtonDisplayEvent
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.view.TokenInfoFragment
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.onPageChangeListener
import java.util.*


@SuppressLint("ViewConstructor")
/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenDetailCenterViewPager(
	val fragment: TokenDetailCenterFragment
) : ViewPager(fragment.context!!) {

	private var fragmentList = ArrayList<SubFragment>()
	private val tokenDetail by lazy { TokenDetailFragment() }
	private val tokenAsset by lazy { TokenAssetFragment() }
	private val tokenInfo by lazy { TokenInfoFragment() }

	init {
		id = ViewPagerID.transactions
		fragmentList.apply {
			add(SubFragment(tokenDetail, FragmentTag.tokenDetail))
			if (fragment.token?.contract.isEOS()) {
				add(SubFragment(tokenAsset, FragmentTag.tokenAsset))
			} else {
				add(SubFragment(tokenInfo, FragmentTag.tokenInfo))
			}
		}
		onPageChangeListener {
			onPageSelected {
				// 控制 `TokenDetail Fragment` 的左上角的 `Filter Icon` 的显示
				EventBus.getDefault().post(FilterButtonDisplayEvent(it == 0))
			}
		}

		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
	}

}