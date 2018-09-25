package io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.view.EOSActivationModeFragment
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.view.RegisterByFriendFragment
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view.SmartContractRegisterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSActivationModePresenter(
	override val fragment: EOSActivationModeFragment
) : BasePresenter<EOSActivationModeFragment>() {
	fun showRegisterByFriendFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<RegisterByFriendFragment>("Register By Friend", "EOS")
	}

	fun showRegisterBySmartContractFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<SmartContractRegisterFragment>("Smart Contract Register", "EOS")
	}
}