package io.goldstone.blinnnk.module.home.wallet.walletsettings.walletnameeditor.presenter

import android.widget.EditText
import com.blinnnk.extension.jump
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.utils.alert
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.entrance.splash.view.SplashActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletnameeditor.view.WalletNameEditorFragment
import kotlinx.coroutines.Dispatchers

/**
 * @date 26/03/2018 10:44 PM
 * @author KaySaith
 */
class WalletNameEditorPresenter(
	override val fragment: WalletNameEditorFragment
) : BasePresenter<WalletNameEditorFragment>() {

	fun changeWalletName(nameInput: EditText) {
		val name = nameInput.text.toString()
		if (name.isEmpty()) fragment.context.alert(WalletSettingsText.emptyNameAleryt)
		else WalletTable.updateName(nameInput.text.toString()) {
			fragment.activity?.jump<SplashActivity>()
		}
	}

	fun showCurrentNameHint(nameInput: EditText) {
		WalletTable.getCurrent(Dispatchers.Main) {
			nameInput.hint = name
		}
	}

	fun updateConfirmButtonStyle(nameInput: EditText) {
		if (nameInput.text.isNotEmpty())
			fragment.confirmButton.setBlueStyle()
		else fragment.confirmButton.setGrayStyle()
	}
}