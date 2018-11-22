package io.goldstone.blockchain.module.common.walletimport.keystoreimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.jump
import com.blinnnk.extension.setMargins
import com.blinnnk.extension.setUnderline
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter.KeystoreImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view.SupportedChainMenu
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 1:49 AM
 * @author KaySaith
 */
class KeystoreImportFragment : BaseFragment<KeystoreImportPresenter>() {

	override val pageTitle: String = ImportMethodText.keystore
	private lateinit var attentionText: AttentionTextView
	private lateinit var supportedChainMenu: SupportedChainMenu
	private lateinit var keystoreEditText: WalletEditText
	private lateinit var nameInput: RoundInput
	private lateinit var passwordInput: RoundInput
	private lateinit var hintInput: RoundInput
	private lateinit var agreementView: AgreementView
	private lateinit var confirmButton: RoundButton
	private val overlayFragment by lazy {
		parentFragment as? WalletImportFragment
	}

	override val presenter = KeystoreImportPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)

				attentionText = AttentionTextView(context)
				attentionText.apply {
					isCenter()
					setPadding(15.uiPX(), 30.uiPX(), 15.uiPX(), 20.uiPX())
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					text = ImportWalletText.keystoreIntro
				}.into(this)

				supportedChainMenu = SupportedChainMenu(context)
				supportedChainMenu.into(this)

				keystoreEditText = WalletEditText(context)
				keystoreEditText.apply {
					hint = ImportWalletText.keystoreHint
				}.into(this)

				nameInput = RoundInput(context)
				nameInput.apply {
					hint = UIUtils.generateDefaultName()
					title = CreateWalletText.name
				}.into(this)
				nameInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}

				passwordInput = RoundInput(context)
				passwordInput.apply {
					setPasswordInput()
					title = CreateWalletText.password
				}.into(this)
				passwordInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 5.uiPX()
				}

				hintInput = RoundInput(context)
				hintInput.apply {
					setTextInput()
					title = CreateWalletText.hint
				}.into(this)
				hintInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 5.uiPX()
				}

				agreementView = AgreementView(context)
				agreementView.click {
					overlayFragment?.presenter?.showTargetFragment<WebViewFragment>(
						Bundle().apply {
							putString(ArgumentKey.webViewUrl, WebUrl.terms)
							putString(ArgumentKey.webViewName, ProfileText.terms)
						}
					)
				}.into(this)
				agreementView.setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}

				confirmButton = RoundButton(context)
				confirmButton.apply {
					setBlueStyle(10.uiPX())
					text = CommonText.confirm.toUpperCase()
				}.click { button ->
					button.showLoadingStatus()
					presenter.importKeystoreWallet(
						keystoreEditText.text.toString(),
						passwordInput,
						nameInput,
						agreementView.radioButton.isChecked,
						hintInput
					) {
						launchUI {
							if (it.hasError()) safeShowError(it)
							else {
								button.showLoadingStatus(false)
								activity?.jump<SplashActivity>()
							}
						}
					}
				}.into(this)

				ExplanationTitle(context).apply {
					text = QAText.whatIsKeystore.setUnderline()
				}.click {
					if (NetworkUtil.hasNetworkWithAlert(context)) {
						overlayFragment?.presenter?.showTargetFragment<WebViewFragment>(
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.whatIsKeystore)
								putString(ArgumentKey.webViewName, QAText.whatIsKeystore)
							}
						)
					}
				}.into(this)
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		overlayFragment?.presenter?.popFragmentFrom<KeystoreImportFragment>()
	}
}