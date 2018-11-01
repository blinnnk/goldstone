package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent

/**
 * @date 26/03/2018 11:06 PM
 * @author KaySaith
 */

class QRCodeFragment : BaseFragment<QRCodePresenter>() {

	override val pageTitle: String = WalletText.qrCode

	private val qrView by lazy { QRView(context!!) }
	override val presenter = QRCodePresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		addView(qrView, LinearLayout.LayoutParams(matchParent, matchParent))
		qrView.showAllButtons()
		setSaveImageEvent()
		setShareImageEvent()
		if (presenter.addressModel?.symbol.equals(CoinSymbol.bch)) {
			convertBCHAddress()
		}
	}

	fun setAddressText(address: String) {
		qrView.setAddressText(address)
	}

	fun setQRImage(bitmap: Bitmap?) {
		qrView.setQRImage(bitmap)
	}

	private var hasConvertedBCH = false

	private fun convertBCHAddress() {
		qrView.showFormattedButton(true)
		qrView.convertEvent = Runnable {
			hasConvertedBCH = !hasConvertedBCH
			val default = presenter.addressModel?.address.orEmpty()
			val net = if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
			val address = if (!hasConvertedBCH) BCHWalletUtils.formattedToLegacy(default, net)
			else {
				if (BCHWalletUtils.isNewCashAddress(default)) default
				else BCHUtil.instance.encodeCashAddressByLegacy(default)
			}
			setAddressText(address)
			setQRImage(QRCodePresenter.generateQRCode(address))
		}
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

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		when (parent) {
			is WalletSettingsFragment -> parent.presenter.popFragmentFrom<QRCodeFragment>()
		}
	}
}