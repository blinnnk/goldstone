package io.goldstone.blockchain.module.home.profile.securitysetting.contract

import android.widget.Switch
import io.goldstone.blockchain.common.utils.FingerprintAvailableStatus
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.profile.securitysetting.view.SecuritySwitchView

/**
 * @date: 2018-11-28.
 * @author: yangLiHai
 * @description:
 */
interface PinCodeAndFingerContract {
	interface GSView: GoldStoneView<GSPresenter> {
		
		fun SecuritySwitchView.notifyFingerStatus()
		
		fun SecuritySwitchView.notifyPincodeStatus()
		
		// 设置数字密码弹窗
		fun setPinCodeTips()
		
		// 设置指纹密码弹窗
		fun setFingerprintTips()
	}
	interface GSPresenter: GoldStonePresenter {
		fun updateFingerStatus(status: Boolean, callback: () -> Unit = {})
		fun goToSettingFinger()
	}
}