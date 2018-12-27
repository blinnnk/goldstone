package io.goldstone.blockchain.module.common.walletimport.watchonly.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.cell.RoundCell
import io.goldstone.blockchain.common.component.cell.roundCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.multichain.AddressType
import io.goldstone.blockchain.module.common.walletimport.watchonly.presenter.WatchOnlyImportPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 2:15 AM
 * @author KaySaith
 */
class WatchOnlyImportFragment : BaseFragment<WatchOnlyImportPresenter>() {

	override val pageTitle: String = ImportMethodText.watchOnly
	private lateinit var attentionView: AttentionTextView
	private lateinit var typeSettings: RoundCell
	private lateinit var nameInput: RoundInput
	private lateinit var addressInput: WalletEditText
	private lateinit var confirmButton: RoundButton
	private var currentType = AddressType.ETHSeries.value
	override val presenter = WatchOnlyImportPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)

			attentionView = AttentionTextView(context)
			attentionView.apply {
				isCenter()
				text = WatchOnlyText.intro
			}.into(this)
			attentionView.setMargins<LinearLayout.LayoutParams> {
				topMargin = 30.uiPX()
			}

			addressInput = WalletEditText(context)
			addressInput.apply {
				hint = WatchOnlyText.enterDescription
			}.into(this)
			addressInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 30.uiPX()
			}

			typeSettings = roundCell().click {
				showWalletTypeDashboard(context) { type ->
					currentType = type
					typeSettings.setTitles(ImportWalletText.walletType, type)
				}
			}

			typeSettings.setTitles(ImportWalletText.walletType, currentType)
			typeSettings.setMargins<LinearLayout.LayoutParams> {
				topMargin = 20.uiPX()
				bottomMargin = 10.uiPX()
			}

			nameInput = roundInput {
				hint = UIUtils.generateDefaultName()
				title = CreateWalletText.name
				horizontalPaddingSize = PaddingSize.gsCard
			}
			nameInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 20.uiPX()
			}

			confirmButton = roundButton {
				setBlueStyle(20.uiPX())
				text = CommonText.startImporting.toUpperCase()
			}.click { button ->
				button.showLoadingStatus()
				presenter.importWatchOnlyWallet(currentType, addressInput, nameInput) {
					launchUI {
						if (it.hasError()) safeShowError(it)
						else activity?.jump<SplashActivity>()
						button.showLoadingStatus(false)
					}
				}
			}

			ExplanationTitle(context).apply {
				text = QAText.whatIsWatchOnlyWallet.setUnderline()
			}.click {
				getParentFragment<ProfileOverlayFragment> {
					presenter.showTargetFragment<WebViewFragment>(
						Bundle().apply {
							putString(ArgumentKey.webViewUrl, WebUrl.whatIsWatchOnly)
							putString(ArgumentKey.webViewName, WalletText.watchOnly)
						}
					)
				}
			}.into(this)
		}
	}

	private fun showWalletTypeDashboard(context: Context, updateCurrentType: (String) -> Unit) {
		val data = arrayListOf(
			AddressType.ETHSeries.value,
			AddressType.BTC.value,
			AddressType.BTCSeriesTest.value,
			AddressType.LTC.value,
			AddressType.BCH.value,
			AddressType.EOS.value,
			AddressType.EOSJungle.value,
			AddressType.EOSKylin.value
		)
		val defaultIndex = data.indexOf(currentType)
		MaterialDialog(context)
			.title(text = "Wallet Type")
			.listItemsSingleChoice(items = data, initialSelection = defaultIndex) { _, _, item ->
				updateCurrentType(item)
			}
			.positiveButton(text = CommonText.confirm)
			.negativeButton(text = CommonText.cancel)
			.show()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<ProfileOverlayFragment>()?.presenter
			?.popFragmentFrom<WatchOnlyImportFragment>()
	}
}