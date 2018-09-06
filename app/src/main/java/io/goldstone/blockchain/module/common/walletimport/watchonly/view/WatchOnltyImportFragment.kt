package io.goldstone.blockchain.module.common.walletimport.watchonly.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.ExplanationTitle
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.QAText
import io.goldstone.blockchain.common.language.WatchOnlyText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.PrivateKeyType
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.presenter.WatchOnlyImportPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 2:15 AM
 * @author KaySaith
 */
class WatchOnlyImportFragment : BaseFragment<WatchOnlyImportPresenter>() {

	private val attentionView by lazy { AttentionTextView(context!!) }
	private val typeSettings by lazy { RoundCell(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	private val addressInput by lazy { WalletEditText(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var currentType = PrivateKeyType.ETHERCAndETC.content
	override val presenter = WatchOnlyImportPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			attentionView.apply {
				isCenter()
				setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
				text = WatchOnlyText.intro
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
						this@WatchOnlyImportFragment,
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

			addressInput.apply {
				setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
				hint = WatchOnlyText.enterDescription
			}.into(this)

			confirmButton.apply {
				marginTop = 20.uiPX()
				setBlueStyle()
				text = CommonText.startImporting.toUpperCase()
			}.click {
				it.showLoadingStatus()
				presenter.importWatchOnlyWallet(currentType, addressInput, nameInput) {
					it.showLoadingStatus(false)
				}
			}.into(this)

			ExplanationTitle(context).apply {
				text = QAText.whatIsWatchOnlyWallet.setUnderline()
			}.click {
				getParentFragment<WalletImportFragment> {
					NetworkUtil.hasNetworkWithAlert(context) isTrue {
						presenter.showTargetFragment<WebViewFragment>(
							QAText.whatIsWatchOnlyWallet,
							ImportWalletText.importWallet,
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.whatIsWatchOnly)
							}
						)
					}
				}
			}.into(this)
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