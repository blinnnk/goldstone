package io.goldstone.blockchain.module.common.walletimport.keystoreimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ElementID
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
	private val attentionText by lazy { AttentionTextView(context!!) }
	private val supportedChainMenu by lazy { SupportedChainMenu(context!!) }
	private val keystoreEditText by lazy { WalletEditText(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val hintInput by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = KeystoreImportPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				attentionText.apply {
					isCenter()
					setPadding(15.uiPX(), 30.uiPX(), 15.uiPX(), 20.uiPX())
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					text = ImportWalletText.importWalletDescription
				}.into(this)
				supportedChainMenu.into(this)

				keystoreEditText.apply {
					hint = ImportWalletText.keystoreHint
				}.into(this)

				nameInput.apply {
					hint = UIUtils.generateDefaultName()
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
					title = CreateWalletText.name
				}.into(this)

				passwordInput.apply {
					setPasswordInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CreateWalletText.password
				}.into(this)

				hintInput.apply {
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CreateWalletText.hint
				}.into(this)

				agreementView
					.apply {
						setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
					}.click {
						getParentFragment<WalletImportFragment> {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.terms)
									putString(ArgumentKey.webViewName, ProfileText.terms)
								}
							)
						}
					}.into(this)

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
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsKeystore)
									putString(ArgumentKey.webViewName, QAText.whatIsKeystore)
								}
							)
						}
					}
				}.into(this)
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentContainer()?.findViewById<DashboardOverlay>(ElementID.dashboardOverlay).apply {
			isNotNull {
				getParentContainer()?.removeView(this)
			} otherwise {
				super.setBaseBackEvent(activity, parent)
			}
		}
	}
}