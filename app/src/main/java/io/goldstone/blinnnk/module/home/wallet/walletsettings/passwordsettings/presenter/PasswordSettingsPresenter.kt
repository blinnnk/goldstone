package io.goldstone.blinnnk.module.home.wallet.walletsettings.passwordsettings.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.util.getDeviceBrand
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.DeviceName
import io.goldstone.blinnnk.crypto.keystore.updatePasswordByWalletID
import io.goldstone.blinnnk.crypto.keystore.verifyKeystorePasswordByWalletID
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */
class PasswordSettingsPresenter(
	override val fragment: PasswordSettingsFragment
) : BasePresenter<PasswordSettingsFragment>() {

	@WorkerThread
	fun checkOrUpdatePassword(
		oldPassword: String,
		newPassword: String,
		repeatPassword: String,
		passwordHint: String,
		callback: (GoldStoneError) -> Unit
	) = GlobalScope.launch(Dispatchers.Default) {
		if (oldPassword.isEmpty()) callback(AccountError.EmptyRepeatPassword)
		else CreateWalletPresenter.checkInputValue(
			"",
			newPassword,
			repeatPassword,
			true
		) { password, _, error ->
			if (password.isNullOrEmpty() || error.hasError()) callback(error)
			else {
				val wallet = WalletTable.dao.findWhichIsUsing() ?: return@checkInputValue
				fragment.context?.verifyKeystorePasswordByWalletID(oldPassword, wallet.id) { isCorrect ->
					if (isCorrect) updateKeystorePasswordByWalletID(
						wallet.id,
						oldPassword,
						password,
						passwordHint
					) {
						if (it.isNone()) launchUI { autoBack() }
						else fragment.safeShowError(it)
					} else callback(AccountError.WrongPassword)
				}
			}
		}
	}

	private fun updateKeystorePasswordByWalletID(
		walletID: Int,
		oldPassword: String,
		newPassword: String,
		passwordHint: String,
		@WorkerThread callback: (AccountError) -> Unit
	) {
		fragment.context?.updatePasswordByWalletID(
			walletID,
			oldPassword,
			newPassword
		) {
			// Update User Password Hint
			passwordHint.isNotEmpty() isTrue {
				WalletTable.updateHint(passwordHint)
			}
			callback(it)
		}
	}

	@UiThread
	private fun autoBack() = launchUI {
		fragment.getParentFragment<WalletSettingsFragment> {
			// `VIVO` 手机显示 `toast` 会出错
			if (!getDeviceBrand().contains(DeviceName.vivo, true)) activity?.toast(CommonText.succeed)
			presenter.popFragmentFrom<PasswordSettingsFragment>()
		}
	}
}