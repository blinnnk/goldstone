package io.goldstone.blinnnk.module.common.tokenpayment.deposit.view

import android.graphics.Bitmap
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.language.AlertText
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.crypto.bitcoincash.BCHUtil
import io.goldstone.blinnnk.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.common.tokenpayment.deposit.presenter.DepositPresenter
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blinnnk.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.qrcodefragment.view.QRView
import kotlinx.coroutines.Dispatchers
import org.bitcoinj.params.MainNetParams
import org.jetbrains.anko.*

/**
 * @date 2018/5/7 11:40 PM
 * @author KaySaith
 */
class DepositFragment : BaseFragment<DepositPresenter>() {

	override val pageTitle: String
		get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol?.symbol.orEmpty()
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
				setAddressText(false) {
					launchUI { qrView.setAddressText(it) }
				}
				qrView.showSaveAndShareButtons()
				setConfirmButtonEvent()
				setShareEvent()
			}

			getParentFragment<TokenDetailOverlayFragment> {
				token?.apply { prepareSymbolPrice(contract) }
				if (token?.contract.isBCH()) {
					showConvertAddressButton()
				}
			}

			inputView.inputTextListener {
				inputView.updateCurrencyValue(symbolPrice)
				if (it.toDoubleOrNull().isNull()) {
					context.alert(AlertText.transferUnvalidInputFormat)
				} else {
					presenter.generateQRCode(if (it.isEmpty()) 0.0 else it.toDouble())
				}
			}
		}
	}

	private var symbolPrice: Double = 0.0
	private fun prepareSymbolPrice(contract: TokenContract) {
		DefaultTokenTable.getCurrentChainToken(contract) {
			symbolPrice = it?.price.orElse(0.0)
		}
	}

	private var hasConvert = false
	private fun showConvertAddressButton() {
		qrView.showFormattedButton(true)
		qrView.convertEvent = Runnable {
			hasConvert = !hasConvert
			setAddressText(hasConvert) {
				launchUI { qrView.setAddressText(it) }
			}
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

	private fun setShareEvent() {
		qrView.shareEvent = Runnable {
			val value = inputView.getValue()
			presenter.generateQRCode(if (value.isEmpty()) 0.0 else value.toDouble()) {
				QRCodePresenter.shareQRImage(this, presenter.qrContent)
			}
		}
	}

	fun setQRImage(bitmap: Bitmap?) {
		qrView.setQRImage(bitmap)
	}

	fun setInputViewDescription(symbol: String) {
		inputView.setHeaderSymbol(symbol, true)
	}

	@WorkerThread
	private fun setAddressText(
		convertBCHAddress: Boolean,
		callback: (value: String) -> Unit
	) = WalletTable.getCurrent(Dispatchers.Main) {
		getParentFragment<TokenDetailOverlayFragment> {
			when {
				token?.contract.isBTC() -> {
					if (SharedValue.isTestEnvironment()) callback(currentBTCSeriesTestAddress)
					else callback(currentBTCAddress)
				}
				token?.contract.isLTC() -> {
					if (SharedValue.isTestEnvironment()) callback(currentBTCSeriesTestAddress)
					else callback(currentLTCAddress)
				}
				token?.contract.isEOSSeries() -> callback(currentEOSAccountName.getCurrent())
				token?.contract.isBCH() -> {
					if (SharedValue.isTestEnvironment()) {
						val bchTestAddress =
							if (convertBCHAddress) BCHUtil.instance.encodeCashAddressByLegacy(currentBTCSeriesTestAddress)
							else currentBTCSeriesTestAddress
						callback(bchTestAddress)
						launchUI { setQRImage(QRCodePresenter.generateQRCode(bchTestAddress)) }
					} else {
						val bchMainnetAddress =
							if (convertBCHAddress) BCHWalletUtils.formattedToLegacy(currentBCHAddress, MainNetParams.get())
							else currentBCHAddress
						callback(bchMainnetAddress)
						launchUI { setQRImage(QRCodePresenter.generateQRCode(bchMainnetAddress)) }
					}
				}
				token?.contract.isETC() -> callback(currentETCAddress)
				else -> callback(currentETHSeriesAddress)
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<TokenDetailOverlayFragment> {
			if (isFromQuickDeposit) {
				presenter.removeSelfFromActivity()
			} else {
				headerTitle = TokenDetailText.tokenDetail
				presenter.popFragmentFrom<DepositFragment>()
			}
		}
	}
}