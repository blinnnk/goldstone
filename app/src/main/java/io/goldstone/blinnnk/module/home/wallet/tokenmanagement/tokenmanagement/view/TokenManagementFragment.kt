package io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagement.view

import android.view.ViewGroup
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.language.TokenManagementText
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagement.presenter.TokenManagementPresenter

/**
 * @date 25/03/2018 2:46 AM
 * @author KaySaith
 */
class TokenManagementFragment : BaseOverlayFragment<TokenManagementPresenter>() {
	
	override val presenter = TokenManagementPresenter(this)
	
	override fun ViewGroup.initView() {
		headerTitle = TokenManagementText.addToken
		presenter.showTokenManagementFragment()
	}
}