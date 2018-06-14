package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent

/**
 * @date 26/03/2018 11:06 PM
 * @author KaySaith
 */

class QRCodeFragment : BaseFragment<QRCodePresenter>() {

	private val qrView by lazy { QRView(context!!) }

	override val presenter = QRCodePresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		addView(qrView, LinearLayout.LayoutParams(matchParent, matchParent))
		qrView.showAllButtons()
		setSaveImageEvent()
		setShareImageEvent()
	}

	fun setAddressText(address: String) {
		qrView.setAddressText(address)
	}

	fun setQRImage(bitmap: Bitmap?) {
		qrView.setQRImage(bitmap)
	}

	private fun setSaveImageEvent() {
		qrView.saveQRImageEvent = Runnable {
			QRCodePresenter.saveQRCodeImageToAlbum(qrView.getAddress(), this)
		}
	}

	private fun setShareImageEvent() {
		qrView.shareEvent = Runnable {
			QRCodePresenter.shareQRImage(this, qrView.getAddress())
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.walletSettings
			presenter.showWalletSettingListFragment()
		}
	}
}