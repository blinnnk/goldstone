package io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.presenter

import android.os.Bundle
import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.sdk25.coroutines.onClick

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
		val argument = Bundle().apply { putString(ArgumentKey.mnemonicCode, mnemonic) }
		val parent = fragment.parentFragment
		when (parent) {
			is WalletGenerationFragment -> {
				showTargetFragment<MnemonicConfirmationFragment, WalletGenerationFragment>(
					CreateWalletText.mnemonicConfirmation, CreateWalletText.mnemonicBackUp, argument
				)
			}
			
			is WalletSettingsFragment -> {
				showTargetFragment<MnemonicConfirmationFragment, WalletSettingsFragment>(
					WalletSettingsText.walletSettings, CreateWalletText.mnemonicBackUp, argument
				)
			}
		}
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		val parent = fragment.parentFragment
		when (parent) {
			is WalletSettingsFragment -> {
				parent.apply {
					overlayView.header.showCloseButton(false)
					overlayView.header.showBackButton(true) {
						parent.presenter.showWalletSettingListFragment()
					}
				}
			}
		}
	}
}
