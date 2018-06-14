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
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.WalletSettingsText
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
	
	val confirmButton by lazy { RoundButton(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	override val presenter = WalletNameEditorPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			nameInput.apply {
				title = WalletSettingsText.walletNameSettings
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 40.uiPX()
					bottomMargin = 30.uiPX()
				}
				
				addTextChangedListener(object : TextWatcher {
					override fun beforeTextChanged(
						p0: CharSequence?,
						p1: Int,
						p2: Int,
						p3: Int
					) {
					}
					
					override fun onTextChanged(
						p0: CharSequence?,
						p1: Int,
						p2: Int,
						p3: Int
					) {
					}
					
					override fun afterTextChanged(p0: Editable?) {
						presenter.updateConfirmButtonStyle(nameInput)
					}
				})
			}.into(this)
			
			presenter.shouCurrentNameHint(nameInput)
			
			confirmButton.apply {
				text = CommonText.confirm
				setGrayStyle()
			}.click {
				presenter.changeWalletName(nameInput)
			}.into(this)
		}
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.walletSettings
			presenter.showWalletSettingListFragment()
		}
	}
}