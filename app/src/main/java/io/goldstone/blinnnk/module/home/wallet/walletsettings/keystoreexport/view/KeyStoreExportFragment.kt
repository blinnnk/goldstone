package io.goldstone.blinnnk.module.home.wallet.walletsettings.keystoreexport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.component.DescriptionView
import io.goldstone.blinnnk.common.component.ValueView
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.button.roundButton
import io.goldstone.blinnnk.common.component.edittext.RoundInput
import io.goldstone.blinnnk.common.component.edittext.roundInput
import io.goldstone.blinnnk.common.component.valueView
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.keystoreexport.presenter.KeystoreExportPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportFragment : BaseFragment<KeystoreExportPresenter>() {

	override val pageTitle: String = WalletSettingsText.exportKeystore
	private lateinit var privateKeyValue: ValueView
	private lateinit var passwordInput: RoundInput
	private lateinit var confirmButton: RoundButton
	override val presenter = KeystoreExportPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			DescriptionView(context).isExportKeyStore().into(this)
			privateKeyValue = valueView {
				layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 150.uiPX())
			}.click {
				context.clickToCopy(privateKeyValue.getContent())
			}

			passwordInput = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				setPasswordInput()
				title = CreateWalletText.password
			}
			passwordInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 30.uiPX()
			}

			confirmButton = roundButton {
				text = CommonText.confirm
				setBlueStyle(15.uiPX())
			}.click { button ->
				button.showLoadingStatus()
				presenter.getKeystoreJSON(passwordInput.text.toString()) { keystoreFile, error ->
					launchUI {
						if (keystoreFile.isNotNull() && error.isNone()) {
							privateKeyValue.setContent(keystoreFile)
						} else safeShowError(error)
						button.showLoadingStatus(false)
					}
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		parentFragment?.let {
			if (it is BaseOverlayFragment<*>) {
				it.showAddButton(false) {}
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.viewAddresses
			presenter.popFragmentFrom<KeystoreExportFragment>()
		}
	}
}