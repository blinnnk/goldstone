package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import com.kenai.jffi.Main
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

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
		compareMnemonicCode(correct, current) isTrue {
			validAndGoHome()
		} otherwise {
			fragment.context?.toast("incorrect mnemonic please re-enter")
		}
	}

	private fun compareMnemonicCode(
		correct: String,
		current: String
	) =
		correct == current

	private fun validAndGoHome() {
		val currentActivity = fragment.activity
		when (currentActivity) {
			is MainActivity -> {
				fragment.getParentFragment<WalletSettingsFragment> {
					context?.alert("Back Up Mnemonic Succeed")
					presenter.removeSelfFromActivity()
				}
			}
			is SplashActivity -> {
				fragment.activity?.jump<SplashActivity>()
			}
		}
		WalletTable.deleteEncryptMnemonicAfterUserHasBackUp()
	}


}