package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.component.overlay.RadioDashboard
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.multichain.AddressType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view.SupportedChainMenu
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */
class PrivateKeyImportFragment : BaseFragment<PrivateKeyImportPresenter>() {

	override val pageTitle: String = ImportMethodText.privateKey
	private val attentionText by lazy { AttentionTextView(context!!) }
	private val supportedChainMenu by lazy { SupportedChainMenu(context!!) }
	private val privateKeyInput by lazy { WalletEditText(context!!) }
	private val passwordHintInput by lazy { RoundInput(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val repeatPassword by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = PrivateKeyImportPresenter(this)

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
				privateKeyInput.apply {
					hint = ImportWalletText.privateKeyHint
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
					setPasswordSafeLevel()
				}.into(this)

				repeatPassword.apply {
					setPasswordInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CreateWalletText.repeatPassword
				}.into(this)

				passwordHintInput.apply {
					title = CreateWalletText.hint
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
				}.into(this)

				agreementView
					.click {
						getParentFragment<WalletImportFragment> {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.terms)
									putString(ArgumentKey.webViewName, ProfileText.terms)
								}
							)
						}
					}
					.into(this)

				confirmButton.apply {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle()
					y += 10.uiPX()
				}.click { button ->
					button.showLoadingStatus()
					presenter.importWalletByPrivateKey(
						privateKeyInput,
						passwordInput,
						repeatPassword,
						agreementView.radioButton.isChecked,
						nameInput,
						passwordHintInput
					) {
						button.showLoadingStatus(false)
						if (!it.isNone()) context.alert(it.message)
						else activity?.jump<SplashActivity>()
					}
				}.into(this)

				ExplanationTitle(context).apply {
					text = QAText.whatIsPrivateKey.setUnderline()
				}.click {
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsPrivatekey)
									putString(ArgumentKey.webViewName, QAText.whatIsPrivateKey)
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

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentContainer()?.findViewById<DashboardOverlay>(ElementID.dashboardOverlay).apply {
			isNotNull {
				getParentContainer()?.removeView(this)
			} otherwise {
				super.setBaseBackEvent(activity, parent)
			}
		}
	}

	companion object {
		fun showWalletTypeDashboard(
			viewGroup: ViewGroup,
			type: String,
			updateCurrentType: (String) -> Unit
		) {
			object : RadioDashboard() {
				override val cellContent =
					arrayListOf(
						AddressType.ETHSeries.value,
						CoinSymbol.updateSymbolIfInReview(AddressType.BTC.value),
						CoinSymbol.updateSymbolIfInReview(AddressType.BTCSeriesTest.value, true),
						AddressType.LTC.value,
						AddressType.BCH.value,
						AddressType.EOS.value,
						AddressType.EOSJungle.value
					)
				override var defaultRadio = type

				override fun afterSelected() {
					updateCurrentType(defaultRadio)
				}
			}.inTo(viewGroup)
		}
	}
}