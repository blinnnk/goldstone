package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view

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
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.ValueView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.valueView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter.KeystoreExportPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportFragment : BaseFragment<KeystoreExportPresenter>() {

	override val pageTitle: String = WalletSettingsText.exportKeystore
	private lateinit var privateKeyTextView: ValueView
	private val passwordInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = KeystoreExportPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			DescriptionView(context).isExportKeyStore().into(this)
			privateKeyTextView = valueView {
				layoutParams.height = 200.uiPX()
			}.click {
				// 如果 `textView` 的内容不是默认的 `placeholder` 就可以支持点击复制
				if (it.getContent().isNotEmpty()) context.clickToCopy(it.getContent())
			}

			passwordInput.apply {
				setPasswordInput()
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 30.uiPX()
				}
				title = CreateWalletText.password
			}.into(this)

			confirmButton.apply {
				text = CommonText.confirm
				setBlueStyle(15.uiPX())
			}.click { button ->
				button.showLoadingStatus()
				presenter.getKeystoreJSON(passwordInput.text.toString()) { keystoreFile, error ->
					launchUI {
						if (keystoreFile.isNotNull() && error.isNone()) {
							privateKeyTextView.setContent(keystoreFile)
						} else safeShowError(error)
						button.showLoadingStatus(false)
					}
				}
			}.into(this)
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