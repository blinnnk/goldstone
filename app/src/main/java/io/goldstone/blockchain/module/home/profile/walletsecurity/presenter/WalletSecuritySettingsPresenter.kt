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
	}

	// 跳转至钱包锁校验身份界面
	fun showPassCodeFragment() {
	}
}