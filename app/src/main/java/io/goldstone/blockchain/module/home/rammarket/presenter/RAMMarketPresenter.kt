package io.goldstone.blockchain.module.home.rammarket.presenter

import android.os.Bundle
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view.RAMTransactionSearchFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description:
 */
class RAMMarketPresenter(override val fragment: RAMMarketOverlayFragment)
	: BaseOverlayPresenter<RAMMarketOverlayFragment>() {

	fun showTransactionHistoryFragment(account: String? = null) {
		showTargetFragment<RAMTransactionSearchFragment>(Bundle().apply { putString("account", account) })
		fragment.getSearchContent().apply {
			fragment.showSearchInput {
				popFragmentFrom<RAMTransactionSearchFragment>()
			}
		}
	}
	
}