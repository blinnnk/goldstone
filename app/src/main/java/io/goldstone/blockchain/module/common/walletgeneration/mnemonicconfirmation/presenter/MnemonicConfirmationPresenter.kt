package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter

import android.content.Context
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.DialogText
import io.goldstone.blockchain.common.value.ImportWalletText
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
		compareMnemonicCode(correct, current) isTrue {
			validAndContinue(correct)
		} otherwise {
			fragment.context?.alert(ImportWalletText.mnemonicAlert)
		}
	}
	
	private fun compareMnemonicCode(
		correct: String,
		current: String
	): Boolean {
		return correct.equals(current, true)
	}
	
	private fun validAndContinue(mnemonic: String) {
		val currentActivity = fragment.activity
		WalletTable.deleteEncryptMnemonicAfterUserHasBackUp(
			mnemonic
		) {
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
							fragment.activity?.jump<SplashActivity>()
						}
					}
				}
				
				is SplashActivity -> {
					fragment.activity?.jump<SplashActivity>()
				}
			}
		}
	}
	
	private fun Context.showSucceedDialog(callback: () -> Unit) {
		GoldStoneDialog.show(this) {
			showOnlyConfirmButton {
				GoldStoneDialog.remove(context)
			}
			setImage(R.drawable.succeed_banner)
			setContent(CommonText.succeed, DialogText.backUpMnemonicSucceed)
		}
		callback()
	}
	
	override fun onFragmentShowFromHidden() {
		fragment.parentFragment.apply {
			fun BaseOverlayFragment<*>.resetEvent() {
				headerTitle = CreateWalletText.mnemonicBackUp
				overlayView.header.showCloseButton(false)
				overlayView.header.showBackButton(true) {
					presenter.popFragmentFrom<MnemonicConfirmationFragment>()
				}
			}
			
			when (this) {
				is WalletGenerationFragment -> resetEvent()
				is WalletSettingsFragment -> resetEvent()
			}
		}
	}
}