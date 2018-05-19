package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletaddingmethod.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletaddingmethod.presenter.WalletAddingMethodPresenter
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view.WalletManagementFragment
import org.jetbrains.anko.*

/**
 * @date 18/04/2018 9:40 PM
 * @author KaySaith
 */

class WalletAddingMethodFragment : BaseFragment<WalletAddingMethodPresenter>() {

	private val createButton by lazy { RoundIcon(context!!) }
	private val importButton by lazy { RoundIcon(context!!) }

	override val presenter = WalletAddingMethodPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		val contentWidth = (ScreenSize.Width - PaddingSize.device * 2) / 2
		linearLayout {

			padding = PaddingSize.device
			y += 30.uiPX()

			verticalLayout {
				lparams(contentWidth, wrapContent)
				gravity = Gravity.CENTER
				createButton.apply {
					iconSize = 100.uiPX()
					iconColor = Spectrum.darkBlue
					src = R.drawable.create_wallet_icon
				}.click { presenter.showCreateWalletFragment() }.into(this)
				textView {
					text = CreateWalletText.create
					gravity = Gravity.CENTER
					textColor = GrayScale.gray
					typeface = GoldStoneFont.heavy(context)
				}.lparams(matchParent, 50.uiPX())
			}

			verticalLayout {
				lparams(contentWidth, wrapContent)
				gravity = Gravity.CENTER
				importButton.apply {
					iconSize = 100.uiPX()
					iconColor = Spectrum.darkBlue
					src = R.drawable.wallet_icon
				}.click {
					NetworkUtil.hasNetworkWithAlert(context, AlertText.importWalletNetwork)
					presenter.showImportWalletFragment()
				}.into(this)
				textView {
					text = ImportWalletText.importWallet
					gravity = Gravity.CENTER
					textColor = GrayScale.gray
					typeface = GoldStoneFont.heavy(context)
				}.lparams(matchParent, 50.uiPX())
			}
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		getParentFragment<WalletManagementFragment> {
			overlayView.header.showAddButton(false)
		}
	}

	override fun setBackEvent(
		activity: MainActivity,
		parent: Fragment?
	) {
		getParentFragment<WalletManagementFragment> {
			headerTitle = CurrentWalletText.Wallets
			presenter.popFragmentFrom<WalletAddingMethodFragment>()
		}
	}

}