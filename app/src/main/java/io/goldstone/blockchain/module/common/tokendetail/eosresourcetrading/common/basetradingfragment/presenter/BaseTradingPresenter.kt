package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.isSameValueAsInt
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.isValidDecimal
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSBandWidthTransaction
import io.goldstone.blockchain.kernel.network.eos.EOSRAM.EOSBuyRamTransaction
import io.goldstone.blockchain.kernel.network.eos.EOSRAM.EOSResourceUtil
import io.goldstone.blockchain.kernel.network.eos.EOSRAM.EOSSellRamTransaction
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.BaseTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/09/18
 */
open class BaseTradingPresenter(
	override val fragment: BaseTradingFragment
) : BasePresenter<BaseTradingFragment>() {

	open fun gainConfirmEvent(callback: (GoldStoneError) -> Unit) {
		if (fragment.tradingType == TradingType.RAM)
			fragment.tradingRam(StakeType.BuyRam, callback)
		else fragment.stakeResource(fragment.tradingType, StakeType.Delegate, callback)
	}

	open fun refundOrSellConfirmEvent(callback: (GoldStoneError) -> Unit) {
		if (fragment.tradingType == TradingType.RAM)
			fragment.tradingRam(StakeType.SellRam, callback)
		else fragment.stakeResource(fragment.tradingType, StakeType.Refund, callback)
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.setUsageValue()
	}

	private fun BaseTradingFragment.setUsageValue() {
		EOSAccountTable.getAccountByName(Config.getCurrentEOSAccount().accountName) { account ->
			when (tradingType) {
				TradingType.CPU -> {
					val cpuEOSValue = "${account?.cpuWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableCPU = account?.cpuLimit?.max.orZero() - account?.cpuLimit?.used.orZero()
					// TODO 计算 CPU 的租赁价格
					setProcessUsage(cpuEOSValue, availableCPU, account?.cpuLimit?.max.orZero(), 0.0027)
				}
				TradingType.NET -> {
					val netEOSValue = "${account?.netWeight?.toEOSCount()}" suffix CoinSymbol.eos
					val availableNET = account?.netLimit?.max.orZero() - account?.netLimit?.used.orZero()
					// TODO 计算 NET 的租赁价格
					setProcessUsage(netEOSValue, availableNET, account?.netLimit?.max.orZero(), 0.0027)
				}
				TradingType.RAM -> {
					getMainActivity()?.showLoadingView()
					val availableRAM = account?.ramQuota.orZero() - account?.ramUsed.orZero()
					EOSResourceUtil.getRAMAmountByCoin(Pair(1.0, CoinSymbol.EOS), EOSUnit.KB) { amount, error ->
						// 因为这里只需显示大概价格, 并且这里需要用到两次, 所以直接取用了 `EOS` 个数买 `KB`` 并反推 `Price` 的方法减少网络请求
						if (!amount.isNull() && error.isNone()) {
							val price = 1.0 / amount!!
							val ramEOSAccount = "≈ " + (availableRAM.toDouble() * price / 1024).formatCount(4) suffix CoinSymbol.eos
							setProcessUsage(ramEOSAccount, availableRAM, account?.ramQuota.orZero(), amount)
						} else {
							context.alert(error.message)
							setProcessUsage(CommonText.calculating, availableRAM, account?.ramQuota.orZero(), 0.0)
						}
						getMainActivity()?.removeLoadingView()
					}
				}
			}
		}
	}

	private fun BaseTradingFragment.tradingRam(stakeType: StakeType, callback: (GoldStoneError) -> Unit) {
		val fromAccount = Config.getCurrentEOSAccount()
		val chainID = Config.getEOSCurrentChain()
		val toAccount = getInputValue(stakeType).first
		val tradingCount = getInputValue(stakeType).second
		prepareTransaction(
			context,
			fromAccount,
			toAccount,
			tradingCount,
			CoinSymbol.EOS,
			stakeType.isSellRam()
		) { privateKey, error ->
			/** `Buy Ram` 是可以按照 `EOS Count` 来进行购买的, 但是 `Sell` 只能按照 `Byte` 进行销售 */
			fun completeEvent(response: EOSResponse) {
				// 显示成功的 `Dialog`
				fragment.getParentContainer()?.apply { response.showDialog(this) }
				// 清空输入框里面的值
				fragment.clearInputValue()
				// 成功提示
				fragment.toast(CommonText.succeed)
				// 更新数据库数据并且更新界面的数据
				fragment.presenter.updateLocalDataAndUI()
				callback(error)
			}
			if (error.isNone() && !privateKey.isNull()) {
				if (stakeType.isBuyRam()) EOSBuyRamTransaction(
					chainID,
					fromAccount.accountName,
					toAccount.accountName,
					tradingCount.toEOSUnit(),
					ExpirationType.FiveMinutes
				).send(privateKey!!, callback) {
					completeEvent(it)
				} else EOSSellRamTransaction(
					chainID,
					fromAccount.accountName,
					BigInteger.valueOf(tradingCount.toLong()),
					ExpirationType.FiveMinutes
				).send(privateKey!!, callback) {
					completeEvent(it)
				}
			} else callback(error)
		}
	}

	private fun BaseTradingFragment.stakeResource(
		tradingType: TradingType,
		stakeType: StakeType,
		callback: (GoldStoneError) -> Unit
	) {
		val fromAccount = Config.getCurrentEOSAccount()
		val toAccount = getInputValue(stakeType).first
		val transferCount = getInputValue(stakeType).second
		prepareTransaction(
			context,
			fromAccount,
			toAccount,
			transferCount,
			CoinSymbol.EOS
		) { privateKey, error ->
			if (error.isNone() && !privateKey.isNull()) {
				EOSBandWidthTransaction(
					Config.getEOSCurrentChain(),
					EOSAuthorization(fromAccount.accountName, EOSActor.Active),
					toAccount.accountName,
					transferCount.toEOSUnit(),
					tradingType,
					stakeType,
					fragment.isSelectedTransfer(stakeType),
					ExpirationType.FiveMinutes
				).send(privateKey!!, callback) {
					// 显示成功的 `Dialog`
					fragment.getParentContainer()?.apply { it.showDialog(this) }
					// 清空输入框里面的值
					fragment.clearInputValue()
					// 更新数据库数据并且更新界面的数据
					fragment.presenter.updateLocalDataAndUI()
					callback(error)
				}
			} else callback(error)
		}
	}

	private fun updateLocalDataAndUI() {
		val currentAccount = Config.getCurrentEOSAccount()
		EOSAPI.getAccountInfo(
			currentAccount,
			{ LogUtil.error("updateLocalResourceData", it) }
		) { newData ->
			EOSAccountTable.getAccountByName(currentAccount.accountName, false) { localData ->
				localData?.let { local ->
					// 新数据标记为老数据的 `主键` 值
					GoldStoneDataBase.database.eosAccountDao().update(newData.apply { this.id = local.id })
					GoldStoneAPI.context.runOnUiThread { fragment.setUsageValue() }
				}
			}
		}
	}

	companion object {

		fun prepareTransaction(
			context: Context?,
			fromAccount: EOSAccount,
			toAccount: EOSAccount,
			tradingCount: Double,
			symbol: CoinSymbol,
			isSellRam: Boolean = false,
			@UiThread hold: (privateKey: EOSPrivateKey?, error: GoldStoneError) -> Unit
		) {
			// 检出用户的输入值是否合规
			isValidInputValue(Pair(toAccount, tradingCount), isSellRam) { error ->
				if (!error.isNone()) hold(null, error) else {
					// 检查余额
					if (isSellRam) EOSAPI.getAvailableRamBytes(
						fromAccount,
						{ hold(null, error) }
					) {
						// 检查发起账户的 `RAM` 余额是否足够
						if (it < BigInteger.valueOf(tradingCount.toLong())) hold(null, TransferError.BalanceIsNotEnough)
						else GoldStoneAPI.context.runOnUiThread {
							PaymentPreparePresenter.showGetPrivateKeyDashboard(context, hold)
						}
					} else EOSAPI.getAccountBalanceBySymbol(
						fromAccount,
						symbol,
						EOSCodeName.EOSIOToken,
						{ hold(null, it) }
					) { balance ->
						// 检查发起账户的余额是否足够
						if (balance < tradingCount) hold(null, TransferError.BalanceIsNotEnough)
						else PaymentPreparePresenter.showGetPrivateKeyDashboard(context, hold)
					}
				}
			}
		}

		private fun isValidInputValue(
			inputValue: Pair<EOSAccount, Double>,
			isSellRam: Boolean,
			callback: (GoldStoneError) -> Unit
		) {
			if (!inputValue.first.isValid()) {
				// 检查用户名是否正确
				callback(AccountError.InvalidAccountName)
			} else if (inputValue.second == 0.0) {
				callback(TransferError.TradingInputIsEmpty)
			} else if (isSellRam && !inputValue.second.isSameValueAsInt()) {
				// 检查输入的卖出的 `EOS` 的值是否正确
				callback(TransferError.wrongRAMInputValue)
			} else if (!inputValue.second.toString().isValidDecimal(CryptoValue.eosDecimal)) {
				// 检查输入值的精度是否正确
				callback(TransferError.IncorrectDecimal)
			} else callback(GoldStoneError.None)
		}
	}
}