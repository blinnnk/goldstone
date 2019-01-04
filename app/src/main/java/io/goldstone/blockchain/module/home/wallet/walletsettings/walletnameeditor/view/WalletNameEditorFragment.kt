package io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.view

import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.presenter.WalletNameEditorPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
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