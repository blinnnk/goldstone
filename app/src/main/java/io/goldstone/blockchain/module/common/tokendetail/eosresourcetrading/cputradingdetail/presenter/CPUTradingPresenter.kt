package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.presenter

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.accountregister.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.error.GoldStoneError
import io.goldstone.blockchain.crypto.error.TransferError
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.network.eos.EOSBandWidthTransaction
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.view.CPUTradingFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter
import org.jetbrains.anko.support.v4.toast

/**
 * @author KaySaith
 * @date  2018/09/18
 */
class CPUTradingPresenter(
	override val fragment: CPUTradingFragment
) : BaseTradingPresenter(fragment) {

	override fun refundOrSellConfirmEvent(callback: (Boolean) -> Unit) {
		refundResource(fragment, TradingType.CPU, callback)
	}

	override fun gainConfirmEvent(callback: (isSuccessful: Boolean) -> Unit) {
		delegateResource(fragment, TradingType.CPU, callback)
	}

	companion object {

		fun delegateResource(
			fragment: BaseTradingFragment,
			tradingType: TradingType,
			callback: (isSuccessful: Boolean) -> Unit
		) {
			val stakeType = StakeType.Delegate
			fragment.showLoading(true, stakeType)
			val currentAccountName = Config.getCurrentEOSName()
			// first is name, second is eos amount
			val inputValue = fragment.getInputValue(stakeType)
			isValidInputValue(fragment.context, inputValue) { isValidInput ->
				if (!isValidInput) {
					callback(false)
					return@isValidInputValue
				}
				PaymentPreparePresenter.checkBalanceIsEnoughOrElse(
					currentAccountName,
					CoinSymbol.getEOS(),
					inputValue.second
				) { hasEnoughBalance ->
					if (hasEnoughBalance) getPrivateKeyAndSendTransaction(
						fragment.context,
						inputValue.first,
						inputValue.second,
						tradingType,
						stakeType
					) {
						completeEvent(fragment, callback)
					} else {
						callback(false)
						fragment.context.alert(TransferError.balanceIsNotEnough.content)
					}
				}
			}
		}

		fun refundResource(
			fragment: BaseTradingFragment,
			tradingType: TradingType,
			callback: (isSuccessful: Boolean) -> Unit
		) {
			val stakeType = StakeType.Refund
			fragment.showLoading(true, stakeType)
			val currentAccountName = Config.getCurrentEOSName()
			// first is name, second is eos amount
			val inputValue = fragment.getInputValue(stakeType)
			isValidInputValue(fragment.context, inputValue) { isValidInput ->
				if (!isValidInput) {
					callback(false)
					return@isValidInputValue
				}
				PaymentPreparePresenter.checkResourceIsEnoughOrElse(
					currentAccountName,
					inputValue.second,
					tradingType,
					{
						callback(false)
						LogUtil.error("refundOrSellEvent", it)
					}
				) { hasEnoughBalance ->
					if (hasEnoughBalance) getPrivateKeyAndSendTransaction(
						fragment.context,
						inputValue.first,
						inputValue.second,
						tradingType,
						stakeType
					) {
						completeEvent(fragment, callback)
					} else {
						callback(false)
						fragment.context.alert(TransferError.balanceIsNotEnough.content)
					}
				}
			}
		}

		private fun completeEvent(
			fragment: BaseTradingFragment,
			callback: (isSuccessful: Boolean) -> Unit
		) {
			// 清空输入框里面的值
			fragment.clearInputValue()
			// 成功提示
			fragment.toast(CommonText.succeed)
			// 更新数据库数据并且更新界面的数据
			fragment.presenter.updateLocalDataAndUI()
			callback(true)
		}

		private fun isValidInputValue(
			context: Context?,
			inputValue: Pair<String, Double>,
			callback: (isValid: Boolean) -> Unit
		) {
			// 检查用户名是否正确
			if (!EOSWalletUtils.isValidAccountName(inputValue.first)) {
				context.alert("Wrong EOS Account Name Formatted, Please Check And Re-Enter")
				callback(false)
				return
			} else if (inputValue.second == 0.0) {
				context.alert("Please Enter EOS Amount That You Decide To Buy")
				callback(false)
				return
			} else callback(true)
		}

		private fun getPrivateKeyAndSendTransaction(
			context: Context?,
			toAccountName: String,
			refundCount: Double,
			tradingType: TradingType,
			stakeType: StakeType,
			callback: (isSuccessful: Boolean) -> Unit
		) {
			PaymentPreparePresenter.showGetPrivateKeyDashboard(context) { privateKey, error ->
				if (privateKey.isNull()) {
					context.alert(error.message)
					callback(false)
				} else stakeResource(
					privateKey!!,
					toAccountName,
					refundCount,
					tradingType,
					stakeType,
					false,
					ExpirationType.FiveMinutes,
					{
						callback(false)
						context.alert(it.message)
					}
				) {
					callback(true)
				}
			}
		}

		/**
		 * 如果是 `StakeType.Refund` 那么 `Transfer` 的参数是无用的, 已经在底层逻辑增加了判断.
		 * 这里可以传默认值 `false`
		 */
		private fun stakeResource(
			privateKey: EOSPrivateKey,
			toAccountName: String,
			delegateEOSCount: Double,
			tradingType: TradingType,
			stakeType: StakeType,
			isTransfer: Boolean,
			expirationType: ExpirationType,
			errorCallback: (GoldStoneError) -> Unit,
			@UiThread hold: (EOSResponse) -> Unit
		) {
			EOSBandWidthTransaction(
				Config.getEOSCurrentChain(),
				EOSAuthorization(Config.getCurrentEOSName(), EOSActor.Active),
				toAccountName,
				delegateEOSCount.toEOSUnit(),
				tradingType,
				stakeType,
				isTransfer,
				expirationType
			).send(
				privateKey,
				errorCallback,
				hold
			)
		}
	}
}