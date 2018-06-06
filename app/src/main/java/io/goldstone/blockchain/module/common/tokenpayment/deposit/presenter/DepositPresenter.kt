package io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.view.DepositFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter

/**
 * @date 2018/5/7 11:41 PM
 * @author KaySaith
 */

class DepositPresenter(
	override val fragment: DepositFragment
) : BasePresenter<DepositFragment>() {

	var qrContent: String = ""

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			fragment.setInputViewDescription(token?.symbol.orEmpty())
		}
		generateQRCode()
		recoveryFragmentHeight()
	}

	fun generateQRCode(amount: Double = 0.0, callback: () -> Unit = {}) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			WalletTable.getCurrentWalletAddress {
				val ethParameter = if (amount == 0.0) "" else "?amount=$amount"
				val tokenParameter = if (amount == 0.0) "" else "?amount=$amount?token=${token?.contract}"
				val content = when (token?.symbol) {
					CryptoSymbol.eth -> "$this$ethParameter"
					else             -> "$this$tokenParameter"
				}
				qrContent = content
				QRCodePresenter.generateQRCode(content).let {
					fragment.setQRImage(it)
					callback()
				}
			}
		}
	}

}