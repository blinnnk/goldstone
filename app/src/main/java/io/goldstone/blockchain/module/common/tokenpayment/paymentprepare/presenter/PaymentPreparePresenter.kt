package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.error.TransferError
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
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
				TokenContract(getToken()?.contract).isBTC() -> prepareBTCPaymentModel(
					count, fragment.getChangeAddress()
				) { error ->
					if (error != TransferError.None) fragment.context?.alert(error.content)
					// 恢复 Loading 按钮
					callback()
				}
				/** 准备 LTC 转账需要的参数 */
				TokenContract(getToken()?.contract).isLTC() -> prepareLTCPaymentModel(
					count, fragment.getChangeAddress()
				) { error ->
					if (error != TransferError.None) fragment.context?.alert(error.content)
					// 恢复 Loading 按钮
					callback()
				}
				/** 准备 BCH 转账需要的参数 */
				TokenContract(getToken()?.contract).isBCH() -> prepareBCHPaymentModel(
					count, fragment.getChangeAddress()
				) { error ->
					if (error != TransferError.None) fragment.context?.alert(error.content)
					// 恢复 Loading 按钮
					callback()
				}
				/** 准备 EOS 转账需要的参数 */
				TokenContract(getToken()?.contract).isEOS() -> transferEOS(count, getToken()?.symbol!!) { error ->
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
}