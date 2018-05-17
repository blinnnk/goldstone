package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter.PasswordSettingsPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */

class PasswordSettingsFragment : BaseFragment<PasswordSettingsPresenter>() {

	private val oldPassword by lazy { RoundInput(context!!) }
	private val newPassword by lazy { RoundInput(context!!) }
	private val repeatPassword by lazy { RoundInput(context!!) }
	private val passwordHint by lazy { RoundInput(context!!) }
	val confirmButton by lazy { RoundButton(context!!) }

	override val presenter = PasswordSettingsPresenter(this)

	override fun AnkoContext<Fragment>.initView() {

		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			oldPassword.apply {
				title = WalletSettingsText.oldPassword
				setPasswordInput()
				setMargins<LinearLayout.LayoutParams> { topMargin = 40.uiPX() }
			}.into(this)

			newPassword.apply {
				title = WalletSettingsText.newPassword
				setPasswordInput()
				setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
			}.into(this)

			repeatPassword.apply {
				title = CreateWalletText.repeatPassword
				setPasswordInput()
				setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
			}.into(this)

			passwordHint.apply {
				title = CreateWalletText.hint
				setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
			}.into(this)

			confirmButton.apply {
				text = CommonText.confirm
				setBlueStyle()
				setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
			}.click {
				presenter.updatePassword(oldPassword, newPassword, repeatPassword)
			}.into(this)
		}
	}

	override fun setBackEvent(activity: MainActivity) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.walletSettings
			presenter.showWalletSettingListFragment()
		}
	}
}