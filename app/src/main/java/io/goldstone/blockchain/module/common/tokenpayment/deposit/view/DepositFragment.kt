package io.goldstone.blockchain.module.common.tokenpayment.deposit.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter.DepositPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRView
import org.bitcoinj.params.MainNetParams
import org.jetbrains.anko.*

/**
 * @date 2018/5/7 11:40 PM
 * @author KaySaith
 */
class DepositFragment : BaseFragment<DepositPresenter>() {

	override val pageTitle: String get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol.orEmpty()
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
			setAddressText(hasConvert)
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

	private fun setAddressText(convertBCHAddress: Boolean = false) {
		WalletTable.getCurrentWallet {
			getParentFragment<TokenDetailOverlayFragment> {
				when {
					token?.contract.isBTC() -> {
						if (SharedValue.isTestEnvironment())
							qrView.setAddressText(currentBTCSeriesTestAddress)
						else qrView.setAddressText(currentBTCAddress)
					}

					token?.contract.isLTC() -> {
						if (SharedValue.isTestEnvironment())
							qrView.setAddressText(currentBTCSeriesTestAddress)
						else qrView.setAddressText(currentLTCAddress)
					}

					token?.contract.isEOS() -> {
						qrView.setAddressText(currentEOSAccountName.getCurrent())
					}

					token?.contract.isBCH() -> {
						if (SharedValue.isTestEnvironment()) {
							val bchTestAddress =
								if (convertBCHAddress) BCHUtil.instance.encodeCashAddressByLegacy(currentBTCSeriesTestAddress)
								else currentBTCSeriesTestAddress
							qrView.setAddressText(bchTestAddress)
							setQRImage(QRCodePresenter.generateQRCode(bchTestAddress))
						} else {
							val bchMainnetAddress =
								if (convertBCHAddress) BCHWalletUtils.formattedToLegacy(currentBCHAddress, MainNetParams.get())
								else currentBCHAddress
							qrView.setAddressText(bchMainnetAddress)
							setQRImage(QRCodePresenter.generateQRCode(bchMainnetAddress))
						}
					}

					token?.contract.isETC() ->
						qrView.setAddressText(currentETCAddress)
					else -> qrView.setAddressText(currentETHSeriesAddress)
				}
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
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