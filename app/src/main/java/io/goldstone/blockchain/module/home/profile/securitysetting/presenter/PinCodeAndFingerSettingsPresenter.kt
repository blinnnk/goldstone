package io.goldstone.blockchain.module.home.profile.securitysetting.presenter

import android.content.Intent
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.hideChildFragment
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.profile.securitysetting.contract.PinCodeAndFingerContract
import io.goldstone.blockchain.module.home.profile.securitysetting.view.PinCodeAndFingerSettingsFragment

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class PinCodeAndFingerSettingsPresenter(
	val fragment: PinCodeAndFingerSettingsFragment
): PinCodeAndFingerContract.GSPresenter {
	override fun updateFingerStatus(status: Boolean, callback: () -> Unit) {
		AppConfigTable.setFingerprintUnlockStatus(status) {
			callback()
		}
	}
	
	override fun goToSettingFinger() {
		val intent = Intent("android.settings.SETTINGS")
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		GoldStoneAPI.context.startActivity(intent)
	}
	
	override fun start() {
	}


	// 跳转至设置数字密码锁界面
	fun setPassCodeFragment() {
		fragment.getParentFragment<ProfileOverlayFragment> {
			hideChildFragment(fragment)
		}
		fragment.activity?.addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main) {
			putString(ArgumentKey.profileTitle, PincodeText.setTheDigitalLock)
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