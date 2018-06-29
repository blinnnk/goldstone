package io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.presenter.WalletGenerationPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity

/**
 * @date 22/03/2018 9:37 PM
 * @author KaySaith
 */
class WalletGenerationFragment : BaseOverlayFragment<WalletGenerationPresenter>() {
	
	override val presenter = WalletGenerationPresenter(this)
	
	override fun ViewGroup.initView() {
		presenter.showCreateWalletFragment()
	}
	
	override fun setTrasparentStatus() {
		if (activity !is MainActivity) {
			super.setTrasparentStatus()
		}
	}
}