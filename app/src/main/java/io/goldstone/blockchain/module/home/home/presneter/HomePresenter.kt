package io.goldstone.blockchain.module.home.home.presneter

import android.support.v4.app.Fragment
import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.home.home.view.HomeFragment
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment

/**
 * @date 23/03/2018 12:59 PM
 * @author KaySaith
 */
class HomePresenter(
	override val fragment: HomeFragment
) : BasePresenter<HomeFragment>() {

	fun showWalletDetailFragment() {
		fragment.selectWalletDetail {
			fragment.showOrAddFragment<WalletDetailFragment>(FragmentTag.walletDetail)
		}
	}

	fun showProfileFragment() {
		fragment.setProfile {
			fragment.showOrAddFragment<ProfileFragment>(FragmentTag.profile)
		}
	}

	fun showQuotationFragment() {
		fragment.selectQuotation {
			fragment.showOrAddFragment<QuotationFragment>(FragmentTag.quotation)
		}
	}

	override fun onFragmentResume() {
		super.onFragmentResume()
		// `App` 频繁的检测更新所有需要使用的数据
		fragment.context?.let {
			object : SilentUpdater() {}.star(it)
		}
	}

	private inline fun <reified T : Fragment> Fragment.showOrAddFragment(fragmentTag: String) {
		// 隐藏可见的 `Fragment`
		childFragmentManager.fragments.forEach { hideChildFragment(it) }
		// 加载目标 `Fragment`
		childFragmentManager.findFragmentByTag(fragmentTag).let { it ->
			it.isNull() isTrue {
				addFragmentAndSetArgument<T>(ContainerID.home, fragmentTag)
			} otherwise {
				it?.let { showChildFragment(it) }
			}
		}
	}
}