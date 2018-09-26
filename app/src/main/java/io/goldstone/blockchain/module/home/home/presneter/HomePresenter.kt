package io.goldstone.blockchain.module.home.home.presneter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.home.view.HomeFragment
import io.goldstone.blockchain.module.home.profile.profile.view.ProfileFragment
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.doAsync

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

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		// `App` 启动后检查本地的 `DefaultToken` 是否有没有 `Name` 的部分, 在后台静默更新
		updateUnknownDefaultToken()
	}

	private fun updateUnknownDefaultToken() {
		doAsync {
			GoldStoneDataBase.database.defaultTokenDao().getAllTokens().filter { ChainID(it.chainID).isETHMain() && it.name.isEmpty() }.forEach {
				it.updateTokenNameFromChain()
			}
		}
	}

	private inline fun <reified T : Fragment> Fragment.showOrAddFragment(
		fragmentTag: String,
		setArgument: Bundle.() -> Unit = {}
	) {
		// 隐藏可见的 `Fragment`
		childFragmentManager.fragments.forEach { hideChildFragment(it) }
		// 加载目标 `Fragment`
		childFragmentManager.findFragmentByTag(fragmentTag).let { it ->
			it.isNull() isTrue {
				addFragmentAndSetArgument<T>(ContainerID.home, fragmentTag) {
					setArgument(this)
				}
			} otherwise {
				it?.let { showChildFragment(it) }
			}
		}
	}
}