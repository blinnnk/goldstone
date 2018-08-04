package io.goldstone.blockchain.module.common.tokenpayment.deposit.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter.DepositPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRView
import org.jetbrains.anko.*

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
				qrView.showSaveAndShareButtons()
				setConfirmButtonEvent()
				setShareEvent()
			}
			prepareSymbolPrice()
			
			inputView.inputTextListener {
				inputView.updateCurrencyValue(symbolPrice)
				if (it.toDoubleOrNull().isNull()) {
					context.alert(AlertText.transferUnvalidInputFromat)
				} else {
					presenter.generateQRCode(if (it.isEmpty()) 0.0 else it.toDouble())
				}
			}
		}
	}
	
	private var symbolPrice: Double = 0.0
	private fun prepareSymbolPrice() {
		getParentFragment<TokenDetailOverlayFragment> {
			DefaultTokenTable.getCurrentChainToken(token?.contract!!) {
				symbolPrice = it?.price.orElse(0.0)
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
	
	private fun setAddressText() {
		WalletTable.getCurrentWallet {
			getParentFragment<TokenDetailOverlayFragment> {
				when {
					token?.symbol.equals(CryptoSymbol.btc, true) -> {
						if (Config.isTestEnvironment())
							qrView.setAddressText(currentBTCTestAddress)
						else
							qrView.setAddressText(currentBTCAddress)
					}
					
					token?.symbol.equals(CryptoSymbol.etc, true) ->
						qrView.setAddressText(currentETCAddress)
					else -> qrView.setAddressText(currentETHAndERCAddress)
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