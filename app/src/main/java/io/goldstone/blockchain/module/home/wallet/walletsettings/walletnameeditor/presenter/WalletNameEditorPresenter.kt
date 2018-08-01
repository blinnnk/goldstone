package io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.view.WalletNameEditorFragment
import org.jetbrains.anko.toast

/**
 * @date 26/03/2018 10:44 PM
 * @author KaySaith
 */
class WalletNameEditorPresenter(
	override val fragment: WalletNameEditorFragment
) : BasePresenter<WalletNameEditorFragment>() {
	
	fun changeWalletName(nameInput: EditText) {
		nameInput.text.toString().let {
			it.isEmpty() isTrue {
				fragment.context?.toast(WalletSettingsText.emptyNameAleryt)
			} otherwise {
				WalletTable.updateName(nameInput.text.toString()) {
					fragment.activity?.jump<SplashActivity>()
				}
			}
		}
	}
	
	fun shouCurrentNameHint(nameInput: EditText) {
		WalletTable.getCurrentWallet {
			nameInput.hint = name
		}
	}
	
	fun updateConfirmButtonStyle(nameInput: EditText) {
		if (nameInput.text.isNotEmpty()) fragment.confirmButton.setBlueStyle()
		else fragment.confirmButton.setGrayStyle()
	}
}