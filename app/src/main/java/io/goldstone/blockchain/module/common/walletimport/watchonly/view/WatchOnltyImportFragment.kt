package io.goldstone.blockchain.module.common.walletimport.watchonly.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
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
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.presenter.WatchOnlyImportPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 2:15 AM
 * @author KaySaith
 */
class WatchOnlyImportFragment : BaseFragment<WatchOnlyImportPresenter>() {

	override val pageTitle: String = ImportMethodText.watchOnly
	private val attentionView by lazy { AttentionTextView(context!!) }
	private val typeSettings by lazy { RoundCell(context!!) }
	private val nameInput by lazy { RoundInput(context!!) }
	private val addressInput by lazy { WalletEditText(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var currentType = AddressType.ETHSeries.value
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

			typeSettings.apply {
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
					bottomMargin = 10.uiPX()
				}
				setTitles(ImportWalletText.walletType, currentType)
			}.click { _ ->
				if (getParentContainer().isNull()) return@click
				PrivateKeyImportFragment.showWalletTypeDashboard(
					getParentContainer()!!,
					currentType
				) {
					currentType = it
					typeSettings.setTitles(ImportWalletText.walletType, it)
				}
			}.into(this)

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
			}.click { button ->
				button.showLoadingStatus()
				presenter.importWatchOnlyWallet(currentType, addressInput, nameInput) {
					if (!it.isNone()) context.alert(it.message)
					else activity?.jump<SplashActivity>()
					button.showLoadingStatus(false)
				}
			}.into(this)

			ExplanationTitle(context).apply {
				text = QAText.whatIsWatchOnlyWallet.setUnderline()
			}.click {
				getParentFragment<WalletImportFragment> {
					NetworkUtil.hasNetworkWithAlert(context) isTrue {
						presenter.showTargetFragment<WebViewFragment>(
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.whatIsWatchOnly)
								putString(ArgumentKey.webViewName, WalletText.watchOnly)
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