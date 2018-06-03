package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view

import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.*
import io.goldstone.blockchain.GoldStoneApp
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
		model?.let { searchToken ->
			DefaultTokenTable.getCurrentChainTokenByContract(
				searchToken.contract,
				GoldStoneApp.getCurrentChain()
			) { localToken ->
				localToken.isNotNull {
					DefaultTokenTable.updateTokenDefaultStatus(
						localToken!!.contract,
						switch.isChecked
					) {
						insertToMyToken(switch, localToken)
					}
				} otherwise {
					DefaultTokenTable.insertToken(searchToken.apply {
						isDefault = switch.isChecked
					}) {
						insertToMyToken(switch, searchToken)
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