package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.presenter

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
		val stakeType = StakeType.Refund
		fragment.showLoading(true, stakeType)
		val currentAccountName = Config.getCurrentEOSName()
		// first is name, second is eos amount
		val inputValue = fragment.getInputValue(stakeType)
		isValidInputValue(inputValue) { isValidInput ->
			if (!isValidInput) {
				callback(false)
				return@isValidInputValue
			}
			PaymentPreparePresenter.checkResourceIsEnoughOrElse(
				currentAccountName,
				inputValue.second,
				TradingType.CPU,
				{
					callback(false)
					LogUtil.error("refundOrSellEvent", it)
				}
			) { hasEnoughBalance ->
				if (hasEnoughBalance)
					getPrivateKeyAndSendTransaction(inputValue.first, inputValue.second, callback)
				else {
					callback(false)
					fragment.context.alert(TransferError.balanceIsNotEnough.content)
				}
			}
		}
	}

	override fun gainConfirmEvent(callback: (isSuccessful: Boolean) -> Unit) {
		val stakeType = StakeType.Delegate
		fragment.showLoading(true, stakeType)
		val currentAccountName = Config.getCurrentEOSName()
		// first is name, second is eos amount
		val inputValue = fragment.getInputValue(stakeType)
		isValidInputValue(inputValue) { isValidInput ->
			if (!isValidInput) {
				callback(false)
				return@isValidInputValue
			}
			PaymentPreparePresenter.checkBalanceIsEnoughOrElse(
				currentAccountName,
				CoinSymbol.getEOS(),
				inputValue.second
			) { hasEnoughBalance ->
				if (hasEnoughBalance)
					getPrivateKeyAndSendTransaction(inputValue.first, inputValue.second, callback)
				else {
					callback(false)
					fragment.context.alert(TransferError.balanceIsNotEnough.content)
				}
			}
		}
	}

	private fun getPrivateKeyAndSendTransaction(
		toAccountName: String,
		refundCount: Double,
		callback: (isSuccessful: Boolean) -> Unit
	) {
		PaymentPreparePresenter.showGetPrivateKeyDashboard(fragment.context) { privateKey, error ->
			if (privateKey.isNull()) {
				fragment.context.alert(error.message)
				callback(false)
			} else stakeCPU(
				privateKey!!,
				toAccountName,
				refundCount,
				StakeType.Delegate,
				false,
				ExpirationType.FiveMinutes,
				{
					callback(false)
					fragment.context.alert(it.message)
				}
			) {
				// 更新数据库数据并且更新界面的数据
				updateLocalResourceData { setUsageValue() }
				// 成功提示
				fragment.toast(CommonText.succeed)
				// 清空输入框里面的值
				fragment.clearInputValue()
				callback(true)
				System.out.println("fuck $it")
			}
		}
	}

	/**
	 * 如果是 `StakeType.Refund` 那么 `Transfer` 的参数是无用的, 已经在底层逻辑增加了判断.
	 * 这里可以传默认值 `false`
	 */
	private fun stakeCPU(
		privateKey: EOSPrivateKey,
		toAccountName: String,
		delegateEOSCount: Double,
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
			TradingType.CPU,
			stakeType,
			isTransfer,
			expirationType
		).send(
			privateKey,
			errorCallback,
			hold
		)
	}

	private fun isValidInputValue(inputValue: Pair<String, Double>, callback: (isValid: Boolean) -> Unit) {
		// 检查用户名是否正确
		if (!EOSWalletUtils.isValidAccountName(inputValue.first)) {
			fragment.context.alert("Wrong EOS Account Name Formatted, Please Check And Re-Enter")
			callback(false)
			return
		} else if (inputValue.second == 0.0) {
			fragment.context.alert("Please Enter EOS Amount That You Decide To Buy")
			callback(false)
			return
		} else callback(true)
	}
}