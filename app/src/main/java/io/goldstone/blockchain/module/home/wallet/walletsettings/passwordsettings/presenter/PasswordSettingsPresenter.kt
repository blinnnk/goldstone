package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.updatePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.support.v4.toast

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */
class PasswordSettingsPresenter(
	override val fragment: PasswordSettingsFragment
) : BasePresenter<PasswordSettingsFragment>() {
	
	fun updatePassword(
		oldPasswordInput: EditText,
		newPasswordInput: EditText,
		repeatPasswordInput: EditText,
		passwordHint: EditText,
		callback: () -> Unit
	) {
		CreateWalletPresenter.checkInputValue(
			"",
			newPasswordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			true,
			fragment.context,
			callback // error callback
		) { password, _ ->
			// ToDO 低端机型解 `Keystore` 会耗时很久,等自定义的 `Alert` 完成后应当友好提示
			fragment.context?.updatePassword(
				Config.getCurrentAddress(),
				oldPasswordInput.text.toString(),
				password,
				{
					// error callback
					callback()
				}
			) {
				// Update User Password Hint
				passwordHint.text.toString().apply {
					isNotEmpty() isTrue {
						WalletTable.updateHint(this)
					}
				}
				
				fragment.toast(CommonText.succeed)
				callback()
				fragment.getParentFragment<WalletSettingsFragment> {
					presenter.showTargetFragmentByTitle(WalletSettingsText.walletSettings)
				}
			}
		}
	}
}