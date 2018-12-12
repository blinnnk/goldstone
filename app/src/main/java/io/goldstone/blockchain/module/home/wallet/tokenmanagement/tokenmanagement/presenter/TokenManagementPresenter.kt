package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.presenter

import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment

/**
 * @date 25/03/2018 2:46 AM
 * @author KaySaith
 */
class TokenManagementPresenter(
	override val fragment: TokenManagementFragment
) : BaseOverlayPresenter<TokenManagementFragment>() {

	fun showTokenManagementFragment() {
		fragment.apply {
			addFragmentAndSetArgument<TokenManagementListFragment>(ContainerID.content)
			showSearchButton(true) {
				showTokenSearchFragment()
				showSearchInput(
					true,
					cancelEvent = { popFragmentFrom<TokenSearchFragment>() },
					enterKeyEvent = {}
				)
			}
		}
	}

	private fun TokenManagementFragment.showTokenSearchFragment() {
		childFragmentManager.fragments.apply {
			if (last() is TokenManagementListFragment) hideChildFragment(last())
			addFragmentAndSetArgument<TokenSearchFragment>(ContainerID.content)
		}
	}
}