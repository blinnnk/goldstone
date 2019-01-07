package io.goldstone.blinnnk.module.home.wallet.walletsettings.passwordsettings.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.button.roundButton
import io.goldstone.blinnnk.common.component.edittext.RoundInput
import io.goldstone.blinnnk.common.component.edittext.roundInput
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.passwordsettings.presenter.PasswordSettingsPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */
class PasswordSettingsFragment : BaseFragment<PasswordSettingsPresenter>() {

	override val pageTitle: String = WalletSettingsText.passwordSettings
	private lateinit var oldPassword: RoundInput
	private lateinit var newPassword: RoundInput
	private lateinit var passwordHint: RoundInput
	private lateinit var repeatPassword: RoundInput
	lateinit var confirmButton: RoundButton
	override val presenter = PasswordSettingsPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			oldPassword = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				title = WalletSettingsText.oldPassword
				setPasswordInput()
			}
			oldPassword.setMargins<LinearLayout.LayoutParams> {
				topMargin = 40.uiPX()
			}

			newPassword = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				title = WalletSettingsText.newPassword
				setPasswordInput()
			}
			newPassword.setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
			}

			repeatPassword = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				title = CreateWalletText.repeatPassword
				setPasswordInput()
			}
			repeatPassword.setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
			}

			passwordHint = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				title = CreateWalletText.hint
			}
			passwordHint.setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
			}

			confirmButton = roundButton {
				text = CommonText.confirm
				setBlueStyle(15.uiPX())
			}.click { button ->
				button.showLoadingStatus()
				presenter.checkOrUpdatePassword(
					oldPassword.text.toString(),
					newPassword.text.toString(),
					repeatPassword.text.toString(),
					passwordHint.text.toString()
				) {
					if (it.hasError()) safeShowError(it)
					launchUI {
						button.showLoadingStatus(false)
					}
				}
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.popFragmentFrom<PasswordSettingsFragment>()
		}
	}
}