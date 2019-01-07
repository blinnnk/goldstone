package io.goldstone.blinnnk.module.home.wallet.walletsettings.hint.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.utils.alert
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.wallet.walletsettings.hint.view.HintFragment

/**
 * @date 24/04/2018 10:54 AM
 * @author KaySaith
 */
class HintPresenter(
	override val fragment: HintFragment
) : BasePresenter<HintFragment>() {
	
	fun updateHint(hintInput: EditText) {
		hintInput.text?.toString()?.let {
			it.isNotEmpty() isTrue {
				WalletTable.updateHint(it) {
					fragment.context?.alert(CommonText.succeed)
				}
			} otherwise {
				fragment.context?.alert(WalletSettingsText.hintAlert)
			}
		}
	}
}