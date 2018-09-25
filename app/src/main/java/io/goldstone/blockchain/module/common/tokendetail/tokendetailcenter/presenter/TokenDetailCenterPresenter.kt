package io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.presenter

import com.blinnnk.extension.getChildFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment


/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenDetailCenterPresenter(
	override val fragment: TokenDetailCenterFragment
) : BasePresenter<TokenDetailCenterFragment>() {

	fun refreshTransactionListFromDatabase() {
		// Transaction Detail Observing Transaction 后通过这个接口更新列表界面的这状态
		fragment.getChildFragment<TokenDetailFragment>()?.presenter?.loadDataFromDatabaseOrElse()
	}

}