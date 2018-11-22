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
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.crypto.multichain.DefaultPath
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter.MnemonicImportDetailPresenter
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
	private val confirmButton by lazy { RoundButton(context!!) }
	private val walletNameInput by lazy { RoundInput(context!!) }
	private val mnemonicInput by lazy { WalletEditText(context!!) }
	private val pathSettings by lazy { RoundCell(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val repeatPassword by lazy { RoundInput(context!!) }
	private val hintInput by lazy { RoundInput(context!!) }
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
					setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
				}.into(this)

				pathSettings.apply {
					setTitles(ImportWalletText.path, ImportWalletText.defaultPath)
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 20.uiPX()
						bottomMargin = 10.uiPX()
					}
				}.click {
					showPatSettingsDashboard()
				}.into(this)

				walletNameInput.apply {
					hint = UIUtils.generateDefaultName()
					setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
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

				hintInput.apply {
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CreateWalletText.hint
				}.into(this)

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

				confirmButton.apply {
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
				}.into(this)
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
		if (activity.isNull()) {
			getParentFragment<WalletImportFragment>()?.presenter
				?.popFragmentFrom<MnemonicImportDetailFragment>()
		} else super.setBaseBackEvent(activity, parent)
	}
}