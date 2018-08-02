package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

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
import io.goldstone.blockchain.crypto.DefaultPath
import io.goldstone.blockchain.crypto.MultiChainPath
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
	
	private val confirmButton by lazy { RoundButton(context!!) }
	private val mnemonicInput by lazy { WalletEditText(context!!) }
	private val pathSettings by lazy { RoundCell(context!!) }
	private val walletNameInput by lazy { RoundInput(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val repeatPassword by lazy { RoundInput(context!!) }
	private val hintInput by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	override val presenter = MnemonicImportDetailPresenter(this)
	// Default Value
	private var defaultPath = arrayListOf(
		DefaultPath.ethPath,
		DefaultPath.etcPath,
		DefaultPath.btcPath,
		DefaultPath.btcTestPath
	)
	
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				mnemonicInput.apply {
					hint = ImportWalletText.mnemonicHint
					setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
				}.into(this)
				
				pathSettings
					.apply {
						setTitles(ImportWalletText.path, ImportWalletText.defaultPath)
						setMargins<LinearLayout.LayoutParams> {
							topMargin = 20.uiPX()
							bottomMargin = 10.uiPX()
						}
					}
					.click { showPatSettingsDashboard() }
					.into(this)
				
				walletNameInput.apply {
					hint = UIUtils.generateDefaultName()
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
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
				
				hintInput.apply {
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
					title = CreateWalletText.hint
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
					presenter.importWalletByMnemonic(
						MultiChainPath(
							defaultPath[0],
							defaultPath[1],
							defaultPath[2],
							defaultPath[3]
						),
						mnemonicInput,
						passwordInput,
						repeatPassword,
						hintInput,
						agreementView.radioButton.isChecked,
						walletNameInput
					) { isScuccessful ->
						it.showLoadingStatus(false)
						if (isScuccessful) activity?.jump<SplashActivity>()
					}
				}.into(this)
				
				
				ExplanationTitle(context).apply {
					text = QAText.whatIsMnemonic.setUnderline()
				}.click {
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								QAText.whatIsMnemonic,
								ImportWalletText.importWallet,
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsMnemonic)
								}
							)
						}
					}
				}.into(this)
			}
		}
	}
	
	private val pathInfo = listOf(
		Pair(ImportWalletText.customEthereumPath, DefaultPath.ethPathHeader),
		Pair(ImportWalletText.customEthereumClassicPath, DefaultPath.etcPathHeader),
		Pair(ImportWalletText.customBitcoinPath, DefaultPath.btcPathHeader),
		Pair(ImportWalletText.customBTCTestPath, DefaultPath.btcTestPathHeader)
	)
	
	private fun showPatSettingsDashboard() {
		getParentContainer()?.apply {
			DashboardOverlay(context) {
				pathInfo.forEachIndexed { index, it ->
					TopBottomLineCell(context).apply {
						layoutParams = LinearLayout.LayoutParams(
							ScreenSize.widthWithPadding - 40.uiPX(),
							100.uiPX()
						)
						setTitle(it.first, fontSize(14))
						TitleEditText(context).apply {
							tag = "pathEdit$index"
							setTitle(it.second)
							getEditText().setText(DefaultPath.default)
						}.into(this)
					}.into(this)
				}
			}.apply {
				confirmEvent = Runnable {
					defaultPath.clear()
					pathInfo.forEachIndexed { index, pair ->
						findViewWithTag<TitleEditText>("pathEdit$index")?.let {
							defaultPath.add(pair.second + it.getText())
						}
					}
				}
			}.into(this)
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
}