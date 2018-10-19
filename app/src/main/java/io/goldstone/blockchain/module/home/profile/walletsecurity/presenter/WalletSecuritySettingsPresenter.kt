package io.goldstone.blockchain.module.home.profile.walletsecurity.presenter

import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.hideChildFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PassCodeFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.profile.walletsecurity.view.WalletSecuritySettingsFragment

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class WalletSecuritySettingsPresenter(override val fragment : WalletSecuritySettingsFragment) : BasePresenter<WalletSecuritySettingsFragment>() {

	fun setFingerprintStatus(
		status : Boolean,
		callback : () -> Unit = {}
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
				ArgumentKey.setTheDigitalLock
			)
			putBoolean(
				ArgumentKey.setPinCode,
				true
			)
		}
	}

	// 跳转至钱包锁校验身份界面
	fun showPassCodeFragment() {
		fragment.getParentFragment<ProfileOverlayFragment> {
			hideChildFragment(fragment)
		}
		fragment.activity?.addFragmentAndSetArguments<PassCodeFragment>(
			ContainerID.main,
			FragmentTag.pinCode
		) { }
	}
}