package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view

import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter.TokenSearchPresenter
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
			it.symbol == model?.symbol
		}?.let {
			it.isUsed = switch.isChecked
		}
		model?.let {
			DefaultTokenTable.getTokenByContractAddress(it.contract) { localToken ->
				localToken.isNotNull {
					insertTokenToDataBase(this)
				} otherwise {
					DefaultTokenTable.insertTokenInfo(it) {
						insertTokenToDataBase(this)
					}
				}
			}
		}
	}

	private fun insertTokenToDataBase(cell: TokenSearchCell) {
		getMainActivity()?.apply {
			TokenManagementListPresenter.updateMyTokensInfoBy(cell, this)
		}
	}

}