package io.goldstone.blinnnk.module.common.walletimport.keystoreimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.jump
import com.blinnnk.extension.setMargins
import com.blinnnk.extension.setUnderline
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.AgreementView
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.button.roundButton
import io.goldstone.blinnnk.common.component.edittext.RoundInput
import io.goldstone.blinnnk.common.component.edittext.WalletEditText
import io.goldstone.blinnnk.common.component.edittext.roundInput
import io.goldstone.blinnnk.common.component.title.AttentionTextView
import io.goldstone.blinnnk.common.component.title.ExplanationTitle
import io.goldstone.blinnnk.common.language.*
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.NetworkUtil
import io.goldstone.blinnnk.common.utils.UIUtils
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.common.value.WebUrl
import io.goldstone.blinnnk.module.common.walletimport.keystoreimport.presenter.KeystoreImportPresenter
import io.goldstone.blinnnk.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blinnnk.module.common.walletimport.walletimportcenter.view.SupportedChainMenu
import io.goldstone.blinnnk.module.common.webview.view.WebViewFragment
import io.goldstone.blinnnk.module.entrance.splash.view.SplashActivity
import io.goldstone.blinnnk.module.home.home.view.MainActivity
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
			lparams(matchParent, wrapContent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, wrapContent)
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

				nameInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					hint = UIUtils.generateDefaultName()
					title = CreateWalletText.name
				}
				nameInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}

				passwordInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					setPasswordInput()
					title = CreateWalletText.password
				}
				passwordInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 5.uiPX()
				}

				hintInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					setTextInput()
					title = CreateWalletText.hint
				}
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

				confirmButton = roundButton {
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
							button.showLoadingStatus(false)
							if (it.hasError()) safeShowError(it)
							else activity?.jump<SplashActivity>()
						}
					}
				}

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