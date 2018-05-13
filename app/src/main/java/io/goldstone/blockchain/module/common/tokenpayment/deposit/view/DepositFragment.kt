package io.goldstone.blockchain.module.common.tokenpayment.deposit.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter.DepositPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.scrollView

/**
 * @date 2018/5/7 11:40 PM
 * @author KaySaith
 */

class DepositFragment : BaseFragment<DepositPresenter>() {

	private val inputView by lazy { DepositInputView(context!!) }
	private val qrView by lazy { QRView(context!!) }

	override val presenter = DepositPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			relativeLayout {
				lparams(matchParent, matchParent)
				inputView.into(this)
				qrView.into(this)
				qrView.setMargins<RelativeLayout.LayoutParams> {
					topMargin = 170.uiPX()
				}
				setAddressText()
				setConfirmButtonEvent()
			}
			inputView.inputTextListener()
		}
	}

	private fun setConfirmButtonEvent() {
		qrView.saveQRImageEvent = Runnable {
			val value = inputView.getValue()
			presenter.generateQRCode(if (value.isEmpty()) 0.0 else value.toDouble()) {
				QRCodePresenter.saveQRCodeImageToAlbum(presenter.qrContent, this)
			}
		}
	}

	fun setQRImage(bitmap: Bitmap?) {
		qrView.setQRImage(bitmap)
	}

	fun setInputViewDescription(symbol: String) {
		inputView.setHeaderSymbol(symbol)
	}

	private fun setAddressText() {
		WalletTable.getCurrentWalletAddress {
			qrView.setAddressText(this)
		}
	}
}