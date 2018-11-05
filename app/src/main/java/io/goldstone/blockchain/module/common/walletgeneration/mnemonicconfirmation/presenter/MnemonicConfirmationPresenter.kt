package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter

import android.content.Context
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.alert
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
			WalletTable.updateHasBackupMnemonic {
				validAndContinue()
			}
		} otherwise {
			fragment.context?.alert(ImportWalletText.mnemonicAlert)
		}
	}

	override fun onFragmentViewCreated() {
		// 如果在窗前钱包的界面用户点击了关闭按钮那么直接切换钱包
		if (fragment.activity is MainActivity) {
			fragment.getParentFragment<WalletGenerationFragment> {
				overlayView.header.showCloseButton(true) {
					activity?.jump<SplashActivity>()
				}
			}
		}
	}

	private fun compareMnemonicCode(correct: String, current: String): Boolean {
		return correct.equals(current, true)
	}

	private fun validAndContinue() {
		val currentActivity = fragment.activity
		when (currentActivity) {
			is MainActivity -> {
				fragment.getParentFragment<WalletSettingsFragment> {
					presenter.removeSelfFromActivity()
					currentActivity.showSucceedDialog()
				}

				fragment.getParentFragment<WalletGenerationFragment> {
					presenter.removeSelfFromActivity()
					fragment.activity?.jump<SplashActivity>()
				}
			}

			is SplashActivity -> {
				fragment.activity?.jump<SplashActivity>()
			}
		}
	}

	private fun Context.showSucceedDialog() {
		GoldStoneDialog.show(this) {
			showOnlyConfirmButton {
				GoldStoneDialog.remove(context)
			}
			setImage(R.drawable.succeed_banner)
			setContent(CommonText.succeed, DialogText.backUpMnemonicSucceed)
		}
	}

	override fun onFragmentShowFromHidden() {
		fragment.parentFragment.apply {
			fun BaseOverlayFragment<*>.resetEvent() {
				headerTitle = CreateWalletText.mnemonicConfirmation
				overlayView.header.showCloseButton(false) {}
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