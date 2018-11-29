package io.goldstone.blockchain.module.home.profile.securitysetting.presenter

import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.hideChildFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.profile.securitysetting.view.PinCodeAndFingerSettingsFragment

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class PinCodeAndFingerSettingsPresenter(
	override val fragment: PinCodeAndFingerSettingsFragment
): BasePresenter<PinCodeAndFingerSettingsFragment>() {

	fun setFingerprintStatus(
		status: Boolean,
		callback: () -> Unit = {}
	) {
		AppConfigTable.setFingerprintUnlockStatus(status) {
			callback()
		}
	}

	// 跳转至设置数字密码锁界面
	fun setPassCodeFragment() {
		fragment.getParentFragment<ProfileOverlayFragment> {
			hideChildFragment(fragment)
		}
		fragment.activity?.addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main) {
			putString(
				ArgumentKey.profileTitle,
				PincodeText.setTheDigitalLock
			)
			putBoolean(
				ArgumentKey.setPinCode,
				true
			)
		}
	}

	// 跳转至钱包锁校验身份界面
	fun showVerifyPinCodeFragment() {
		fragment.getParentFragment<ProfileOverlayFragment> {
			hideChildFragment(fragment)
		}
		fragment.activity?.addFragmentAndSetArguments<PasscodeFragment>(
			ContainerID.main,
			FragmentTag.pinCode
		) { }
	}
}