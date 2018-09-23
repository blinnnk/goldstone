package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.TitleEditText
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
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
	private var defaultPath = arrayListOf(
		DefaultPath.ethPath,
		DefaultPath.etcPath,
		DefaultPath.btcPath,
		DefaultPath.testPath,
		DefaultPath.ltcPath,
		DefaultPath.bchPath,
		DefaultPath.eosPath
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
							ProfileText.terms,
							ImportWalletText.importWallet,
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.terms)
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
							defaultPath[0],
							defaultPath[1],
							defaultPath[2],
							defaultPath[3],
							defaultPath[4],
							defaultPath[5],
							defaultPath[6]
						),
						mnemonicInput.text.toString(),
						passwordInput.text.toString(),
						repeatPassword.text.toString(),
						hintInput.text.toString(),
						agreementView.radioButton.isChecked,
						walletNameInput.text.toString()
					) {
						button.showLoadingStatus(false)
						if (!it.isNone()) context.alert(it.message)
						else activity?.jump<SplashActivity>()
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

	// 根据业务需要更改子集的值, 所以采用 `MutableList`
	private val defaultValue = mutableListOf(
		DefaultPath.default,
		DefaultPath.default,
		DefaultPath.default,
		DefaultPath.default,
		DefaultPath.default,
		DefaultPath.default,
		DefaultPath.default
	)

	private val pathInfo = listOf(
		Pair(ImportWalletText.customEthereumPath, DefaultPath.ethPathHeader),
		Pair(ImportWalletText.customEthereumClassicPath, DefaultPath.etcPathHeader),
		Pair(
			ImportWalletText.customBitcoinPath(Config.getYingYongBaoInReviewStatus()),
			DefaultPath.btcPathHeader
		),
		Pair(
			ImportWalletText.customBTCTestPath(Config.getYingYongBaoInReviewStatus()),
			DefaultPath.testPathHeader
		),
		Pair(ImportWalletText.customLitecoinPath, DefaultPath.ltcPathHeader),
		Pair(ImportWalletText.customBCHPath, DefaultPath.bchPathHeader),
		Pair(ImportWalletText.customEOSPath, DefaultPath.eosPathHeader)
	)

	private fun showPatSettingsDashboard() {
		getParentContainer()?.apply {
			DashboardOverlay(context) {
				pathInfo.forEachIndexed { index, it ->
					TopBottomLineCell(context).apply {
						layoutParams = LinearLayout.LayoutParams(
							ScreenSize.widthWithPadding - 40.uiPX(),
							85.uiPX()
						)
						setTitle(it.first, fontSize(14))
						TitleEditText(context).apply {
							y -= 10.uiPX()
							tag = "pathEdit$index"
							setTitle(it.second)
							getEditText().setText(defaultValue[index])
						}.into(this)
					}.into(this)
				}
			}.apply {
				confirmEvent = Runnable {
					defaultPath.clear()
					pathInfo.forEachIndexed { index, pair ->
						findViewWithTag<TitleEditText>("pathEdit$index")?.let {
							// 更新默认值的子集数据, 下次在打开的时候显示上次编辑过的数据
							defaultValue[index] = it.getText()
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