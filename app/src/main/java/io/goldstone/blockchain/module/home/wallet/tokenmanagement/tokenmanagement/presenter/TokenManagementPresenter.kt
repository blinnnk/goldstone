package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.presenter

import com.blinnnk.extension.findChildFragmentByTag
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment

/**
 * @date 25/03/2018 2:46 AM
 * @author KaySaith
 */

class TokenManagementPresenter(
	override val fragment: TokenManagementFragment
) : BaseOverlayPresenter<TokenManagementFragment>() {

	override fun onFragmentDestroy() {
		fragment.getMainActivity()?.apply {
			supportFragmentManager.findFragmentByTag(FragmentTag.home)
				.findChildFragmentByTag<WalletDetailFragment>(FragmentTag.walletDetail)?.apply {
					presenter.updateAllTokensInWallet()
				}
		}
	}

	fun showTokenManagementFragment() {
		fragment.apply {

			addFragmentAndSetArgument<TokenManagementListFragment>(ContainerID.content) {
				// Send Arguments
			}

			overlayView.header.apply {
				showSearchButton(true) {
					showTokenSearchFragment()
					showSearchInput {
						popFragmentFrom<TokenSearchFragment>()
						activity?.apply { SoftKeyboard.hide(this) }
					}
				}
			}
		}
	}

	private fun TokenManagementFragment.showTokenSearchFragment() {
		childFragmentManager.fragments.apply {
			if (last() is TokenManagementListFragment) hideChildFragment(last())
			addFragmentAndSetArgument<TokenSearchFragment>(ContainerID.content) {
				// Send Arguments
			}
		}
	}

}