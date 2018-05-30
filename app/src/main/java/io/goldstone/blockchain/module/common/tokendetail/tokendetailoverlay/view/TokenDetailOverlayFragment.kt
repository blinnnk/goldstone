package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel

/**
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */
class TokenDetailOverlayFragment : BaseOverlayFragment<TokenDetailOverlayPresenter>() {
	
	var valueHeader: TwoLineTitles? = null
	val token by lazy { arguments?.get(ArgumentKey.tokenDetail) as? WalletDetailCellModel }
	var confirmButton: RoundButton? = null
	override val presenter = TokenDetailOverlayPresenter(this)
	
	override fun ViewGroup.initView() {
		presenter.setValueHeader(token)
		presenter.showTokenDetailFragment(token)
	}
}