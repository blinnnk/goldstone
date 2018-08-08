package io.goldstone.blockchain.module.common.walletimport.keystoreimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.CreateWalletText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.*
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.language.QAText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter.KeystoreImportPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 1:49 AM
 * @author KaySaith
 */
class KeystoreImportFragment : BaseFragment<KeystoreImportPresenter>() {
	
	private val attentionView by lazy { AttentionTextView(context!!) }
	private val keystoreEditText by lazy { WalletEditText(context!!) }
	private val typeSettings by lazy { RoundCell(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val hintInput by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var currentType = CryptoValue.PrivateKeyType.ETHERCAndETC.content
	override val presenter = KeystoreImportPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				attentionView.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
					text = ImportWalletText.keystoreIntro
					isCenter()
				}.into(this)
				keystoreEditText.apply {
					hint = ImportWalletText.keystoreHint
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)
				
				typeSettings
					.apply {
						setMargins<LinearLayout.LayoutParams> {
							topMargin = 20.uiPX()
							bottomMargin = 10.uiPX()
						}
						setTitles(ImportWalletText.walletType, currentType)
					}
					.click { _ ->
						PrivateKeyImportFragment.showWalletTypeDashboard(
							this@KeystoreImportFragment,
							currentType
						) {
							currentType = it
							typeSettings.setTitles(ImportWalletText.walletType, it)
						}
					}
					.into(this)
				
				nameInput.apply {
					hint = UIUtils.generateDefaultName()
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
					title = CreateWalletText.name
				}.into(this)
				
				passwordInput.apply {
					setPasswordInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
					title = CreateWalletText.password
				}.into(this)
				
				hintInput.apply {
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
					title = CreateWalletText.hint
				}.into(this)
				
				agreementView
					.apply {
						setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
					}.click {
						getParentFragment<WalletImportFragment> {
							presenter.showTargetFragment<WebViewFragment>(
								ProfileText.terms,
								ImportWalletText.importWallet,
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.terms)
								}
							)
						}
					}.into(this)
				
				confirmButton.apply {
					setBlueStyle()
					text = CommonText.confirm.toUpperCase()
					y += 10.uiPX()
				}.click {
					it.showLoadingStatus()
					presenter.importKeystoreWallet(
						currentType,
						keystoreEditText.text.toString(),
						passwordInput,
						nameInput,
						agreementView.radioButton.isChecked,
						hintInput
					) { isSuccessful ->
						it.showLoadingStatus(false)
						if (isSuccessful) activity?.jump<SplashActivity>()
					}
				}.into(this)
				
				ExplanationTitle(context).apply {
					text = QAText.whatIsKeystore.setUnderline()
				}.click {
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								QAText.whatIsKeystore,
								ImportWalletText.importWallet,
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsKeystore)
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