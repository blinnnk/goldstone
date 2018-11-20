package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter.TokenSearchPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 27/03/2018 11:22 AM
 * @author KaySaith
 */
class TokenSearchFragment : BaseRecyclerFragment<TokenSearchPresenter, DefaultTokenTable>() {

	override val pageTitle: String = "Token Search"
	override val presenter = TokenSearchPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView, asyncData: ArrayList<DefaultTokenTable>?
	) {
		recyclerView.adapter = TokenSearchAdapter(asyncData.orEmptyArray()) { default, switch ->
			switch.onClick {
				switch.isClickable = false
				// 更新内存数据, 防止复用 `UI` 错乱
				asyncData?.find { data ->
					data.contract.equals(default.contract, true) }?.isUsed = switch.isChecked
				presenter.setMyTokenStatus(default, switch.isChecked) {
					switch.isClickable = true
				}
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<TokenManagementFragment> {
			presenter.popFragmentFrom<TokenSearchFragment>()
			showSearchInput(false) {}
		}
	}
}