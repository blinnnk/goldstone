package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.cell.roundCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.crypto.multichain.DefaultPath
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter.MnemonicImportDetailPresenter
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
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */
class MnemonicImportDetailFragment : BaseFragment<MnemonicImportDetailPresenter>() {
	override val pageTitle: String = ImportMethodText.mnemonic
	private lateinit var confirmButton: RoundButton
	private lateinit var walletNameInput: RoundInput
	private lateinit var pathSettings: RoundCell
	private lateinit var passwordInput: RoundInput
	private lateinit var repeatPassword: RoundInput
	private lateinit var hintInput: RoundInput
	private val mnemonicInput by lazy { WalletEditText(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	override val presenter = MnemonicImportDetailPresenter(this)
	// Default Value
	private val pathInfo = arrayListOf(
		PathModel(ImportWalletText.customEthereumPath, DefaultPath.ethPathHeader, DefaultPath.default),
		PathModel(ImportWalletText.customEthereumClassicPath, DefaultPath.etcPathHeader, DefaultPath.default),
		PathModel(
			ImportWalletText.customBitcoinPath(SharedWallet.getInReviewStatus()),
			DefaultPath.btcPathHeader,
			DefaultPath.default
		),
		PathModel(
			ImportWalletText.customBTCTestPath(SharedWallet.getInReviewStatus()),
			DefaultPath.testPathHeader,
			DefaultPath.default
		),
		PathModel(ImportWalletText.customLitecoinPath, DefaultPath.ltcPathHeader, DefaultPath.default),
		PathModel(ImportWalletText.customBCHPath, DefaultPath.bchPathHeader, DefaultPath.default),
		PathModel(ImportWalletText.customEOSPath, DefaultPath.eosPathHeader, DefaultPath.default)
	)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				mnemonicInput.apply {
					hint = ImportWalletText.mnemonicHint
				}.into(this)
				mnemonicInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 30.uiPX()
				}

				pathSettings = roundCell {
					setTitles(ImportWalletText.path, ImportWalletText.defaultPath)
				}.click {
					showPatSettingsDashboard()
				}
				pathSettings.setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
					bottomMargin = 10.uiPX()
				}

				walletNameInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					hint = UIUtils.generateDefaultName()
					title = CreateWalletText.name
				}
				walletNameInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 15.uiPX()
				}

				passwordInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					setPasswordInput()
					title = CreateWalletText.password
					setPasswordSafeLevel()
				}
				passwordInput.setMargins<LinearLayout.LayoutParams> {
					topMargin = 5.uiPX()
				}

				repeatPassword = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					setPasswordInput()
					title = CreateWalletText.repeatPassword
				}
				repeatPassword.setMargins<LinearLayout.LayoutParams> {
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

				agreementView.click {
					getParentFragment<WalletImportFragment> {
						presenter.showTargetFragment<WebViewFragment>(
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.terms)
								putString(ArgumentKey.webViewName, ProfileText.terms)
							}
						)
					}
				}.into(this)

				confirmButton = roundButton {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle(10.uiPX())
				}.click { button ->
					button.showLoadingStatus()
					presenter.importWalletByMnemonic(
						ChainPath(
							pathInfo[0].pathHeader + pathInfo[0].defaultPath,
							pathInfo[1].pathHeader + pathInfo[1].defaultPath,
							pathInfo[2].pathHeader + pathInfo[2].defaultPath,
							pathInfo[3].pathHeader + pathInfo[3].defaultPath,
							pathInfo[4].pathHeader + pathInfo[4].defaultPath,
							pathInfo[5].pathHeader + pathInfo[5].defaultPath,
							pathInfo[6].pathHeader + pathInfo[6].defaultPath
						),
						mnemonicInput.text.toString(),
						passwordInput.text.toString(),
						repeatPassword.text.toString(),
						hintInput.text.toString(),
						agreementView.radioButton.isChecked,
						walletNameInput.text.toString()
					) {
						launchUI {
							button.showLoadingStatus(false)
							if (it.hasError()) safeShowError(it)
							else activity?.jump<SplashActivity>()
						}
					}
				}

				ExplanationTitle(context).apply {
					text = QAText.whatIsMnemonic.setUnderline()
				}.click {
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsMnemonic)
									putString(ArgumentKey.webViewName, QAText.whatIsMnemonic)
								}
							)
						}
					}
				}.into(this)
			}
		}
	}

	private fun showPatSettingsDashboard() {
		MaterialDialog(context!!)
			.title(text = "Set BIP44 Path")
			.customListAdapter(PathAdapter(pathInfo))
			.positiveButton(text = CommonText.confirm)
			.negativeButton(text = CommonText.cancel)
			.show()
	}

	private fun RoundInput.setPasswordSafeLevel() {
		afterTextChanged = Runnable {
			CreateWalletPresenter.showPasswordSafeLevel(passwordInput)
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletImportFragment>()?.presenter
			?.popFragmentFrom<MnemonicImportDetailFragment>()
	}
}