package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view

import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.TokenManagementText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter.TokenSearchPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 11:22 AM
 * @author KaySaith
 */
class TokenSearchFragment : BaseRecyclerFragment<TokenSearchPresenter, DefaultTokenTable>() {
	
	override val presenter = TokenSearchPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView, asyncData: ArrayList<DefaultTokenTable>?
	) {
		recyclerView.adapter = TokenSearchAdapter(asyncData.orEmptyArray()) { cell ->
			cell.switch.onClick { cell.setMyTokenStatus() }
		}
	}
	
	private fun TokenSearchCell.setMyTokenStatus() {
		// 更新缓存中的数据, 防止 `Recycler` 复用的时候 `switch` `UI` 样式变化
		asyncData?.find {
			it.contract.equals(model?.contract, true)
		}?.let {
			it.isUsed = switch.isChecked
		}
		
		model?.let {
			DefaultTokenTable.getTokenByContract(it.contract) { localToken ->
				localToken.isNotNull {
					DefaultTokenTable.updateTokenDefaultStatus(it.contract, switch.isChecked) {
						insertToMyToken(switch, it)
					}
				} otherwise {
					DefaultTokenTable.insertToken(it.apply { isDefault = switch.isChecked }) {
						insertToMyToken(switch, it)
					}
				}
			}
		}
		
		switch.preventDuplicateClicks()
	}
	
	private fun insertToMyToken(switch: HoneyBaseSwitch, model: DefaultTokenTable?) {
		getMainActivity()?.apply {
			model?.let {
				TokenManagementListPresenter.updateMyTokensInfoBy(switch, it)
			}
		}
	}
	
	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<TokenManagementFragment> {
			headerTitle = TokenManagementText.addToken
			presenter.popFragmentFrom<TokenSearchFragment>()
			overlayView.header.showSearchInput(false)
		}
	}
}