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
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import org.jetbrains.anko.runOnUiThread
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

	fun goToGasEditorFragmentOrTransfer(callback: () -> Unit) {
		val count = fragment.getTransferCount()
		if (count == 0.0) {
			fragment.context.alert(AlertText.emptyTransferValue)
			callback()
		} else {
			fragment.toast(LoadingText.calculateGas)
			when {
				/** 准备 BTC 转账需要的参数 */
				getToken()?.contract.isBTC() -> prepareBTCPaymentModel(
					count, fragment.getChangeAddress()
				) { error ->
					if (!error.isNone()) fragment.context?.alert(error.message)
					// 恢复 Loading 按钮
					callback()
				}
				/** 准备 LTC 转账需要的参数 */
				getToken()?.contract.isLTC() -> prepareLTCPaymentModel(
					count, fragment.getChangeAddress()
				) { error ->
					if (error != GoldStoneError.None) fragment.context?.alert(error.message)
					// 恢复 Loading 按钮
					callback()
				}
				/** 准备 BCH 转账需要的参数 */
				getToken()?.contract.isBCH() -> prepareBCHPaymentModel(
					count, fragment.getChangeAddress()
				) { error ->
					if (!error.isNone()) fragment.context?.alert(error.message)
					// 恢复 Loading 按钮
					callback()
				}
				/** 准备 EOS 转账需要的参数 */
				getToken()?.contract.isEOS() -> transferEOS(count, CoinSymbol(getToken()?.symbol)) { error ->
					when (error) {
						TransferError.BalanceIsNotEnough -> fragment.context.alert(AlertText.balanceNotEnough)
						TransferError.IncorrectDecimal -> fragment.context?.alert(AlertText.transferWrongDecimal)
						else -> callback()
					}
					// 恢复 Loading 按钮
					callback()
				}
				else -> prepareETHERC20ETCPaymentModel(count, callback)
			}
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
			rootFragment?.token?.price?.formatCurrency().orEmpty() + " " + Config.getCurrencyCode()
		)
	}

	companion object {

		fun showGetPrivateKeyDashboard(
			context: Context?,
			@UiThread hold: (privateKey: EOSPrivateKey?, error: GoldStoneError) -> Unit
		) {
			context?.showAlertView(
				"Transfer EOS Token",
				"prepare to transfer eos token, now you should enter your password",
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
						Config.getCurrentEOSAddress(),
						ChainType.EOS,
						password
					) {
						if (!isNullOrEmpty()) hold(EOSPrivateKey(this!!), GoldStoneError.None)
						else hold(null, AccountError.DecryptKeyStoreError)
					}
				} else hold(null, PasswordError.InputIsEmpty)
			}
		}

		// 检查的是对应 `EOS` 个数的余额
		fun checkResourceIsEnoughOrElse(
			account: EOSAccount,
			checkCount: Double,
			tradingType: TradingType,
			errorCallback: (GoldStoneError) -> Unit,
			@UiThread hold: (GoldStoneError) -> Unit
		) {
			EOSAPI.getAccountInfo(
				account,
				errorCallback
			) {
				val isEnough = when (tradingType) {
					TradingType.CPU -> it.cpuWeight > checkCount.toEOSUnit()
					TradingType.NET -> it.netWeight > checkCount.toEOSUnit()
					TradingType.RAM -> true // 待处理内存的 `EOS` 余额判断　
				}
				GoldStoneAPI.context.runOnUiThread {
					hold(if (isEnough) GoldStoneError.None else TransferError.BalanceIsNotEnough)
				}
			}
		}
	}
}