package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.CommonText
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
		oldPasswordInput: EditText, newPasswordInput: EditText, repeatPasswordInput: EditText
	) {
		CreateWalletPresenter.checkInputValue(
			"",
			newPasswordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			true,
			fragment.context
		) { password, _ ->
			fragment.context?.updatePassword(
				WalletTable.current.address, oldPasswordInput.text.toString(), password
			) {
				fragment.toast(CommonText.succeed)
				fragment.getParentFragment<WalletSettingsFragment> {
					presenter.showTargetFragmentByTitle(WalletSettingsText.walletSettings)
				}
			}
		}
	}

}