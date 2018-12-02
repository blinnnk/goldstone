package io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getParentFragment
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.error.*
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.view.PaymentDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/5/15 10:19 PM
 * @author KaySaith
 */
class PaymentDetailPresenter(
	override val fragment: PaymentDetailFragment
) : BasePresenter<PaymentDetailFragment>() {

	val rootFragment by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		// 根据入口不同决定是否显示关闭按钮
		rootFragment?.apply {
			if (isFromQuickTransfer) {
				showCloseButton(false) {}
			}
		}
	}

	fun getToken(): WalletDetailCellModel? {
		return rootFragment?.token
	}

	fun goToGasEditorFragmentOrTransfer(callback: (GoldStoneError) -> Unit) {
		if (!NetworkUtil.hasNetwork(fragment.context)) {
			callback(NetworkError.WithOutNetwork)
		} else {
			val count = fragment.getTransferCount()
			val token = getToken()
			if (count == 0.0) {
				callback(TransferError.TradingInputIsEmpty)
				return
			}
			if (!token?.contract.isEOS()) fragment.toast(LoadingText.calculateGas)
			when {
				/** 准备 BTCSeries 转账需要的参数 */
				token?.contract.isBTCSeries() -> prepareBTCSeriesPaymentModel(
					token?.contract.getChainType(),
					count,
					fragment.getChangeAddress(),
					callback
				)
				/** 准备 EOS 转账需要的参数 */
				token?.contract.isEOSSeries() -> transferEOS(
					count,
					getToken()?.contract.orEmpty(),
					callback
				)
				else -> prepareETHSeriesPaymentModel(count, callback)
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			showBackButton(true) {
				fragment.backEvent()
			}
		}
	}

	companion object {
		// FOR EOS
		fun showGetPrivateKeyDashboard(
			context: Context?,
			cancelEvent: () -> Unit = {},
			confirmEvent: () -> Unit = {},
			@WorkerThread hold: (privateKey: EOSPrivateKey?, error: GoldStoneError) -> Unit
		) = GlobalScope.launch(Dispatchers.Main) {
			Dashboard(context!!) {
				showAlertView(
					TransactionText.confirmTransactionTitle.toUpperCase(),
					TransactionText.confirmTransaction,
					true,
					// User click cancel button
					{
						hold(null, AccountError.None)
						cancelEvent()
					}
				) { passwordInput ->
					confirmEvent()
					val password = passwordInput?.text?.toString()
					if (password?.isNotEmpty() == true) PrivateKeyExportPresenter.getPrivateKey(
						SharedAddress.getCurrentEOS(),
						ChainType.EOS,
						password
					) { privateKey, error ->
						if (!privateKey.isNullOrEmpty() && error.isNone()) hold(EOSPrivateKey(privateKey), error)
						else hold(null, AccountError.WrongPassword)
					} else hold(null, PasswordError.InputIsEmpty)
				}
			}
		}
	}
}