package io.goldstone.blockchain.module.home.profile.walletsecurity.presenter

import android.widget.EditText
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.profile.walletsecurity.view.WalletSecuritySettingsFragment

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class WalletSecuritySettingsPresenter(
	override val fragment: WalletSecuritySettingsFragment
) : BasePresenter<WalletSecuritySettingsFragment>() {

	fun showPinCodeStatus(
		status: Boolean,
		callback: () -> Unit = {}
	) {
		AppConfigTable.apply {
			getAppConfig {
				if (it?.pincode.isNull()) {
					fragment.context?.alert(PincodeText.turnOnAttention)
					callback()
					return@getAppConfig
				}
				showPinCodeStatus(status) {
					callback()
				}
			}
		}
	}

	fun showFingerprintStatus(
		status: Boolean,
		callback: () -> Unit = {}
	) {
		AppConfigTable.apply {
			showFingerprintUnlockStatus(status) {
				callback()
			}
		}
	}

	fun resetPinCode(
		newPinCode: EditText,
		repeatPinCode: EditText,
		switch: HoneyBaseSwitch
	) {
		if (newPinCode.text.isEmpty()) {
			fragment.context?.alert(PincodeText.countAlert)
			return
		}

		if (newPinCode.text.length > Count.pinCode || repeatPinCode.text.length > Count.pinCode) {
			fragment.context?.alert(PincodeText.countAlert)
			return
		}

		if (newPinCode.text.toString() != repeatPinCode.text.toString()) {
			fragment.context?.alert(PincodeText.verifyAlert)
			return
		}

		AppConfigTable.updatePinCode(newPinCode.text.toString().toInt()) {
			fragment.context?.alert(CommonText.succeed)
			showPinCodeStatus(true)
			switch.isChecked = true
		}
	}

	// 跳转至设置数字密码锁界面
	fun setPassCodeFragment() {
	}

	// 跳转至钱包锁校验身份界面
	fun showPassCodeFragment() {
	}
}