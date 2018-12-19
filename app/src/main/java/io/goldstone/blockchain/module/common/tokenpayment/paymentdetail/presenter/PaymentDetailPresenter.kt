package io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getDecimalCount
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.error.*
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.utils.KeystoreInfo
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.view.PaymentDetailFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.fingerprintsetting.view.FingerprintSettingFragment
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
		if (!NetworkUtil.hasNetwork()) {
			callback(NetworkError.WithOutNetwork)
		} else {
			val count = fragment.getTransferCount()
			val token = getToken()
			val plainString = count.toBigDecimal().toPlainString()
			val currentDecimal = plainString.substring(0, plainString.lastIndex).getDecimalCount().orZero()
			if (count == 0.0) {
				callback(TransferError.TradingInputIsEmpty)
				return
			} else if (currentDecimal > token?.contract?.decimal.orZero()) {
				callback(TransferError.IncorrectDecimal)
				return
			} else if (!token?.contract.isEOS()) fragment.toast(LoadingText.calculateGas)
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

		fun getPrivatekey(
			context: Context,
			chainType: ChainType,
			cancelEvent: () -> Unit = {},
			confirmEvent: () -> Unit = {},
			hold: (privateKey: String?, error: GoldStoneError) -> Unit
		) {
			if (SharedWallet.hasFingerprint()) getPrivatekeyByFingerprint(context, chainType, hold)
			else getPrivatekeyByPassword(context, chainType, cancelEvent, confirmEvent, hold)
		}

		private fun getPrivatekeyByFingerprint(
			context: Context,
			chainType: ChainType,
			hold: (privateKey: String?, error: GoldStoneError) -> Unit
		) = GlobalScope.launch(Dispatchers.Main) {
			FingerprintSettingFragment.showFingerprintDashboard(
				context,
				true,
				usePasswordEvent = {
					PaymentDetailPresenter.getPrivatekeyByPassword(context, ChainType.EOS, hold = hold)
				}
			) { cipher ->
				load {
					WalletTable.dao.getEncryptFingerprintKey()
				} then { encryptKey ->
					val decryptKey =
						if (cipher.isNotNull()) JavaKeystoreUtil(KeystoreInfo.isFingerPrinter(cipher)).decryptData(encryptKey!!)
						else JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(encryptKey!!)
					PrivateKeyExportPresenter.getPrivateKeyByRootData(
						chainType.getContract().getAddress(false),
						chainType,
						decryptKey
					) {
						hold(it, GoldStoneError.None)
					}
				}
			}
		}

		private fun getPrivatekeyByPassword(
			context: Context,
			chainType: ChainType,
			cancelEvent: () -> Unit = {},
			confirmEvent: () -> Unit = {},
			@WorkerThread hold: (privateKey: String?, error: GoldStoneError) -> Unit
		) = GlobalScope.launch(Dispatchers.Main) {
			Dashboard(context) {
				showAlertView(
					TransactionText.confirmTransactionTitle.toUpperCase(),
					TransactionText.confirmTransaction,
					true,
					cancelAction = {
						GlobalScope.launch(Dispatchers.Default) {
							hold(null, AccountError.None)
							launchUI(cancelEvent)
						}
					}
				) { passwordInput ->
					confirmEvent()
					val password = passwordInput?.text?.toString()
					if (password?.isNotEmpty() == true) {
						PrivateKeyExportPresenter.getPrivateKey(
							chainType.getContract().getAddress(false),
							chainType,
							password
						) { privateKey, error ->
							if (privateKey.isNotNull() && error.isNone()) hold(privateKey, error)
							else hold(null, error)
						}
					} else hold(null, PasswordError.InputIsEmpty)
				}
			}
		}
	}
}