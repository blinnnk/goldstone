package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter

import android.content.Context
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.error.AccountError
import io.goldstone.blockchain.crypto.error.GoldStoneError
import io.goldstone.blockchain.crypto.error.TransferError
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSBandWidthTransaction
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter
import org.jetbrains.anko.support.v4.toast


/**
 * @author KaySaith
 * @date  2018/09/18
 */
open class BaseTradingPresenter(
	override val fragment: BaseTradingFragment
) : BasePresenter<BaseTradingFragment>() {

	open fun gainConfirmEvent(callback: (GoldStoneError) -> Unit) {
		delegateResource(fragment, fragment.tradingType, callback)
	}

	open fun refundOrSellConfirmEvent(callback: (GoldStoneError) -> Unit) {
		refundResource(fragment, fragment.tradingType, callback)
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setUsageValue()
	}

	private fun setUsageValue() {
		EOSAccountTable.getAccountByName(Config.getCurrentEOSName()) { account ->
			when (fragment.tradingType) {
				TradingType.CPU -> {
					val cpuEOSValue = "${account?.cpuWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableCPU = account?.cpuLimit?.max.orZero() - account?.cpuLimit?.used.orZero()
					fragment.setProcessUsage(cpuEOSValue, availableCPU, account?.cpuLimit?.max.orZero())
				}
				TradingType.NET -> {
					val netEOSValue = "${account?.netWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableNET = account?.netLimit?.max.orZero() - account?.netLimit?.used.orZero()
					fragment.setProcessUsage(netEOSValue, availableNET, account?.netLimit?.max.orZero())
				}
				TradingType.RAM -> {
					val availableRAM = account?.ramQuota.orZero() - account?.ramUsed.orZero()
					fragment.setProcessUsage(CommonText.calculating, availableRAM, account?.ramQuota.orZero())
				}
			}
		}
	}

	private fun buyRam(
		tradingType: TradingType,
		callback: (GoldStoneError) -> Unit
	) {
		prepareTransaction(fragment, StakeType.BuyRam) { fragment, accountName, toAccountName, count, stakeType, error ->
			if (!error.isNone()) {
				callback(error)
				return@prepareTransaction
			}
			PaymentPreparePresenter.checkBalanceIsEnoughOrElse(
				accountName,
				CoinSymbol.getEOS(),
				count
			) { hasEnoughBalance ->
				if (!hasEnoughBalance) {
					callback(TransferError.BalanceIsNotEnough)
					return@checkBalanceIsEnoughOrElse
				}
				getPrivateKeyAndSendTransaction(
					fragment.context,
					fragment.isSelectedTransfer(stakeType),
					toAccountName,
					count,
					tradingType,
					stakeType
				) {
					completeEvent(fragment, callback)
				}
			}
		}

	}

	private fun <T : BaseTradingFragment> delegateResource(
		fragment: T,
		tradingType: TradingType,
		callback: (GoldStoneError) -> Unit
	) {
		val stakeType = StakeType.Delegate
		fragment.showLoading(true, stakeType)
		val currentAccountName = Config.getCurrentEOSName()
		// first is name, second is eos amount
		val inputValue = fragment.getInputValue(stakeType)
		isValidInputValue(fragment.context, inputValue) { error ->
			if (!error.isNone()) {
				callback(error)
				return@isValidInputValue
			}
			PaymentPreparePresenter.checkBalanceIsEnoughOrElse(
				currentAccountName,
				CoinSymbol.getEOS(),
				inputValue.second
			) { hasEnoughBalance ->
				if (hasEnoughBalance) getPrivateKeyAndSendTransaction(
					fragment.context,
					fragment.isSelectedTransfer(stakeType),
					inputValue.first,
					inputValue.second,
					tradingType,
					stakeType
				) {
					completeEvent(fragment, callback)
				} else {
					callback(TransferError.BalanceIsNotEnough)
				}
			}
		}
	}

	private fun <T : BaseTradingFragment> refundResource(
		fragment: T,
		tradingType: TradingType,
		callback: (GoldStoneError) -> Unit
	) {
		val stakeType = StakeType.Refund
		fragment.showLoading(true, stakeType)
		val currentAccountName = Config.getCurrentEOSName()
		// first is name, second is eos amount
		val inputValue = fragment.getInputValue(stakeType)
		isValidInputValue(fragment.context, inputValue) { error ->
			if (!error.isNone()) {
				callback(error)
				return@isValidInputValue
			}
			PaymentPreparePresenter.checkResourceIsEnoughOrElse(
				currentAccountName,
				inputValue.second,
				tradingType,
				callback
			) { transferError ->
				if (transferError.isNone()) getPrivateKeyAndSendTransaction(
					fragment.context,
					false, // 释放 `bandWidth` 不用传递这个字段
					inputValue.first,
					inputValue.second,
					tradingType,
					stakeType
				) {
					if (it.isNone()) completeEvent(fragment, callback)
					else callback(it)
				} else {
					callback(TransferError.BalanceIsNotEnough)
				}
			}
		}
	}

	private fun completeEvent(
		fragment: BaseTradingFragment,
		callback: (GoldStoneError) -> Unit
	) {
		// 清空输入框里面的值
		fragment.clearInputValue()
		// 成功提示
		fragment.toast(CommonText.succeed)
		// 更新数据库数据并且更新界面的数据
		fragment.presenter.updateLocalDataAndUI()
		callback(GoldStoneError.None)
	}

	private fun isValidInputValue(
		context: Context?,
		inputValue: Pair<String, Double>,
		callback: (GoldStoneError) -> Unit
	) {
		// 检查用户名是否正确
		if (!EOSWalletUtils.isValidAccountName(inputValue.first)) {
			context.alert("Wrong EOS Account Name Formatted, Please Check And Re-Enter")
			callback(AccountError.InvalidAccountName)
			return
		} else if (inputValue.second == 0.0) {
			context.alert("Please Enter EOS Amount That You Decide To Buy")
			callback(TransferError.TradingInputIsEmpty)
			return
		} else callback(GoldStoneError.None)
	}

	private fun getPrivateKeyAndSendTransaction(
		context: Context?,
		isTransfer: Boolean,
		toAccountName: String,
		stakeCount: Double,
		tradingType: TradingType,
		stakeType: StakeType,
		callback: (GoldStoneError) -> Unit
	) {
		PaymentPreparePresenter.showGetPrivateKeyDashboard(context) { privateKey, error ->
			if (privateKey.isNull()) {
				// 用户主动取消输入密码也会到这里
				if (error != GoldStoneError.None) context.alert(error.message)
				callback(AccountError.None)
			} else EOSBandWidthTransaction(
				Config.getEOSCurrentChain(),
				EOSAuthorization(Config.getCurrentEOSName(), EOSActor.Active),
				toAccountName,
				stakeCount.toEOSUnit(),
				tradingType,
				stakeType,
				isTransfer,
				ExpirationType.FiveMinutes
			).send(
				privateKey!!,
				callback
			) {
				// TODO 用 Response 做点什么
				callback(AccountError.None)
			}
		}
	}

	private fun updateLocalDataAndUI() {
		val currentAccountName = Config.getCurrentEOSName()
		EOSAPI.getAccountInfoByName(
			currentAccountName,
			{ LogUtil.error("updateLocalResourceData", it) }
		) { newData ->
			EOSAccountTable.getAccountByName(currentAccountName, false) { localData ->
				localData?.let { local ->
					GoldStoneDataBase.database.eosAccountDao().update(newData.apply { this.id = local.id })
					setUsageValue()
				}
			}
		}
	}

	private fun <T : BaseTradingFragment> prepareTransaction(
		fragment: T,
		stakeType: StakeType,
		hold: (
			fragment: T,
			accountName: String,
			toAccountName: String,
			count: Double,
			stakeType: StakeType,
			error: GoldStoneError
		) -> Unit
	) {
		fragment.showLoading(true, stakeType)
		val currentAccountName = Config.getCurrentEOSName()
		// first is name, second is eos amount
		val inputValue = fragment.getInputValue(stakeType)
		isValidInputValue(fragment.context, inputValue) { isValidInput ->
			hold(
				fragment,
				currentAccountName,
				fragment.getInputValue(stakeType).first,
				fragment.getInputValue(stakeType).second,
				stakeType,
				isValidInput
			)
		}
	}
}