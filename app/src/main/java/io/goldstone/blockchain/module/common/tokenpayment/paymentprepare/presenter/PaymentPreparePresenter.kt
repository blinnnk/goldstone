package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.PasswordError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/5/15 10:19 PM
 * @author KaySaith
 */
class PaymentPreparePresenter(
	override val fragment: PaymentPrepareFragment
) : BasePresenter<PaymentPrepareFragment>() {

	val rootFragment by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setSymbol()
		// 根据入口不同决定是否显示关闭按钮
		rootFragment?.apply {
			if (isFromQuickTransfer) {
				overlayView.header.showCloseButton(false)
			}
		}
	}

	fun getToken(): WalletDetailCellModel? {
		return rootFragment?.token
	}

	fun goToGasEditorFragmentOrTransfer(callback: (GoldStoneError) -> Unit) {
		val count = fragment.getTransferCount()
		val token = getToken()
		if (!token?.contract.isEOS()) fragment.toast(LoadingText.calculateGas)
		if (count == 0.0) callback(TransferError.TradingInputIsEmpty)
		else when {
			/** 准备 BTC 转账需要的参数 */
			token?.contract.isBTC() ->
				prepareBTCPaymentModel(count, fragment.getChangeAddress(), callback)
			/** 准备 LTC 转账需要的参数 */
			token?.contract.isLTC() ->
				prepareLTCPaymentModel(count, fragment.getChangeAddress(), callback)
			/** 准备 BCH 转账需要的参数 */
			token?.contract.isBCH() ->
				prepareBCHPaymentModel(count, fragment.getChangeAddress(), callback)
			/** 准备 EOS 转账需要的参数 */
			token?.contract.isEOSSeries() ->
				transferEOS(
					count,
					getToken()?.contract.orEmpty(),
					callback
				)
			else -> prepareETHSeriesPaymentModel(count, callback)
		}
	}

	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			headerTitle = TokenDetailText.address
			presenter.popFragmentFrom<PaymentPrepareFragment>()
		}
	}

	override fun onFragmentShowFromHidden() {
		rootFragment?.apply {
			overlayView.header.backButton.onClick {
				backEvent(this@apply)
			}
		}
	}

	private fun setSymbol() {
		fragment.setSymbolAndPrice(
			rootFragment?.token?.symbol.orEmpty(),
			rootFragment?.token?.price?.formatCurrency().orEmpty() + " " + SharedWallet.getCurrencyCode()
		)
	}

	companion object {

		fun showGetPrivateKeyDashboard(
			context: Context?,
			@UiThread hold: (privateKey: EOSPrivateKey?, error: GoldStoneError) -> Unit
		) {
			context?.showAlertView(
				TransactionText.confirmTransactionTitle.toUpperCase(),
				TransactionText.confirmTransaction,
				true,
				{
					// User click cancel button
					hold(null, AccountError.None)
				}
			) { passwordInput ->
				if (passwordInput.isNull()) return@showAlertView
				val password = passwordInput!!.text.toString()
				if (password.isNotEmpty()) WalletTable.getCurrentWallet {
					PrivateKeyExportPresenter.getPrivateKey(
						GoldStoneAPI.context,
						SharedAddress.getCurrentEOS(),
						ChainType.EOS,
						password
					) { privateKey, error ->
						if (!privateKey.isNull() && error.isNone())
							hold(EOSPrivateKey(privateKey!!), GoldStoneError.None)
						else hold(null, AccountError.DecryptKeyStoreError)
					}
				} else hold(null, PasswordError.InputIsEmpty)
			}
		}
	}
}