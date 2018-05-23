package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter

import android.content.Context
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.removeStartAndEndValue
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 22/03/2018 11:40 PM
 * @author KaySaith
 */

class MnemonicConfirmationPresenter(
	override val fragment: MnemonicConfirmationFragment
) : BasePresenter<MnemonicConfirmationFragment>() {

	fun clickConfirmationButton(
		correct: String,
		current: String
	) {
		compareMnemonicCode(correct, current.removeStartAndEndValue(" ")) isTrue {
			validAndContinue()
		} otherwise {
			fragment.context?.alert("incorrect mnemonic please re-enter")
		}
	}

	private fun compareMnemonicCode(
		correct: String,
		current: String
	): Boolean {
		return correct.equals(current, true)
	}

	private fun validAndContinue() {
		val currentActivity = fragment.activity
		when (currentActivity) {
			is MainActivity -> {
				fragment.getParentFragment<WalletSettingsFragment> {
					context?.showSucceedDialog {
						presenter.removeSelfFromActivity()
					}
				}

				fragment.getParentFragment<WalletGenerationFragment> {
					context?.showSucceedDialog {
						presenter.removeSelfFromActivity()
					}
				}
			}

			is SplashActivity -> {
				fragment.activity?.jump<SplashActivity>()
			}
		}
		WalletTable.deleteEncryptMnemonicAfterUserHasBackUp()
	}

	private fun Context.showSucceedDialog(callback: () -> Unit) {
		GoldStoneDialog.show(this) {
			showOnlyConfirmButton {
				GoldStoneDialog.remove(context)
			}
			setImage(R.drawable.alert_banner)
			setContent(
				"WELCOME",
				"You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
			)
		}
		callback()
	}


}