package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
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
	private val qrView by lazy { QRView(context!!) }
	override val presenter = QRCodePresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		addView(qrView, LinearLayout.LayoutParams(matchParent, matchParent))
		qrView.showAllButtons()
		setSaveImageEvent()
		setShareImageEvent()
		if (presenter.addressModel?.symbol.equals(CryptoSymbol.bch)) {
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
			val net = if (Config.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
			val address = if (hasConvertedBCH) BCHWalletUtils.formattedToLegacy(default, net)
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