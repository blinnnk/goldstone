package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.*
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */
class PrivateKeyImportFragment : BaseFragment<PrivateKeyImportPresenter>() {
	
	private val privateKeyInput by lazy { WalletEditText(context!!) }
	private val passwordHintInput by lazy { RoundInput(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	private val typeSettings by lazy { RoundCell(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val repeatPassword by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var currentType = CryptoValue.PrivateKeyType.ETHERCAndETC.content
	override val presenter = PrivateKeyImportPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				
				privateKeyInput.apply {
					hint = ImportWalletText.privateKeyHint
					setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
				}.into(this)
				
				typeSettings
					.apply {
						setMargins<LinearLayout.LayoutParams> {
							topMargin = 20.uiPX()
							bottomMargin = 10.uiPX()
						}
						setTitles(ImportWalletText.walletType, currentType)
					}
					.click {
						showWalletTypeDashboard(
							this@PrivateKeyImportFragment,
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
					setPasswordSafeLevel()
				}.into(this)
				
				repeatPassword.apply {
					setPasswordInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
					title = CreateWalletText.repeatPassword
				}.into(this)
				
				passwordHintInput.apply {
					title = CreateWalletText.hint
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
				}.into(this)
				
				agreementView
					.click {
						getParentFragment<WalletImportFragment> {
							presenter.showTargetFragment<WebViewFragment>(
								ProfileText.terms,
								ImportWalletText.importWallet,
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.terms)
								}
							)
						}
					}
					.into(this)
				
				confirmButton.apply {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle()
					y += 10.uiPX()
				}.click {
					it.showLoadingStatus()
					presenter.importWalletByPrivateKey(
						CryptoValue.PrivateKeyType.getTypeByContent(currentType),
						privateKeyInput,
						passwordInput,
						repeatPassword,
						agreementView.radioButton.isChecked,
						nameInput,
						passwordHintInput
					) { isSuccessful ->
						it.showLoadingStatus(false)
						if (isSuccessful) activity?.jump<SplashActivity>()
					}
				}.into(this)
				
				ExplanationTitle(context).apply {
					text = QAText.whatIsPrivateKey.setUnderline()
				}.click {
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								QAText.whatIsPrivateKey,
								ImportWalletText.importWallet,
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsPrivatekey)
								}
							)
						}
					}
				}.into(this)
			}
		}
	}
	
	private fun RoundInput.setPasswordSafeLevel() {
		afterTextChanged = Runnable {
			CreateWalletPresenter.showPasswordSafeLevel(passwordInput)
		}
	}
	
	companion object {
		fun showWalletTypeDashboard(
			fragment: BaseFragment<*>,
			type: String,
			updateCurrentType: (String) -> Unit
		) {
			object : RadioDashboard() {
				override val cellContent =
					arrayListOf(
						CryptoValue.PrivateKeyType.ETHERCAndETC.content,
						CryptoValue.PrivateKeyType.BTC.content,
						CryptoValue.PrivateKeyType.BTCTest.content
					)
				override var defaultRadio = type
				
				override fun afterSelected() {
					updateCurrentType(defaultRadio)
				}
			}.inTo(fragment.getParentContainer())
		}
	}
}