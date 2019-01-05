package io.goldstone.blinnnk.module.common.walletgeneration.mnemonicbackup.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.jump
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blinnnk.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blinnnk.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blinnnk.module.entrance.splash.view.SplashActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 22/03/2018 9:32 PM
 * @author KaySaith
 */
class MnemonicBackupPresenter(
	override val fragment: MnemonicBackupFragment
) : BasePresenter<MnemonicBackupFragment>() {

	fun skipBackUp() {
		val parent = fragment.parentFragment
		when (parent) {
			is WalletGenerationFragment -> {
				fragment.activity?.jump<SplashActivity>()
			}

			is WalletSettingsFragment -> {
				parent.presenter.removeSelfFromActivity()
			}
		}
	}

	fun goToMnemonicConfirmation(mnemonic: String?) {
		val argument = Bundle().apply {
			putString(ArgumentKey.mnemonicCode, mnemonic)
		}
		val parent = fragment.parentFragment
		when (parent) {
			is WalletGenerationFragment ->
				showTargetFragment<MnemonicConfirmationFragment, WalletGenerationFragment>(argument)

			is WalletSettingsFragment ->
				showTargetFragment<MnemonicConfirmationFragment, WalletSettingsFragment>(argument)

		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		val parent = fragment.parentFragment
		when (parent) {
			is WalletSettingsFragment -> {
				parent.apply {
					headerTitle = CreateWalletText.mnemonicBackUp
					showBackButton(true) {
						parent.presenter.popFragmentFrom<MnemonicBackupFragment>()
					}
				}
			}
		}

		fragment.activity?.let {
			if (it is SplashActivity) {
				it.backEvent = Runnable {
					fragment.getParentFragment<WalletGenerationFragment> {
						presenter.removeSelfFromActivity()
						it.backEvent = null
					}
				}
			}
		}
	}
}
