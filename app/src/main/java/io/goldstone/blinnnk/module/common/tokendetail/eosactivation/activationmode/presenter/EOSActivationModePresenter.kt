package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.activationmode.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.activationmode.view.EOSActivationModeFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbyfriend.view.RegisterByFriendFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view.SmartContractRegisterFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSActivationModePresenter(
	override val fragment: EOSActivationModeFragment
) : BasePresenter<EOSActivationModeFragment>() {
	fun showRegisterByFriendFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<RegisterByFriendFragment>()
	}

	fun showRegisterBySmartContractFragment() {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<SmartContractRegisterFragment>()
	}
}