package io.goldstone.blinnnk.module.home.wallet.walletsettings.walletnameeditor.view

import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
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
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletnameeditor.presenter.WalletNameEditorPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 26/03/2018 10:44 PM
 * @author KaySaith
 */
class WalletNameEditorFragment : BaseFragment<WalletNameEditorPresenter>() {

	override val pageTitle: String = WalletSettingsText.walletName
	lateinit var confirmButton: RoundButton
	private lateinit var nameInput: RoundInput
	override val presenter = WalletNameEditorPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			nameInput = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				title = WalletSettingsText.walletNameSettings
				addTextChangedListener(object : TextWatcher {
					override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
					override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
					override fun afterTextChanged(p0: Editable?) {
						presenter.updateConfirmButtonStyle(nameInput)
					}
				})
			}
			nameInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 40.uiPX()
				bottomMargin = 30.uiPX()
			}
			presenter.showCurrentNameHint(nameInput)
			confirmButton = roundButton {
				text = CommonText.confirm
				setGrayStyle()
			}.click {
				presenter.changeWalletName(nameInput)
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			presenter.popFragmentFrom<WalletNameEditorFragment>()
		}
	}
}