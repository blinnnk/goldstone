package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.isValidDecimal
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSBandWidthTransaction
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSBuyRamTransaction
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSSellRamTransaction
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/09/18
 */
open class BaseTradingPresenter(
	override val fragment: BaseTradingFragment
) : BasePresenter<BaseTradingFragment>() {

	open fun gainConfirmEvent(callback: (response: EOSResponse?, error: GoldStoneError) -> Unit) {
		if (fragment.tradingType == TradingType.RAM)
			with(fragment) {
				buyRam(
					context!!,
					getInputValue(StakeType.BuyRam).first,
					getInputValue(StakeType.BuyRam).second,
					callback
				)
			}
		else fragment.stakeResource(fragment.tradingType, StakeType.Delegate, callback)
	}

	open fun refundOrSellConfirmEvent(callback: (response: EOSResponse?, error: GoldStoneError) -> Unit) {
		if (fragment.tradingType == TradingType.RAM) with(fragment) {
			val sellAmount = getInputValue(StakeType.SellRam).second
			if (sellAmount.toString().substringBefore(".").toLongOrNull().isNull()) {
				callback(null, TransferError.InvalidRAMNumber)
			} else sellRAM(
				context!!,
				sellAmount.toLong(),
				callback
			)
		}
		else fragment.stakeResource(fragment.tradingType, StakeType.Refund, callback)
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		GlobalScope.launch(Dispatchers.Default) {
			fragment.setUsageValue()
		}
	}

	@WorkerThread
	private fun BaseTradingFragment.setUsageValue() {
		val accountDao =
			GoldStoneDataBase.database.eosAccountDao()
		val account =
			accountDao.getAccount(SharedAddress.getCurrentEOSAccount().accountName)
		launchUI {
			when (tradingType) {
				TradingType.CPU -> {
					val cpuEOSValue = "${account?.cpuWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableCPU = account?.cpuLimit?.max.orZero() - account?.cpuLimit?.used.orZero()
					setProcessUsage(cpuEOSValue, availableCPU, account?.cpuLimit?.max.orZero(), SharedValue.getCPUUnitPrice())
				}
				TradingType.NET -> {
					val netEOSValue = "${account?.netWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableNET = account?.netLimit?.max.orZero() - account?.netLimit?.used.orZero()
					// TODO 计算 NET 的租赁价格
					setProcessUsage(netEOSValue, availableNET, account?.netLimit?.max.orZero(), SharedValue.getNETUnitPrice())
				}
				TradingType.RAM -> {
					val loadingView = LoadingView(fragment.context!!)
					loadingView.show()
					val availableRAM = account?.ramQuota.orZero() - account?.ramUsed.orZero()
					// 因为这里只需显示大概价格, 并且这里需要用到两次, 所以直接取用了 `EOS` 个数买 `KB`` 并反推 `Price` 的方法减少网络请求
					val price = SharedValue.getRAMUnitPrice()
					val amountKBInEOS = 1.0 / price
					val ramEOSAccount = "≈ " + (availableRAM.toDouble() * price / 1024).formatCount(4) suffix CoinSymbol.eos
					setProcessUsage(ramEOSAccount, availableRAM, account?.ramQuota.orZero(), amountKBInEOS)
					loadingView.remove()
				}
			}
		}
	}

	private fun BaseTradingFragment.stakeResource(
		tradingType: TradingType,
		stakeType: StakeType,
		@WorkerThread callback: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		val fromAccount = SharedAddress.getCurrentEOSAccount()
		val toAccount = getInputValue(stakeType).first
		val transferCount = getInputValue(stakeType).second
		if (toAccount.isValid(false)) {
			prepareTransaction(
				context,
				transferCount,
				TokenContract.EOS
			) { privateKey, error ->
				if (error.isNone() && privateKey.isNotNull()) {
					EOSBandWidthTransaction(
						SharedChain.getEOSCurrent().chainID,
						EOSAuthorization(fromAccount.accountName, EOSActor.Active),
						toAccount.accountName,
						transferCount.toEOSUnit(),
						tradingType,
						stakeType,
						fragment.isSelectedTransfer(stakeType),
						ExpirationType.FiveMinutes
					).send(privateKey, callback)
				} else callback(null, error)
			}
		} else callback(null, AccountError.InvalidAccountName)
	}

	fun updateLocalDataAndUI() {
		val currentAccount = SharedAddress.getCurrentEOSAccount()
		EOSAPI.getAccountInfo(currentAccount) { newData, error ->
			if (newData.isNotNull() && error.isNone()) {
				// 新数据标记为老数据的 `主键` 值
				GoldStoneDataBase.database.eosAccountDao().update(newData)
				fragment.setUsageValue()
			} else fragment.safeShowError(error)
		}
	}

	companion object {

		fun sellRAM(
			context: Context,
			tradingCount: Long,
			@WorkerThread callback: (response: EOSResponse?, GoldStoneError) -> Unit
		) {
			val fromAccount = SharedAddress.getCurrentEOSAccount()
			val chainID = SharedChain.getEOSCurrent().chainID
			prepareTransaction(
				context,
				tradingCount,
				TokenContract.EOS,
				true
			) { privateKey, error ->
				if (error.isNone() && privateKey.isNotNull()) {
					EOSSellRamTransaction(
						chainID,
						fromAccount.accountName,
						BigInteger.valueOf(tradingCount),
						ExpirationType.FiveMinutes
					).send(privateKey, callback)
				} else callback(null, error)
			}
		}

		fun buyRam(
			context: Context,
			toAccount: EOSAccount,
			tradingCount: Double,
			@WorkerThread callback: (response: EOSResponse?, GoldStoneError) -> Unit
		) {
			val fromAccount = SharedAddress.getCurrentEOSAccount()
			val chainID = SharedChain.getEOSCurrent().chainID
			// 检查接收内存的用户名是否正常如果不正常就返回
			if (toAccount.isValid(false)) prepareTransaction(
				context,
				tradingCount,
				TokenContract.EOS,
				false
			) { privateKey, error ->
				if (error.isNone() && privateKey.isNotNull()) {
					EOSBuyRamTransaction(
						chainID,
						fromAccount.accountName,
						toAccount.accountName,
						tradingCount.toEOSUnit(),
						ExpirationType.FiveMinutes
					).send(privateKey, callback)
				} else callback(null, error)
			} else callback(null, AccountError.InvalidAccountName)
		}

		fun <T : Number> prepareTransaction(
			context: Context?,
			tradingCount: T,
			contract: TokenContract,
			isSellRam: Boolean = false,
			@WorkerThread hold: (privateKey: EOSPrivateKey?, error: GoldStoneError) -> Unit
		) {
			val fromAccount = SharedAddress.getCurrentEOSAccount()
			// 检出用户的输入值是否合规
			isValidInputValue(tradingCount, isSellRam) { error ->
				if (!error.isNone()) hold(null, error) else {
					// 检查余额
					if (isSellRam) EOSAPI.getAvailableRamBytes(fromAccount) { ramAvailable, ramError ->
						// 检查发起账户的 `RAM` 余额是否足够
						when {
							!ramError.isNone() -> hold(null, ramError)
							ramAvailable.isNull() -> hold(null, ramError)
							ramAvailable < BigInteger.valueOf(tradingCount.toLong()) ->
								hold(null, TransferError.BalanceIsNotEnough)
							tradingCount == 1.0 -> hold(null, TransferError.SellRAMTooLess)
							else -> PaymentDetailPresenter.showGetPrivateKeyDashboard(context, hold)
						}
					} else EOSAPI.getAccountBalanceBySymbol(
						fromAccount,
						CoinSymbol(contract.symbol),
						contract.contract
					) { balance, balanceError ->
						if (balance.isNotNull() && balanceError.isNone() && tradingCount is Double) {
							// 检查发起账户的余额是否足够
							if (balance < tradingCount) hold(null, TransferError.BalanceIsNotEnough)
							else PaymentDetailPresenter.showGetPrivateKeyDashboard(context, hold)
						} else hold(null, balanceError)
					}
				}
			}
		}

		private fun <T : Number> isValidInputValue(
			tradingNumber: T,
			isSellRam: Boolean,
			callback: (GoldStoneError) -> Unit
		) {
			System.out.println("fcuk you $tradingNumber")
			when {
				tradingNumber == 0 -> callback(TransferError.TradingInputIsEmpty)
				isSellRam && tradingNumber !is Long -> // 检查输入的卖出的 `EOS` 的值是否正确
					callback(TransferError.wrongRAMInputValue)
				!tradingNumber.toString().isValidDecimal(CryptoValue.eosDecimal) -> // 检查输入值的精度是否正确
					callback(TransferError.IncorrectDecimal)
				else -> callback(GoldStoneError.None)
			}
		}
	}
}